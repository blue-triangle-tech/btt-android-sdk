package com.bluetriangle.android.demo.tests

class InfiniteLoop(val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "Infinite Loop"
    override val description: String
        get() = "Runs an infinite loop for $interval secs"

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        while(System.currentTimeMillis() - startTime <= (interval * 1000)) {

        }
        return null
    }

}