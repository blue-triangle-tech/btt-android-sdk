package com.bluetriangle.android.demo.tests

class DeadLockMainThreadTest : BTTTestCase {

    var interval = 10000L

    val resource: String = "Resource"

    override val title: String
        get() = "DeadLock MainThread $interval Sec."

    override val description: String
        get() = "This test calls Thread.sleep for $interval on main thread."

    override fun run(): String? {
        OtherThread().start()
        synchronized(resource) {
            println("Resource acquired: $resource")
        }
        return null
    }

    inner class OtherThread : Thread() {

        override fun run() {
            super.run()
            synchronized(resource) {
                try {
                    sleep(interval)
                } catch (e: InterruptedException) {

                }
            }
        }
    }
}