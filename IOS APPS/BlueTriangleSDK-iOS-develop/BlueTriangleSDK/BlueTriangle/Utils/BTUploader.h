//
//  BTUploader.h
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/6/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BTTimer;

@interface BTUploader : NSObject

- (instancetype)initWithURL:(NSURL *)url;
- (void)upload:(BTTimer *)timer;

@end
