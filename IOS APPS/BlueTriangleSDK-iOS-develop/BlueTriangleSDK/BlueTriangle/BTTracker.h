//
//  BTTracker.h
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/6/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BTTimer;

/*!
    @header BTTracker
    @abstract An object responsible for submitting BTTimer instances to Blue Triangle for processing.
 */
@interface BTTracker : NSObject

/*!
    Singleton instance of the tracker
 */
+ (instancetype)sharedTracker;

/*!
    Submits a timer to Blue triangle for processing
    @param timer The BTTimer instance which represents a tracked session.
 */
- (void)submitTimer:(BTTimer *)timer;

/*!
    Set the session ID for this tracker
 */
- (void)setSessionID:(NSString *)sessionID;

/*!
    Set the global user ID for this tracker
 */
- (void)setGlobalUserID:(NSString *)globalUserID;

/*!
    Set the Site ID for this tracker
 */
- (void)setSiteID:(NSString *)siteID;

/*!
    Set a global field to be applied to all trackers
 */
- (void)setGlobalField:(NSString *)fieldName stringValue:(NSString *)stringValue;

/*!
    Set a global field to be applied to all trackers
 */
- (void)setGlobalField:(NSString *)fieldName integerValue:(NSInteger)integerValue;

/*!
    Set a global field to be applied to all trackers
 */
- (void)setGlobalField:(NSString *)fieldName floatValue:(float)floatValue;

/*!
    Set a global field to be applied to all trackers
 */
- (void)setGlobalField:(NSString *)fieldName doubleValue:(double)doubleValue;

/*!
    Set a global field to be applied to all trackers
 */
- (void)setGlobalField:(NSString *)fieldName boolValue:(BOOL)boolValue;

/*!
    Set a global field to be applied to all trackers
 */
- (void)clearGlobalField:(NSString *)fieldName;

/*!
 Get all the fields currently associated with this timer.
 @return Returns a Dictionary with all the fields currently.
 */
- (NSDictionary *)allGlobalFields;

@end
