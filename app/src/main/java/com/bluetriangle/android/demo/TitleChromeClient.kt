package com.bluetriangle.android.demo

import android.webkit.WebChromeClient
import android.webkit.WebView

class TitleChromeClient(val setTitle:(String?)->Unit): WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        setTitle(title)
    }
}