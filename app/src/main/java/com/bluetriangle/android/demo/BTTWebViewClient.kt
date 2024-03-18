package com.bluetriangle.android.demo

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bluetriangle.analytics.hybrid.BTTWebViewTracker

class BTTWebViewClient(val onUrlChange: (String?) -> Unit = {}) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onUrlChange(url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        BTTWebViewTracker.onLoadResource(view, url)
    }

}