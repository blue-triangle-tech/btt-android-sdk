/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.fetcher

import android.annotation.SuppressLint
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfigurationMapper
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigFetchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


internal class BTTConfigurationFetcher(private val remoteConfigUrl: String) :
    IBTTConfigurationFetcher {

    @Throws
    override suspend fun fetch(): BTTConfigFetchResult {
        Tracker.instance?.configuration?.logger?.debug("Fetching remote config from $remoteConfigUrl")
        var remoteConfigJSONString = ""
        try {
            remoteConfigJSONString = URL(remoteConfigUrl).fetchJSON()

            val remoteConfigJSON = JSONObject(remoteConfigJSONString)
            val remoteConfig = BTTRemoteConfigurationMapper.fromJson(remoteConfigJSON)

            return BTTConfigFetchResult.Success(remoteConfig)
        } catch (e: JSONException) {
            return BTTConfigFetchResult.Failure(BTTConfigFetchError.InvalidJSON("${e.message}, JSON: ${remoteConfigJSONString.substring(0, remoteConfigJSONString.length.coerceAtMost(300))}"))
        } catch (e: InvalidResponseCode) {
            return BTTConfigFetchResult.Failure(BTTConfigFetchError.ErrorResponse(e.responseCode))
        } catch (e: IOException) {
            return BTTConfigFetchResult.Failure(BTTConfigFetchError.Other(e.message ?: ""))
        } catch (e: Exception) {
            e.printStackTrace()
            return BTTConfigFetchResult.Failure(BTTConfigFetchError.Other(e.message ?: ""))
        }
    }

    private suspend fun URL.fetchJSON(): String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
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
                throw InvalidResponseCode(
                    connection.responseCode,
                    "HTTP Request failed for Remote Config: ${connection.responseCode} : ${connection.responseMessage}"
                )
            }
        } finally {
            connection?.disconnect()
        }
    }

}