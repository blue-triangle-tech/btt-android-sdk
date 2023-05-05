package com.bluetriangle.android.demo.tests

object ANRTestFactory {

    fun getANRTests(): List<BTTTestCase> {
        return listOf(
            SleepMainThreadTest(),
            HeavyLoopTest(),
            DownloadTest(),
            DeadLockMainThreadTest()
        )
    }

}


