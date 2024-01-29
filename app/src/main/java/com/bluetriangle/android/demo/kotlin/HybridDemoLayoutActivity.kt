package com.bluetriangle.android.demo.kotlin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bluetriangle.android.demo.BTTWebViewClient
import com.bluetriangle.android.demo.DemoApplication
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.TitleChromeClient
import com.bluetriangle.android.demo.databinding.ActivityHybridDemoLayoutBinding


class HybridDemoLayoutActivity : AppCompatActivity() {

    private var optionsMenu: Menu? = null

    private var binding: ActivityHybridDemoLayoutBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHybridDemoLayoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setTitle(R.string.hybrid_demo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.webView?.webViewClient = BTTWebViewClient()
        binding?.webView?.webChromeClient = TitleChromeClient(this::setTitle)
        binding?.webView?.settings?.javaScriptEnabled = true
        binding?.webView?.settings?.domStorageEnabled = true
        binding?.webView?.settings?.allowFileAccess = true
        WebView.setWebContentsDebuggingEnabled(true)

        binding?.webView?.loadUrl(DemoApplication.DEMO_WEBSITE_URL)

        onBackPressedDispatcher.addCallback {
            binding?.webView?.apply {
                if (canGoBack()) {
                    goBack()
                    if(!canGoBack()) {
                        optionsMenu?.findItem(R.id.about_menu)?.isVisible = true
                    }
                } else {
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        optionsMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if(binding?.webView?.canGoBack() == true) {
                binding?.webView?.goBack()

                if(binding?.webView?.canGoBack() == false) {
                    optionsMenu?.findItem(R.id.about_menu)?.isVisible = true
                }
            } else {
                finish()
            }
            return true
        } else if(item.itemId == R.id.about_menu) {
            binding?.webView?.loadUrl("https://trackerdemo.github.io/hybrid-demo-info/")
            item.isVisible = false
        }
        return super.onOptionsItemSelected(item)
    }
}