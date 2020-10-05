//
//  ThreadSafeMutableDictionary.h
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/12/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 *  A dictionary based on NSMutableDictionary, however all operations are thread safe both for reading & writing.
 */
@interface ThreadSafeMutableDictionary : NSObject

@property (readonly) NSUInteger count;
@property (readonly, copy) NSArray *allKeys;
@property (readonly, copy) NSArray *allValues;
@property (readonly, copy) NSDictionary *dictionary;


#pragma mark - Initialization
- (instancetype)init NS_DESIGNATED_INITIALIZER;
- (instancetype)initWithDictionary:(NSDictionary *)otherDictionary;
- (nullable instancetype)initWithContentsOfURL:(NSURL *)url;
- (BOOL)writeToURL:(NSURL *)url atomically:(BOOL)atomically; // the atomically flag is ignored if url of a type that cannot be written atomically.


#pragma mark - Adding objects
- (void)addEntriesFromDictionary:(NSDictionary *)otherDictionary;
- (void)setObject:(id)obj forKeyedSubscript:(id <NSCopying>)key NS_AVAILABLE(10_8, 6_0);
- (void)setObject:(id)obj forKey:(id)aKey;


#pragma mark - Removing objects
- (void)removeAllObjects;
- (void)removeObjectForKey:(id)aKey;


#pragma mark - Indexes & Objects
- (id)objectForKey:(id)aKey;
- (id)objectForKeyedSubscript:(id)key NS_AVAILABLE(10_8, 6_0);
- (void)enumerateKeysAndObjectsUsingBlock:(void (^)(id key, id obj, BOOL *stop))block NS_AVAILABLE(10_6, 4_0);


#pragma mark - Equality
- (BOOL)isEqualToDictionary:(nullable ThreadSafeMutableDictionary *)otherDictionary;

@end

NS_ASSUME_NONNULL_END
