//
//  BTUploadOperation.m
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/13/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import "BTUploadOperation.h"
#import "BTTimer.h"

@interface BTUploadOperation()
@property (nonatomic, strong) NSURL *url;
@end

@implementation BTUploadOperation {
    BOOL finished;
    BOOL executing;
}

+ (BTUploadOperation *)operationWithTimer:(BTTimer *)timer withURL:(NSURL *)url {
    BTUploadOperation *operation = [BTUploadOperation new];
    operation.url = url;
    operation.timer = timer;

    return operation;
}

- (void)start {
    if (self.isCancelled) {
        [self willChangeValueForKey:@"isFinished"];
        finished = YES;
        [self didChangeValueForKey:@"isFinished"];

        return;
    }

    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];

    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] initWithURL:_url];
    [urlRequest setHTTPMethod:@"POST"];

    NSError *error = nil;
    NSData *data = [NSJSONSerialization dataWithJSONObject:_timer.allFields options:kNilOptions error:&error];

    if (!error) {
        __weak typeof(self) weakSelf = self;
        NSURLSessionUploadTask *uploadTask = [session uploadTaskWithRequest:urlRequest fromData:[data base64EncodedDataWithOptions: 0] completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (error) {
                weakSelf.retryCount += 1;
            }

            [weakSelf updateSuccessful:!error];
        }];

        [uploadTask resume];

        [self willChangeValueForKey:@"isExecuting"];
        executing = YES;
        [self didChangeValueForKey:@"isExecuting"];
    }
}

- (BOOL)isAsynchronous {
    return YES;
}

- (BOOL)isExecuting {
    return executing;
}

- (BOOL)isFinished {
    return finished;
}

- (void)updateSuccessful:(BOOL)success {
    [self willChangeValueForKey:@"isExecuting"];
    [self willChangeValueForKey:@"isFinished"];
    self.successful = success;

    executing = NO;
    finished = YES;
    [self didChangeValueForKey:@"isFinished"];
    [self didChangeValueForKey:@"isExecuting"];
}

@end
