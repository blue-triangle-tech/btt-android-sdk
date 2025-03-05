package com.bluetriangle.android.demo.tests

import android.os.StrictMode
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DownloadTest : BTTTestCase {

    var interval = 10L

    override val title: String
        get() = "Download on MainThread $interval Sec."

    override val description: String
        get() = "This test downloads 200MB file in loop until $interval Sec."

    override fun run(): String? {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitNetwork().build())
        val taskStartTime = System.currentTimeMillis()
        var totalBytesDownloded = 0
        val intervalInMillis = interval * 1000

        while (System.currentTimeMillis() - taskStartTime < intervalInMillis) {
            val connection =
                URL("https://github.com/IsmailAloha/downloadtest/raw/refs/heads/main/dummy.txt?download=").openConnection() as HttpsURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            while (inputStream.read() != -1) {
                totalBytesDownloded++
            }
        }
        return null
    }

}