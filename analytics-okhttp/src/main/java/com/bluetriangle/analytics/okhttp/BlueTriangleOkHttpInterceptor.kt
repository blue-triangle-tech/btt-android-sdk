package com.bluetriangle.analytics.okhttp

import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.RequestType
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*

class BlueTriangleOkHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val capturedRequest = CapturedRequest()
        capturedRequest.url = request.url.toString()
        capturedRequest.host = request.url.host
        capturedRequest.file = request.url.pathSegments.last()

        return try {
            capturedRequest.start()
            val response: Response = chain.proceed(request)
            capturedRequest.stop()

            capturedRequest.encodedBodySize = response.body?.contentLength() ?: 0
            capturedRequest.requestType = requestTypeFromMediaType(capturedRequest.file, response.body?.contentType())

            response
        } finally {
            capturedRequest.submit()
        }
    }

    fun requestTypeFromMediaType(filePathSegment: String?, mediaType: MediaType?): RequestType {
        if (mediaType != null) {
            // try content sub type first
            if (mediaType.type == "application" || mediaType.type == "text") {
                kotlin.runCatching {
                    return RequestType.valueOf(mediaType.subtype.lowercase(Locale.getDefault()))
                }
            }

            // try media type next
            kotlin.runCatching { return RequestType.valueOf(mediaType.type) }
        }

        // try based on the URL file extension
        return RequestType.fromFilePath(filePathSegment)
    }
}