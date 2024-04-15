package com.bluetriangle.android.demo.tests

import kotlin.random.Random

class HeavyLoopTest(var interval: Long = 10L) : BTTTestCase {

    override val title: String
        get() = "HeavyLoop $interval Sec."

    override val description: String
        get() = "This test creates an array of 20K random strings, loops thru this array many times to find numbers in these strings till $interval Sec."

    override fun run(): String? {
        task(System.currentTimeMillis())
        return null
    }

    private fun task(taskStartTime: Long) {
        val list = arrayListOf<String>()
        val intervalInMillis = interval * 1000
        while (list.size <= 20000) {
            list.add("${Random.nextDouble() * Int.MAX_VALUE}")

            if ((System.currentTimeMillis() - taskStartTime) >= intervalInMillis) return
        }

        var duplicates = 0
        for (number in list) {

            var currentDuplicate = 0
            for (n in list) {
                if (n == number) {
                    currentDuplicate += 1
                }
                if ((System.currentTimeMillis() - taskStartTime) >= intervalInMillis) return
            }

            duplicates += (currentDuplicate - 1)
            if ((System.currentTimeMillis() - taskStartTime) >= intervalInMillis) return
        }

        if ((System.currentTimeMillis() - taskStartTime) < intervalInMillis) {
            task(taskStartTime)
        }
    }

}