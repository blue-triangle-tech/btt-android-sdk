package com.bluetriangle.android.demo.tests

enum class ANRTest {
    All, // allowed only with ANRTestScenario.Unknown
    SleepMainThreadTest,
    HeavyLoopTest,
    DownloadTest,
    DeadLockMainThreadTest
}

enum class ANRTestScenario {
    Unknown,
    OnApplicationCreate,
    OnActivityCreate,
    OnActivityResume,
    OnBroadCastReceived
}