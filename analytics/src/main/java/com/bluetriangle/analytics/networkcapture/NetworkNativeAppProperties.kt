package com.bluetriangle.analytics.networkcapture

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class NetworkNativeAppProperties(
    val err:String
):Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("err", err)
        return obj
    }
}
