package com.bluetriangle.android.demo.tests

enum class ANRTest {
    SleepMainThreadTest,
    HeavyLoopTest,
    DownloadTest,
    DeadLockMainThreadTest,
    All // allowed only with ANRTestScenario.Unknown
}

enum class ANRTestScenario {
    OnApplicationCreate,
    OnActivityCreate,
    OnActivityResume,
    OnBroadCastReceived,
    Unknown
}