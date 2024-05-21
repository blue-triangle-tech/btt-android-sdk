package com.bluetriangle.analytics.networkcapture

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class NetworkNativeAppProperties(
    var err:String?,
    var netState: String?
):Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("err", err)
        obj.put(CapturedRequest.FIELD_NETWORK_STATE, netState)
        return obj
    }
}
