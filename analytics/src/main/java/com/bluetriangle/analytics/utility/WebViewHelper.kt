package com.bluetriangle.analytics.utility

import android.webkit.WebView

internal class WebViewHelper(private val webView: WebView) {

    fun evaluateJavascript(
        script: String,
        callback: (result: String?, error: String?) -> Unit
    ) {
        webView.evaluateJavascript(
            "try { $script } catch (e) { 'Error: ' + e.message }"
        ) { value ->
            if (value != null && value.contains("Error:")) {
                callback(null, value)
            } else {
                callback(value, null)
            }
        }
    }

}
