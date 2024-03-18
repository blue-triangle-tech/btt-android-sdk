package com.bluetriangle.android.demo.tests

import android.widget.Button

class UIOperationsTest(val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "IO Operation"
    override val description: String
        get() = "Performs button click for $interval secs"

    var button: Button?=null

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        while(System.currentTimeMillis() - startTime <= (interval * 1000)) {
            button?.performClick()
            Thread.sleep(300)
        }
        return null
    }

}