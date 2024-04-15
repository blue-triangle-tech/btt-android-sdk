package com.bluetriangle.android.demo.tests

import android.util.Log

class InfiniteLoopWithDelay(val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "Infinite Loop With Delay"
    override val description: String
        get() = "Runs an infinite loop for $interval secs with some delay in between"

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        var counter = 0u
        while(System.currentTimeMillis() - startTime <= (interval * 1000)) {
            counter++
            Log.d("BlueTriangle", "Counter: $counter")
            if(counter % 200u == 0u) {
                Thread.sleep(1)
            }
        }
        return null
    }

}