package com.bluetriangle.analytics

import android.os.Build
import android.webkit.WebView

object BTTWebViewTracker {

    @JvmStatic
    public fun onLoadResource(view: WebView?, url: String?) {
        if (view == null || url == null) return
        val fileName = url.split("/").lastOrNull { segment -> segment.isNotEmpty() }
        if (fileName != "btt.js") return

        Tracker.instance?.configuration?.let {
            val expiration = (System.currentTimeMillis() + 1800000).toString()
            val sessionID = "{\"value\":\"${it.sessionId}\",\"expires\":\"$expiration\"}"

            val wcdOn = if(it.shouldSampleNetwork) "on" else "off"
            val wcdCollect = "{\"value\":\"$wcdOn\",\"expires\":\"$expiration\"}"
            val sdkVersion = "Android-${BuildConfig.SDK_VERSION}"

            view.setLocalStorage("BTT_X0siD", sessionID)
            view.setLocalStorage("BTT_SDK_VER", sdkVersion)
            view.setLocalStorage("BTT_WCD_Collect", wcdCollect)
            Tracker.instance?.configuration?.logger?.info("Injected session ID and SDK version in WebView: BTT_X0siD: $sessionID, BTT_SDK_VER: $sdkVersion")
        }
    }

    private fun WebView.setLocalStorage(key:String, value:String) {
        runJS("window.localStorage.setItem('$key', '$value');")
    }

    private fun WebView.runJS(js: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js, null)
        } else {
            loadUrl("javascript:$js")
        }
    }

}