package com.bluetriangle.android.demo.tests

import kotlin.io.path.writeBytes

class IOOperationsTest(val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "IO Operation"
    override val description: String
        get() = "Performs file writing operation for $interval secs"

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        var counter = 0u
        val file = kotlin.io.path.createTempFile("test_file")
        while(System.currentTimeMillis() - startTime <= (interval * 1000)) {
            counter++
            if(counter % 1000u == 0u) {
                file.writeBytes(title.toByteArray())
            }
        }
        return null
    }

}