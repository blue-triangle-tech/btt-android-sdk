package com.bluetriangle.android.demo.tests

class SleepMainThreadTest : BTTTestCase {

    var interval = 10L
    
    override val title: String
        get() = "Sleep MainThread $interval Sec."

    override val description: String
        get() = "This test calls Thread.sleep for $interval on main thread."
    
    override fun run():String? {
        Thread.sleep(interval * 1000)
        return null
    }

}