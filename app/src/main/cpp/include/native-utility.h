#ifndef ANDROID_UTILS_H
#define ANDROID_UTILS_H

#include <android/log.h>

using namespace std;

#define  LOG_TAG    "NativeLibrary"

#define  LOG_VERBOSE(...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define  LOG_DEBUG(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOG_ERROR(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //ANDROID_UTILS_H