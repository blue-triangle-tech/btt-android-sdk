package com.bluetriangle.android.demo

class NativeWrapper {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    fun addNumbers(a: Int, b: Int): Int {
        return nativeAddNumbers(a, b)
    }

    fun testCrash() {
        return nativeTestCrash()
    }

    fun testANR() {
        return nativeTestANR()
    }

    private external fun nativeAddNumbers(a: Int, b: Int): Int
    private external fun nativeTestCrash()
    private external fun nativeTestANR()
}