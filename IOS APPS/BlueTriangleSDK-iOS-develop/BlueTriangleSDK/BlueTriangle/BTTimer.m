//
//  BTTimer.m
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/6/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import "BTTimer.h"
#import "ThreadSafeMutableDictionary.h"

@import UIKit;

NSString * const kLogTag = @"BTT_TIMER";
NSString * const kExtraTimer = @"BTT_TIMER";

NSString * const kPageName = @"pageName";
NSString * const kNST = @"nst";
NSString * const kUnloadEventStart = @"unloadEventStart";
NSString * const kContentGroupName = @"pageType";
NSString * const kPageValue = @"pageValue";
NSString * const kPageTime = @"pgTm";
NSString * const kDomInteractive = @"domInteractive";
NSString * const kNavigationType = @"navigationType";
NSString * const kCartValue = @"cartValue";
NSString * const kOrderNumber = @"ONumBr";
NSString * const kOrderTime = @"orderTND";
NSString * const kEventType = @"eventType";
NSString * const kSiteID = @"siteID";
NSString * const kTrafficSegmentName = @"txnName";
NSString * const kCampaign = @"campaign";
NSString * const kTimeOnPage = @"top";
NSString * const kBrandValue = @"bv";
NSString * const kURL = @"thisURL";
NSString * const kBVZN = @"bvzn";
NSString * const kOS = @"EUOS";
NSString * const kSessionID = @"sID";
NSString * const kGlobalUserID = @"gID";
NSString * const kCustomValue4 = @"CV4";
NSString * const kRV = @"RV";
NSString * const kWCD = @"wcd";
NSString * const kABTestID = @"AB";
NSString * const kCampaignSource = @"CmpS";
NSString * const kCampaignMedium = @"CmpM";
NSString * const kCampaignName = @"CmpN";
NSString * const kDataCenter = @"DCTR";
NSString * const kReferrerURL = @"RefURL";
NSString * const kNativeOS = @"os";
NSString * const kDevice = @"device";
NSString * const kBrowser = @"browser";
NSString * const kBrowserVersion = @"browserVersion";

@interface BTTimer()
@property (nonatomic, strong) ThreadSafeMutableDictionary *timerFields;
@property (nonatomic, assign) NSInteger startTime;
@property (nonatomic, assign) NSInteger interactiveTime;
@property (nonatomic, assign) NSInteger endTime;
@end

