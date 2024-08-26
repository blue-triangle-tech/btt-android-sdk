package com.bluetriangle.analytics

import android.webkit.WebView
import com.bluetriangle.analytics.hybrid.BTTWebViewTracker

object BTTWebViewTracker {

    @JvmStatic
    public fun onLoadResource(view: WebView?, url: String?) {
        BTTWebViewTracker.onLoadResource(view, url)
    }

}