package com.ssongh.webview.sample

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssongh.webview.sample.base.BaseWebChromeClient
import com.ssongh.webview.sample.base.BaseWebViewClient
import com.ssongh.webview.sample.databinding.ActivityMainBinding
import com.ssongh.webview.sample.manager.WebViewPopupManager
import com.ssongh.webview.sample.utils.L

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wvMain.webViewClient = BaseWebViewClient()
        binding.wvMain.webChromeClient = BaseWebChromeClient(this)
        binding.wvMain.loadUrl("file:///android_asset/swiper_sample.html")
//        binding.wvMain.loadUrl("https://ssongh.cafe24.com/sh/swiper_sample.html")
//        binding.wvMain.loadUrl("http://www.naver.com")
    }

    override fun onResume() {
        super.onResume()
        binding.wvMain.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.wvMain.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.wvMain.stopLoading()
        binding.wvMain.removeAllViews()
        binding.wvMain.destroy()
    }

    override fun onBackPressed() {
        if (!WebViewPopupManager.getInstance(this).isNotEmpty()) {
            // 웹뷰 히스토리 여부 확인
            if (binding.wvMain.canGoBack()) {
                L.d("goBack()")
                binding.wvMain.goBack()
            } else {
                L.d("onBackPressed()")
                super.onBackPressed()
            }
        } else {
            WebViewPopupManager.getInstance(this).closePopup()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}