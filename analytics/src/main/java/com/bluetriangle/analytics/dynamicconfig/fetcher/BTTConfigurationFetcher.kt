package com.bluetriangle.analytics.dynamicconfig.fetcher

import android.util.Log
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
        val remoteConfigJSON = URL(remoteConfigUrl).fetchJSON()
        return BTTRemoteConfiguration.fromJson(JSONObject(remoteConfigJSON))
    }

    private suspend fun URL.fetchJSON():String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (openConnection() as HttpsURLConnection).apply {
                trustAllHosts()
                hostnameVerifier = DO_NOT_VERIFY
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


    private val DO_NOT_VERIFY: HostnameVerifier =
        HostnameVerifier { _, _ -> true }

    private fun trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }
        })

        // Install the all-trusting trust manager
        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection
                .setDefaultSSLSocketFactory(sc.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}