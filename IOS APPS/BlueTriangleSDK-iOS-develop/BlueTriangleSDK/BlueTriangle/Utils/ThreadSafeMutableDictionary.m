//
//  ThreadSafeMutableDictionary.m
//  BlueTriangle
//
//  Created by Jeremy Greenwood on 7/12/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

#import "ThreadSafeMutableDictionary.h"

NS_ASSUME_NONNULL_BEGIN

@interface ThreadSafeMutableDictionary()

@property (nonatomic, strong) NSMutableDictionary *backingDict;
@property (nonatomic) dispatch_queue_t queue;

@end

@implementation ThreadSafeMutableDictionary


#pragma mark - Initialization

- (instancetype)init {
    self = [super init];
    if (self) {
        self.backingDict = [NSMutableDictionary new];
        [self setupQueue];
    }
    return self;
}

- (instancetype)initWithDictionary:(NSDictionary *)otherDictionary {
    self = [self init];
    if (self) {
        self.backingDict = [otherDictionary mutableCopy];
        if (!self.backingDict) {
            return nil;
        }
    }
    return self;
}

- (nullable instancetype)initWithContentsOfURL:(NSURL *)url {
    self = [self init];
    if (self) {
        self.backingDict = [NSMutableDictionary dictionaryWithContentsOfURL:url];
        if (!self.backingDict) {
            return nil;
        }
    }
    return self;
}

- (void)setupQueue {
    self.queue = dispatch_queue_create("com.mobelux.MXMutableDictionary", DISPATCH_QUEUE_CONCURRENT);
}

- (BOOL)writeToURL:(NSURL *)url atomically:(BOOL)atomically {
    __block BOOL success = NO;
    dispatch_barrier_sync(self.queue, ^{
        success = [self.backingDict writeToURL:url atomically:atomically];
    });
    return success;
}


#pragma mark - Properties

- (NSUInteger)count {
    __block NSUInteger count = 0;
    dispatch_sync(self.queue, ^{
        count = [self.backingDict count];
    });
    return count;
}

- (NSArray *)allKeys {
    __block NSArray *keys;
    dispatch_sync(self.queue, ^{
        keys = [self.backingDict allKeys];
    });
    return keys;
}

- (NSArray *)allValues {
    __block NSArray *values;
    dispatch_sync(self.queue, ^{
        values = [self.backingDict allValues];
    });
    return values;
}

- (NSDictionary *)dictionary {
    __block NSDictionary *dictionary;
    dispatch_sync(self.queue, ^{
        dictionary = [self.backingDict copy];
    });
    return dictionary;
}


#pragma mark - Adding objects

- (void)addEntriesFromDictionary:(NSDictionary *)otherDictionary {
    dispatch_barrier_sync(self.queue, ^{
        [self.backingDict addEntriesFromDictionary:otherDictionary];
    });
}

- (void)setObject:(id)obj forKeyedSubscript:(id <NSCopying>)key {
    dispatch_barrier_sync(self.queue, ^{
        [self.backingDict setObject:obj forKeyedSubscript:key];
    });
}

- (void)setObject:(id)obj forKey:(id)aKey {
    dispatch_barrier_sync(self.queue, ^{
        [self.backingDict setObject:obj forKey:aKey];
    });
}


#pragma mark - Removing objects

- (void)removeAllObjects {
    dispatch_barrier_sync(self.queue, ^{
        [self.backingDict removeAllObjects];
    });
}

- (void)removeObjectForKey:(id)aKey {
    dispatch_barrier_sync(self.queue, ^{
        [self.backingDict removeObjectForKey:aKey];
    });
}


#pragma mark - Indexes & Objects

- (id)objectForKey:(id)aKey {
    __block id obj;
    dispatch_sync(self.queue, ^{
        obj = [self.backingDict objectForKey:aKey];
    });
    return obj;
}

- (id)objectForKeyedSubscript:(id)key {
    __block id obj;
    dispatch_sync(self.queue, ^{
        obj = [self.backingDict objectForKeyedSubscript:key];
    });
    return obj;
}

- (void)enumerateKeysAndObjectsUsingBlock:(void (^)(id key, id obj, BOOL *stop))block {
    if (block) {
        dispatch_barrier_sync(self.queue, ^{
            [self.backingDict enumerateKeysAndObjectsUsingBlock:block];
        });
    }
}


#pragma mark - Equality

- (BOOL)isEqualToDictionary:(nullable ThreadSafeMutableDictionary *)otherDictionary {
    return [self.dictionary isEqualToDictionary:otherDictionary.dictionary];
}


#pragma mark - Descriptions

- (NSString *)description {
    __block NSString *desc;
    dispatch_sync(self.queue, ^{
        desc = [self.backingDict description];
    });
    return desc;
}

- (NSString *)debugDescription {
    __block NSString *desc;
    dispatch_sync(self.queue, ^{
        desc = [self.backingDict debugDescription];
    });
    return desc;
}

@end

NS_ASSUME_NONNULL_END
