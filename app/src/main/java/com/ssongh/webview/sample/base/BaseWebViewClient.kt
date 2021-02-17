package com.ssongh.webview.sample.base

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.ssongh.webview.sample.R
import com.ssongh.webview.sample.utils.DialogUtils
import com.ssongh.webview.sample.utils.L
import java.lang.Error
import java.lang.Exception
import kotlin.jvm.Throws


class BaseWebViewClient : WebViewClient() {
    private var isErrorDialog = false

    companion object {
        private var networkState = false

        var isNetworkState
            set(value) {
                networkState = value
            }
            get() = networkState
    }

    /**
     * 페이지 로드 시작 시
     */
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        L.d("onPageStarted = $url")
    }

    /**
     * 페이지 로드 완료 시
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        L.d("onPageFinished = $url")
        CookieManager.getInstance().flush()
    }

    /**
     * 웹뷰 브릿지
     * api level 24 이상에서 사용
     */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (view != null && request != null) {
            if (request.url != null) {
                return overrideUrlLoading(view, request.url.toString())
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    /**
     * 웹뷰 브릿지
     * api level 24 미만에서 사용
     */
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (view != null && !url.isNullOrEmpty()) {
            return overrideUrlLoading(view, url)
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    /**
     * 웹뷰 브릿지 처리
     */
    private fun overrideUrlLoading(view: WebView, url: String): Boolean {
        L.d("overrideUrlLoading = $url")

        val uri: Uri? = Uri.parse(url)

//        val scheme = uri?.scheme ?: "unknown"
//        val host = uri?.host ?: "unknown"

        if (url.startsWith("intent://")) {
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            val existPackage = view.context?.packageManager?.getLaunchIntentForPackage(
                intent.`package` ?: ""
            )

            if (existPackage != null) {
                try {
                    view.context?.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    L.e(e.toString())
                }
            }

        } else if (url.startsWith("market://")) {
            if (uri != null) {
                view.context?.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        } else if (url.startsWith("mailto:") ||
            url.startsWith("sms:") || url.startsWith("smsto:") ||
            url.startsWith("mms:") || url.startsWith("mmsto:")
        ) { // 메일, 문자
            if (uri != null) {
                view.context?.startActivity(Intent(Intent.ACTION_SENDTO, uri))
            }
        } else if (url.startsWith("tel:")) {    // 전화
            if (uri != null) {
                view.context?.startActivity(Intent(Intent.ACTION_DIAL, uri))
            }
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            if (URLUtil.isValidUrl(url)) {
                if (url.contains("play.google.com/store/apps/details")) {
                    val id = uri?.getQueryParameter("id") ?: ""
                    if (id.isNotEmpty()) {
                        view.context?.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$id")
                            )
                        )
                    }
                } else {
                    return false
                }
            }
        }
        return true
    }

    /**
     * SSL 에러처리
     * 처리가 없을 시 스토어에서 앱이 삭제될 수 있음.
     */
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        val title = R.string.ssl_default_title
        val msg = when (error?.primaryError) {
            SslError.SSL_UNTRUSTED -> R.string.ssl_untrusted_msg

            SslError.SSL_EXPIRED -> R.string.ssl_expired_msg

            SslError.SSL_IDMISMATCH -> R.string.ssl_idmismatch_msg

            SslError.SSL_NOTYETVALID -> R.string.ssl_notyetvalid_msg

            else -> R.string.ssl_default_msg
        }

        view?.context?.let {
            DialogUtils.showConfirm(
                context = it,
                title = title,
                msg = msg,
                cancelable = false,
                positiveFun = { handler?.proceed() },
                negativeFun = { handler?.cancel() }
            )
        }
    }

    /**
     * 웹뷰 에러 수신
     * api level 23 이상 에서 사용
     */
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            error?.errorCode?.let { doReceivedError(view, it) }
        }
    }

    /**
     * 웹뷰 에러 수신
     * api level 23 미만 에서 사용
     */
    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        doReceivedError(view, errorCode)
    }

    /**
     * 웹뷰 에러 처리
     */
    private fun doReceivedError(view: WebView?, errorCode: Int) {
        view?.context?.run {
            when (errorCode) {
                ERROR_BAD_URL, ERROR_CONNECT, ERROR_TIMEOUT, ERROR_HOST_LOOKUP -> {
                    L.i("errorProcess::$errorCode")
                    view.loadUrl("about:blank")

                    if (!isErrorDialog) {
                        isErrorDialog = true

                        DialogUtils.showConfirm(
                            context = this,
                            msg = R.string.msg_err_webview,
                            cancelable = false,
                            positiveFun = {
                                isErrorDialog = false
                                view.reload()
                            },
                            negativeFun = {
                                isErrorDialog = false
                            })
                    }
                }
                else -> {
                    L.i("errorProcess_else::$errorCode")
                }
            }
        }
    }
}