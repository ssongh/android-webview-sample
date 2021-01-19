package com.ssongh.webview.sample.manager


import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.ssongh.webview.sample.base.BaseWebChromeClient
import com.ssongh.webview.sample.utils.CustomWebView
import com.ssongh.webview.sample.utils.SingletonHolder


class WebViewPopupManager private constructor(private val context: Context) {
    companion object : SingletonHolder<WebViewPopupManager, Context>(::WebViewPopupManager)

    // 팝업에서 사용되는 웹뷰 객체들
    private var webViewList: ArrayList<WebView> = ArrayList()

    // 팝업에서 사용되는 웹크롬클라이언트 객체들
    private var webChromeClientList: ArrayList<WebChromeClient> = ArrayList()

    // 웹뷰 비디오 풀스크린 여부
    private var _isVideoFullScreen = false
    var isVideoFullScreen
        set(value) {
            _isVideoFullScreen = value
        }
        get() = _isVideoFullScreen

    /**
     * Manager 초기화
     */
    fun reset() {
        webViewList.clear()
        webChromeClientList.clear()
        _isVideoFullScreen = false
    }

    /**
     * webViewList 의 null 또는 비어있는지 체크
     */
    fun isNotEmpty() = webViewList.isNotEmpty()


    /**
     * 팝업 웹뷰들 onResume 처리
     */
    fun resumePopupWebView() {
        if (webViewList.isNotEmpty()) {
            for (value in webViewList) {
                value.onResume()
            }
        }
    }

    /**
     * 팝업 웹뷰들 onPause 처리
     */
    fun pausePopupWebView() {
        if (webViewList.isNotEmpty()) {
            for (value in webViewList) {
                value.onPause()
            }
        }
    }

    /**
     * 팝업 웹뷰들 destroy 처리
     */
    fun destroyPopupWebView() {
        if (webViewList.isNotEmpty()) {
            for (value in webViewList) {
                value.stopLoading()
                value.removeAllViews()
                value.destroy()
            }
        }
    }

    /**
     * 팝업 생성 시
     * Manager 에 WebView, WebChromeClient 추가
     * activity : 팝업 생성하는 activity
     * webView : 팝업 WebView
     * webChromeClient : 팝업 WebChromeClient
     */
    fun addWebView(
        activity: Activity?,
        webView: CustomWebView,
        webChromeClient: BaseWebChromeClient
    ) {
        activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)?.addView(webView)

        webViewList.add(webView)
        webChromeClientList.add(webChromeClient)
    }

    /**
     * 팝업 닫기 시 호출
     * 팝업이 열려 있을 시 onBackPressed 호출 시 사용함
     * 웹 페이지가 팝업이 닫혔다는걸 알 수 있게 스크립트로 닫음
     */
    fun closePopup() {
        if (webViewList.isNotEmpty()) {
            webViewList[webViewList.lastIndex].loadUrl("javascript:self.close();")
        }
    }

    /**
     *  팝업 닫기 시 호출
     *  뷰와 리스트에서 팝업 WebView, WebChromeClient 를 제거함
     */
    fun removeWebView(activity: Activity?) {
        if (webViewList.isNotEmpty()) {
            // 뷰에서 WebView 제거
            activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
                ?.removeView(webViewList[webViewList.lastIndex])

            // WebView 객체 제거
            webViewList[webViewList.lastIndex].stopLoading()
            webViewList[webViewList.lastIndex].removeAllViews()
            webViewList[webViewList.lastIndex].destroy()

            // 리스트에서 WebView 제거
            webViewList.removeAt(webViewList.lastIndex)
        }

        // 리스트에서 WebChromeClient 제거
        if (webChromeClientList.isNotEmpty()) {
            webChromeClientList.removeAt(webChromeClientList.lastIndex)
        }
    }

    /**
     * 최상위 팝업의 WebView 반환
     */
    fun lastWebView() = webViewList[webViewList.lastIndex]

    /**
     * 최상위 팝업의 WebChromeClient 반환
     */
    fun lastWebChromeClient() = webChromeClientList[webChromeClientList.lastIndex]
}