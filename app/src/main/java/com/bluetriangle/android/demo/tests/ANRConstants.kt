package com.bluetriangle.android.demo.tests

enum class ANRTest {
    SleepMainThreadTest,
    HeavyLoopTest,
    DownloadTest,
    DeadLockMainThreadTest,
    Unknown
}

enum class ANRTestScenario {
    OnActivityCreate,
    OnActivityResume,
    OnBroadCastReceived,
    OnApplicationCreate,
    Unknown
}