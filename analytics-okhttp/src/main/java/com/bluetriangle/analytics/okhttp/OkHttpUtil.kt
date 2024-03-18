package com.bluetriangle.analytics.okhttp

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.networkcapture.RequestType
import okhttp3.MediaType
import okhttp3.OkHttpClient
import java.util.Locale

fun OkHttpClient.Builder.bttTrack():OkHttpClient.Builder {
    val configuration = Tracker.instance?.configuration
    if(configuration != null) {
        addInterceptor(BlueTriangleOkHttpInterceptor(configuration))
        eventListenerFactory(BlueTriangleOkHttpEventListenerFactory(configuration))
    }
    return this
}

fun requestTypeFromMediaType(filePathSegment: String?, mediaType: MediaType?): RequestType {
    if (mediaType != null) {
        // try content sub type first
        if (mediaType.type == "application" || mediaType.type == "text") {
            runCatching {
                return RequestType.valueOf(mediaType.subtype.lowercase(Locale.getDefault()))
            }
        }

        // try media type next
        runCatching { return RequestType.valueOf(mediaType.type) }
    }

    // try based on the URL file extension
    return RequestType.fromFilePath(filePathSegment)
}