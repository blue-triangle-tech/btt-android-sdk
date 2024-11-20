/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.fetcher

import android.annotation.SuppressLint
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


internal class BTTConfigurationFetcher(private val remoteConfigUrl:String):IBTTConfigurationFetcher {

    @Throws
    override suspend fun fetch(): BTTRemoteConfiguration {
        Tracker.instance?.configuration?.logger?.debug("Fetching remote config from $remoteConfigUrl")
        val remoteConfigJSON = URL(remoteConfigUrl).fetchJSON()
        Tracker.instance?.configuration?.logger?.debug("Fetched remote config: $remoteConfigJSON")
        return BTTRemoteConfiguration.fromJson(JSONObject(remoteConfigJSON))
    }

    private suspend fun URL.fetchJSON():String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            trustAllCertificates()
            connection = (openConnection() as HttpsURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                connect()
            }

            // Check for success response code
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { reader ->
                    return@withContext reader.readText()
                }
            } else {
                throw IOException("HTTP Request failed for Remote Config: ${connection.responseCode} : ${connection.responseMessage}")
            }
        } finally {
            connection?.disconnect()
        }
    }

    private fun trustAllCertificates() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}