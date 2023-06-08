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

    fun getANRTest(anrTest: ANRTest): BTTTestCase {
        return when (anrTest) {
            ANRTest.SleepMainThreadTest -> SleepMainThreadTest()
            ANRTest.HeavyLoopTest -> HeavyLoopTest()
            ANRTest.DownloadTest -> DownloadTest()
            else -> DeadLockMainThreadTest()
        }
    }
}


