//
//  BTUploader.m
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/6/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import "BTUploader.h"
#import "BTTimer.h"
#import "BTUploadOperation.h"

@interface BTUploader()
@property (nonatomic, strong) NSOperationQueue *queue;
@property (nonatomic, strong) NSMutableSet<BTUploadOperation *> *operationSet;
@property (nonatomic, strong) NSURL *url;
@end

@implementation BTUploader

- (instancetype)initWithURL:(NSURL *)url {
    if (self = [super init]) {
        self.url = url;

        self.queue = [NSOperationQueue new];
        _queue.maxConcurrentOperationCount = 1;

        self.operationSet = [NSMutableSet new];
    }

    return self;
}

- (void)upload:(BTTimer *)timer {
    BTUploadOperation *operation = [self operationForTimer:timer];

    if (!operation) {
        operation = [BTUploadOperation operationWithTimer:timer withURL:_url];

        __weak typeof(BTUploadOperation) *weakOperation = operation;
        __weak typeof(self) weakSelf = self;
        [operation setCompletionBlock:^{
            if (!weakOperation.wasSuccessful && weakOperation.retryCount < 3) {
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)((weakOperation.retryCount * 10.0) * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [weakSelf startOperation:weakOperation];
                });
            }
        }];
    }

    [self startOperation:operation];
}

- (void)startOperation:(BTUploadOperation *)operation {
    [_queue addOperation:operation];
}

- (nullable BTUploadOperation *)operationForTimer:(BTTimer *)timer {
    __block BTUploadOperation *operation = nil;
    [_operationSet enumerateObjectsUsingBlock:^(BTUploadOperation * _Nonnull obj, BOOL * _Nonnull stop) {
        if ([obj.timer isEqual:timer]) {
            operation = obj;
            *stop = YES;
        }
    }];

    return operation;
}

@end
