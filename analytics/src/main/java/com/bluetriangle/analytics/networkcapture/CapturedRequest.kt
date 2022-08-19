package com.bluetriangle.analytics.networkcapture

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import java.util.HashMap

class CapturedRequest {
    /**
     * entry type. Fixed value of "resource".
     */
    var entryType = ENTRY_TYPE

    /**
     * Page domain without host.
     */
    var domain: String? = null

    /**
     * Subdomain of the fully qualified domain name.
     */
    var host: String? = null
        set(value) {
            field = value
            domain = value?.split("\\.")?.takeLast(2)?.joinToString(".")
        }

    /**
     * Full request URL
     */
    var url: String? = null

    /**
     * name of file requested
     */
    var file: String? = null

    /**
     * Request start time.
     */
    var startTime: Long = 0

    /**
     * Request end time.
     */
    var endTime: Long = 0

    /**
     * Request duration.
     */
    var duration: Long = 0

    /**
     * The type of file being returned by the request.
     */
    var requestType: RequestType? = null

    /**
     * Decompressed size of content.
     */
    var decodedBodySize: Long = 0

    /**
     * Compressed size of content
     */
    var encodedBodySize: Long = 0

    val payload: Map<String, String?>
        get() {
            val payload = HashMap<String, String?>(12)
            payload[FIELD_ENTRY_TYPE] = entryType
            payload[FIELD_DOMAIN] = domain
            payload[FIELD_HOST] = host
            payload[FIELD_URL] = url
            payload[FIELD_FILE] = file
            payload[FIELD_START_TIME] = startTime.toString()
            payload[FIELD_END_TIME] = endTime.toString()
            payload[FIELD_DURATION] = duration.toString()
            payload[FIELD_REQUEST_TYPE] = requestType!!.name
            payload[FIELD_DECODED_BODY_SIZE] = decodedBodySize.toString()
            payload[FIELD_ENCODED_BODY_SIZE] = encodedBodySize.toString()
            return payload
        }

    /**
     * start the request timer if not already started
     */
    fun start() {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
    }

    fun stop() {
        if (startTime > 0 && endTime == 0L) {
            endTime = System.currentTimeMillis()
            duration = endTime - startTime
        }
    }

    /**
     * convenience method to submit this captured request to the tracker
     */
    fun submit() {
        val tracker = Tracker.getInstance()
        tracker?.submitCapturedRequest(this)
    }

    companion object {
        private const val ENTRY_TYPE = "resource"
        const val FIELD_ENTRY_TYPE = "e"
        const val FIELD_DOMAIN = "dmn"
        const val FIELD_HOST = "h"
        const val FIELD_URL = "URL"
        const val FIELD_FILE = "f"
        const val FIELD_START_TIME = "sT"
        const val FIELD_END_TIME = "rE"
        const val FIELD_DURATION = "d"
        const val FIELD_REQUEST_TYPE = "i"
        const val FIELD_DECODED_BODY_SIZE = "dz"
        const val FIELD_ENCODED_BODY_SIZE = "ez"
    }
}