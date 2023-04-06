package com.template.sample

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebViewClient
import android.widget.Toast
import com.template.sample.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        url = intent.getStringExtra("url").toString()
        setupWebView(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.apply {
            binding.webView.webViewClient = WebViewClient()
            webView.apply {
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    cacheMode
                    allowContentAccess = true
                    userAgentString
                    setSupportMultipleWindows(true)
                    domStorageEnabled = true
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    settings.safeBrowsingEnabled = true
                }
            }
            webView.loadUrl(url)
        }
    }

    override fun onBackPressed() {
        if (!getInternetStatus(this)) {
            Toast.makeText(this, internetStatusMessage, Toast.LENGTH_SHORT).show()
        }
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            CookieManager.getInstance().flush()
        } else {
            CookieManager.getInstance().flush()
        }
    }

    override fun onStop() {
        getInternetStatus(this)
        CookieManager.getInstance().flush()
        super.onStop()
    }

    override fun onResume() {
        if (!getInternetStatus(this)) {
            Toast.makeText(this, internetStatusMessage, Toast.LENGTH_SHORT).show()
        }
        super.onResume()
        CookieManager.getInstance().flush()
    }


}
