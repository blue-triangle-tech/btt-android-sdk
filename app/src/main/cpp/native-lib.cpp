#include <jni.h>
#include <algorithm>
#include <string>
#include <vector>
#include <native-utility.h>
#include <unistd.h>

static const char *const NATIVE_WRAPPER_CLASS = "com/bluetriangle/android/demo/NativeWrapper";

static jint native_addNumbers(JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}

static void native_testCrash(JNIEnv *env, jobject obj) {
    LOG_VERBOSE("Method native_testCrash Entry");
    jclass excCls = env->FindClass("java/lang/IllegalArgumentException");
    env->ThrowNew(excCls, "thrown from native code");
    LOG_VERBOSE("Method native_testCrash Exit");
}

static void native_testANR(JNIEnv *env, jobject obj) {
    LOG_VERBOSE("Method native_testANR Entry");
    sleep(10);
    LOG_VERBOSE("Method native_testANR Exit");
}

static JNINativeMethod gMethods[] = {
        {
                "nativeAddNumbers",
                "(II)I",
                (void *) native_addNumbers
        },
        {
                "nativeTestCrash",
                "()V",
                (void *) native_testCrash
        },
        {
                "nativeTestANR",
                "()V",
                (void *) native_testANR
        }
};

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_FALSE;
    }

    jclass nativeWrapperClass = env->FindClass(NATIVE_WRAPPER_CLASS);
    if (env->RegisterNatives(nativeWrapperClass, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) <
        0) {
        return JNI_FALSE;
    }

    return JNI_VERSION_1_6;
}