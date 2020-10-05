//
//  BTTracker.m
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/6/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import "BTTracker.h"
#import "BTTimer.h"
#import "BTUploader.h"
#import "ThreadSafeMutableDictionary.h"

@interface BTTracker()
@property (nonatomic, strong) ThreadSafeMutableDictionary *globalFields;
@property (nonatomic, strong) BTUploader *uploader;
@end

NSString * const kGlobalUserIDUserDefault = @"com.bluetriangle.kGlobalUserIDUserDefault";

@implementation BTTracker
+ (instancetype)sharedTracker {
    static dispatch_once_t onceQueue;
    static BTTracker *tracker = nil;

    dispatch_once(&onceQueue, ^{ tracker = [[self alloc] init]; });
    return tracker;
}

- (instancetype)init {
    if (self = [super init]) {
        self.globalFields = [ThreadSafeMutableDictionary new];
        self.uploader = [[BTUploader alloc] initWithURL:[NSURL URLWithString:@"https://d.btttag.com/btt.gif"]];

        [self setSessionID:[self randomID]];
        [self setGlobalUserID:[self getOrCreatGlobalUserID]];
    }
    return self;
}

- (void)submitTimer:(BTTimer *)timer {
    if (!timer.hasEnded) {
        [timer end];
    }

    [timer setFields:_globalFields.dictionary];
    [_uploader upload:timer];
}

- (void)setSessionID:(NSString *)sessionID {
    [self setGlobalField:kSessionID stringValue:sessionID];
}

- (void)setGlobalUserID:(NSString *)globalUserID {
    [self setGlobalField:kGlobalUserID stringValue:globalUserID];
}

- (void)setSiteID:(NSString *)siteID {
    [_globalFields setObject:siteID forKey:kSiteID];
}

- (void)setGlobalField:(NSString *)fieldName stringValue:(NSString *)stringValue {
    [_globalFields setObject:stringValue forKey:fieldName];
}

- (void)setGlobalField:(NSString *)fieldName integerValue:(NSInteger)integerValue {
    [_globalFields setObject:[NSString stringWithFormat:@"%ld", (long)integerValue] forKey:fieldName];
}

- (void)setGlobalField:(NSString *)fieldName floatValue:(float)floatValue {
    [_globalFields setObject:[NSString stringWithFormat:@"%f", floatValue] forKey:fieldName];
}

- (void)setGlobalField:(NSString *)fieldName doubleValue:(double)doubleValue {
    [_globalFields setObject:[NSString stringWithFormat:@"%f", doubleValue] forKey:fieldName];
}

- (void)setGlobalField:(NSString *)fieldName boolValue:(BOOL)boolValue {
    [_globalFields setObject:[NSNumber numberWithBool:boolValue].stringValue forKey:fieldName];
}

- (void)clearGlobalField:(NSString *)fieldName {
    [_globalFields removeObjectForKey:fieldName];
}

- (NSDictionary *)allGlobalFields {
    return _globalFields.dictionary;
}

- (NSString *)getOrCreatGlobalUserID {
    NSString *globalUserID = [[NSUserDefaults standardUserDefaults] objectForKey:kGlobalUserIDUserDefault];

    if (!globalUserID) {
        globalUserID = [self randomID];
        [[NSUserDefaults standardUserDefaults] setObject:globalUserID forKey:kGlobalUserIDUserDefault];
    }

    return globalUserID;
}

- (NSString *)randomID {
    NSInteger (^randomInt)(void) = ^NSInteger() {
        return (arc4random() % 1000) + 100;
    };

    NSString *randomID = [NSString stringWithFormat:@"%ld%ld%ld%ld%ld%ld", (long)randomInt(), (long)randomInt(), (long)randomInt(), (long)randomInt(), (long)randomInt(), (long)randomInt()];
    return [randomID substringToIndex:17];
}

@end
