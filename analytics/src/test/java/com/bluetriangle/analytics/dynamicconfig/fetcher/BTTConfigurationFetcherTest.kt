package com.bluetriangle.analytics.dynamicconfig.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.IOException
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URL
import javax.net.ssl.HttpsURLConnection


@RunWith(RobolectricTestRunner::class)
class BTTConfigurationFetcherTest {

    companion object {
        private const val HOST: String = "https://localhost:3000/"
    }
    private lateinit var fetcher: IBTTConfigurationFetcher

    @Throws(IOException::class)
    @Before
    fun setUp() {
        fetcher = BTTConfigurationFetcher(HOST + "config.js?siteID=sdkdemo26621z")
    }

    class Config(
        val wcdSamplePercent: Int = 0,
        val errorSamplePercent: Int = 0,
        val sessionDuration: Int? = null
    )

    @Test(timeout = 5000)
    @Throws
    fun whenCalledFetch_shouldReturnConfigurationObject() {
        runBlocking {
            setConfig(Config(wcdSamplePercent = 70))

            val config = fetcher.fetch()
            assertTrue(
                "Didn't get correct sample rate - expected: 0.7, actual: ${config.networkSampleRate}",
                config.networkSampleRate == 0.7
            )
        }
    }

    private suspend fun setConfig(config: Config) = withContext(Dispatchers.IO){
        val connection = (URL(HOST + "updateConfig").openConnection() as HttpsURLConnection).apply {
            setHostnameVerifier { _, _ -> true }
            requestMethod = "POST"
            connectTimeout = 5000
            readTimeout = 5000
            connect()
        }
        val jsonObject = JSONObject()
        jsonObject.put("wcdSamplePercent", config.wcdSamplePercent)
        jsonObject.put("errorSamplePercent", config.errorSamplePercent)
        jsonObject.put("sessionDuration", config.sessionDuration)
        connection.outputStream.write(jsonObject.toString().toByteArray())
        connection.outputStream.flush()
        connection.disconnect()
    }

}