package com.bluetriangle.android.demo.tests

import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class DownloadTest : BTTTestCase {

    var interval = 10L
    
    override val title: String
        get() = "Download on MainThread $interval Sec."
    
    override val description: String
        get() = "This test downloads 200MB file in loop until $interval Sec."
    
    override fun run():String? {

        val taskStartTime = System.currentTimeMillis()
        var totalBytesDownloded = 0
        val intervalInMillis = interval * 1000

        while(System.currentTimeMillis() - taskStartTime < intervalInMillis) {
            val connection = URL("http://ipv4.download.thinkbroadband.com/200MB.zip").openConnection()
            connection.connect()
            connection.getInputStream().readBytes()
        }
//
//        repeat{
//            let data = try? Data(contentsOf: URL("http://ipv4.download.thinkbroadband.com/200MB.zip"))
//            totalBytesDownloded += data?.count ?? 0
//        }while(Date().timeIntervalSince(taskStartTime) < interval)
        return null
    }

}