static NSDictionary *defaultFields() {
    static dispatch_once_t defaultsPred;
    static NSDictionary *__defaultFields = nil;

    UIDevice *device = [UIDevice currentDevice];
    dispatch_once(&defaultsPred, ^{ __defaultFields = @{kBVZN: @"",
                                                        kOS: @"iOS",
                                                        kEventType: @"9",
                                                        kNavigationType: @"9",
                                                        kRV: @"0",
                                                        kCustomValue4: @"0",
                                                        kWCD: @"0",
                                                        kDataCenter: @"Default",
                                                        kABTestID: @"Default",
                                                        kBrandValue: @"0",
                                                        kTimeOnPage: @"0",
                                                        kCampaign: @"",
                                                        kCampaignName: @"",
                                                        kCampaignMedium: @"",
                                                        kCampaignSource: @"",
                                                        kReferrerURL: @"",
                                                        kURL: @"",
                                                        kCartValue: @"0",
                                                        kOrderTime: @"0",
                                                        kOrderNumber: @"",
                                                        kPageValue: @"0",
                                                        kContentGroupName: @"",
                                                        kNativeOS: [NSString stringWithFormat:@"iOS %@", [device systemVersion]],
                                                        kDevice: [[device model] isEqualToString:@"iPad"] ? @"Tablet" : @"Mobile",
                                                        kBrowser: @"Native App",
                                                        kBrowserVersion: [NSString stringWithFormat:@"Native App-%@-iOS %@",
                                                                          [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"],
                                                                          [device systemVersion]]
                                                        };
    });
    return __defaultFields;
};

@implementation BTTimer

+ (BTTimer *)timer {
    return [BTTimer new];
}

+ (BTTimer *)timerWithPageName:(NSString *)pageName trafficSegment:(NSString *)trafficSegment {
    BTTimer *timer = [BTTimer new];
    [timer.timerFields setObject:pageName forKey:kPageName];
    [timer.timerFields setObject:trafficSegment forKey:kTrafficSegmentName];

    return timer;
}

+ (BTTimer *)timerWithPageName:(NSString *)pageName trafficSegment:(NSString *)trafficSegment abTestIdentifier:(NSString *)abTestIdentifier contentGroupName:(NSString *)contentGroupName {
    BTTimer *timer = [BTTimer new];
    [timer.timerFields setObject:pageName forKey:kPageName];
    [timer.timerFields setObject:trafficSegment forKey:kTrafficSegmentName];
    [timer.timerFields setObject:abTestIdentifier forKey:kABTestID];
    [timer.timerFields setObject:contentGroupName forKey:kContentGroupName];

    return timer;
}

- (instancetype)init
{
    if (self = [super init]) {
        self.timerFields = [[ThreadSafeMutableDictionary alloc] initWithDictionary:defaultFields()];
    }
    return self;
}

- (void)start {
    if (_startTime == 0) {
        self.startTime = [self epochInMilliSeconds];
    } else {
        NSLog(@"Start time already set.");
    }
}

- (void)interactive {
    if (_startTime > 0 && _interactiveTime == 0) {
        self.interactiveTime = [self epochInMilliSeconds];
        [_timerFields setObject:[NSNumber numberWithInteger:[self epochInMilliSeconds]].stringValue forKey:kDomInteractive];
    } else if (_startTime == 0) {
        NSLog(@"Interactive time cannot be set until timer is started.");
    } else if (_interactiveTime != 0) {
        NSLog(@"Interactive time already set.");
    }
}

- (void)end {
    if (_startTime > 0 && _endTime == 00) {
        _endTime = [self epochInMilliSeconds];

        NSString *start = [NSNumber numberWithInteger:[self epochInMilliSeconds]].stringValue;
        [_timerFields setObject:start forKey:kUnloadEventStart];
        [_timerFields setObject:start forKey:kNST];
        [_timerFields setObject:[NSNumber numberWithInteger:(_endTime - _startTime)] forKey:kPageTime];

    } else if (_startTime == 0) {
        NSLog(@"Cannot end timer before it is started.");
    } else if (_endTime != 0) {
        NSLog(@"Timer already ended.");
    }
}

- (void)setPageName:(nonnull NSString *)pageName {
    [self setField:kPageName stringValue:pageName];
}

- (void)setPageValue:(double)pageValue {
    [self setField:kPageValue doubleValue:pageValue];
}

- (void)setTrafficSegmentName:(nonnull NSString *)trafficSegmentName {
    [self setField:kTrafficSegmentName stringValue:trafficSegmentName];
}

- (void)setABTestIdentifier:(nonnull NSString *)abTestIdentifier {
    [self setField:kABTestID stringValue:abTestIdentifier];
}

- (void)setContentGroupName:(nonnull NSString *)contentGroupName {
    [self setField:kContentGroupName stringValue:contentGroupName];
}

- (void)setBrandValue:(double)brandValue {
    [self setField:kBrandValue doubleValue:brandValue];
}

- (void)setCartValue:(double)cartValue {
    [self setField:kCartValue doubleValue:cartValue];
}

- (void)setOrderNumber:(nonnull NSString *)orderNumber {
    [self setField:kOrderNumber stringValue:orderNumber];
}

- (void)setOrderTime:(NSInteger)orderTime {
    [self setField:kOrderTime integerValue:orderTime];
}

- (void)setCampaignName:(nonnull NSString *)campaignName {
    [self setField:kCampaignName stringValue:campaignName];
}
- (void)setCampaignSource:(nonnull NSString *)campaignSource {
    [self setField:kCampaignSource stringValue:campaignSource];
}

- (void)setCampaignMedium:(nonnull NSString *)campaignMedium {
    [self setField:kCampaignMedium stringValue:campaignMedium];
}

- (void)setTimeOnPage:(NSInteger)timeOnPage {
    [self setField:kTimeOnPage integerValue:timeOnPage];
}

- (void)setURL:(nonnull NSString *)url {
    [self setField:kURL stringValue:url];
}

- (void)setReferrer:(nonnull NSString *)referrer {
    [self setField:kReferrerURL stringValue:referrer];
}

- (void)setFields:(NSDictionary *)fields {
    [_timerFields addEntriesFromDictionary:fields];
}

- (void)setField:(NSString *)fieldName stringValue:(NSString *)stringValue {
    [_timerFields setObject:stringValue forKey:fieldName];
}

- (void)setField:(NSString *)fieldName integerValue:(NSInteger)integerValue {
    [_timerFields setObject:[NSString stringWithFormat:@"%ld", (long)integerValue] forKey:fieldName];
}

- (void)setField:(NSString *)fieldName floatValue:(float)floatValue {
    [_timerFields setObject:[NSString stringWithFormat:@"%f", floatValue] forKey:fieldName];
}

- (void)setField:(NSString *)fieldName doubleValue:(double)doubleValue {
    [_timerFields setObject:[NSString stringWithFormat:@"%f", doubleValue] forKey:fieldName];
}

- (void)setField:(NSString *)fieldName boolValue:(BOOL)boolValue {
    [_timerFields setObject:[NSNumber numberWithBool:boolValue].stringValue forKey:fieldName];
}

- (void)clearField:(NSString *)fieldName {
    [_timerFields removeObjectForKey:fieldName];
}

- (NSDictionary *)allFields {
    return _timerFields.dictionary;
}

- (BOOL)hasEnded {
    return _startTime > 0 && _endTime > 0;
}

- (BOOL)isRunning {
    return _startTime > 0 && _endTime == 0;
}

- (BOOL)isEqual:(BTTimer *)object {
    return [_timerFields isEqualToDictionary:object.timerFields];
}

- (NSUInteger)hash {
    return _timerFields.hash;
}

- (NSInteger)epochInMilliSeconds {
    return (NSInteger)floor([[NSDate date] timeIntervalSince1970] * 1000);
}

@end
