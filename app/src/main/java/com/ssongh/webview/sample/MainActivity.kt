package com.ssongh.webview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssongh.webview.sample.base.BaseWebChromeClient
import com.ssongh.webview.sample.base.BaseWebViewClient
import com.ssongh.webview.sample.databinding.ActivityMainBinding
import com.ssongh.webview.sample.manager.WebViewPopupManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wvMain.webViewClient = BaseWebViewClient()
        binding.wvMain.webChromeClient = BaseWebChromeClient(this)
        binding.wvMain.loadUrl("https://m.inavi.com/")
    }

    override fun onBackPressed() {
        if (!WebViewPopupManager.getInstance(this).isNotEmpty()) {
            // 웹뷰 히스토리 여부 확인
            if (binding.wvMain.canGoBack()) {
                binding.wvMain.goBack()
            } else {
               super.onBackPressed()
            }
        } else {
            // 비디오 풀스크린이 떠있는지 확인
            if (WebViewPopupManager.getInstance(this).isVideoFullScreen) {
                WebViewPopupManager.getInstance(this).lastWebChromeClient().onHideCustomView()
            } else {
                WebViewPopupManager.getInstance(this).closePopup()
            }
        }
    }
}