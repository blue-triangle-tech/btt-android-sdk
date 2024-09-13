package com.bluetriangle.analytics.dynamicconfig.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URL
import java.util.Locale
import javax.net.ssl.HttpsURLConnection


@RunWith(RobolectricTestRunner::class)
class BTTConfigurationFetcherTest {

    companion object {
        private const val HOST: String = "https://localhost:3000/"
        private const val MOCK_RESPONSE: String = "{\n" +
                "  \"errorSamplePercent\": 0,\n" +
                "  \"wcdSamplePercent\": %d,\n" +
                "  \"sessionDuration\": null\n" +
                "}"

    }
    private lateinit var fetcher: IBTTConfigurationFetcher

    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(Exception::class)
    fun setUp() {
        // Generate a self-signed certificate using OkHttp's HeldCertificate
        val heldCertificate: HeldCertificate = HeldCertificate.Builder()
            .commonName("localhost")
            .build()

        // Create SSL configuration for MockWebServer
        val serverCertificates: HandshakeCertificates = HandshakeCertificates.Builder()
            .heldCertificate(heldCertificate)
            .build()

        // Start the MockWebServer with HTTPS
        mockWebServer = MockWebServer()
        mockWebServer.useHttps(
            serverCertificates.sslSocketFactory(),
            false
        ) // Set MockWebServer to use HTTPS
        mockWebServer.start()

        // Set up client-side certificates to trust MockWebServer's certificate
        val clientCertificates: HandshakeCertificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(heldCertificate.certificate)
            .build()

        // Trust the mock server's certificate by configuring HttpsURLConnection
        HttpsURLConnection.setDefaultSSLSocketFactory(clientCertificates.sslSocketFactory())

        // Build the ApiClient with the mock server's HTTPS URL
        val mockUrl = mockWebServer.url("/config.js?siteID=sdkdemo26621z").toString()

        fetcher = BTTConfigurationFetcher(mockUrl)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        mockWebServer.shutdown()
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
            mockWebServer.enqueue(MockResponse().setBody(String.format(Locale.ENGLISH, MOCK_RESPONSE, 70)))

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