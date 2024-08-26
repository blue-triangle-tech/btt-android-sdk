package com.bluetriangle.analytics.utility

import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebView

internal class WebViewHelper(private val webView: WebView) {

    // Callback version
    fun evaluateJavascript(
        script: String,
        callback: (result: String?, error: String?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // For KitKat and later
            webView.evaluateJavascript(
                "try { $script } catch (e) { 'Error: ' + e.message }"
            ) { value ->
                if (value != null && value.contains("Error:")) {
                    callback(null, value)
                } else {
                    callback(value, null)
                }
            }
        } else {
            webView.addJavascriptInterface(object : JSInterface {
                @JavascriptInterface
                override fun onResult(value: String) {
                    callback(value, null)
                }

                @JavascriptInterface
                override fun onError(error: String) {
                    callback(null, error)
                }
            },"AndroidInterface")
            webView.loadUrl("javascript:try { AndroidInterface.onResult((function() { return $script; })()) } catch (e) { AndroidInterface.onError(e.message) }")
        }
    }

    // JavaScript interface to capture results or errors for pre-KitKat devices
    private interface JSInterface {
        fun onResult(value: String)

        fun onError(error: String)
    }
}
