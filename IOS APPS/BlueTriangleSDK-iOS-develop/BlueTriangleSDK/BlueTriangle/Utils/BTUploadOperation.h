//
//  BTUploadOperation.h
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/13/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BTTimer;

@interface BTUploadOperation : NSOperation

@property (nonatomic, strong) BTTimer *timer;
@property (nonatomic, assign) NSInteger retryCount;
@property (nonatomic, assign, getter = wasSuccessful) BOOL successful;

+ (BTUploadOperation *)operationWithTimer:(BTTimer *)timer withURL:(NSURL *)url;

@end
