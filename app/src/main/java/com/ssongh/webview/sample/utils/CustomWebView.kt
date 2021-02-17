package com.ssongh.webview.sample.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.AttributeSet
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import com.ssongh.webview.sample.BuildConfig
import com.ssongh.webview.sample.R
import com.ssongh.webview.sample.`object`.ReqCodeObject
import com.ssongh.webview.sample.manager.WebViewFileManager

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Default 웹뷰
 */
class CustomWebView : WebView {
    private var downloadID: ArrayList<Long?> = ArrayList()

    init {
        webSettings()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 웹뷰 설정
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun webSettings() {
        this.settings.run {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            textZoom = 100


            // 웹뷰 내 핀치 줌
            builtInZoomControls = true
            setSupportZoom(true)
            displayZoomControls = true

            setSupportMultipleWindows(true)

            // 롤리팝 이상
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Mixed Content 항상 허용
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            L.d(userAgentString)

        }

        // 킷캣 이상
        // 개발계, 웹 디버깅 허용
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true)
        }


        // 롤리팝 이상
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 쿠키 설정
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)
        }

        // 파일 다운로드 리스너 등록
        setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when ((this.context as Activity).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    PackageManager.PERMISSION_GRANTED -> {
                        // 허용된 권한 있음
                        onDownloadStart(url, userAgent, contentDisposition, mimeType)
                    }

                    else -> {
                        // 허용된 권한 없음
                        WebViewFileManager.getInstance(context).downloadFileInfo.run {
                            this.url = url
                            this.userAgent = userAgent
                            this.contentDisposition = contentDisposition
                            this.mimeType = mimeType
                        }

                        DialogUtils.showAlert(context = context,
                            msg = R.string.msg_permission_file_download,
                            cancelable = false,
                            positiveFun = {
                                (this.context as Activity).requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    ReqCodeObject.REQ_CODE_PERMISSION_WEB_FILE_DOWNLOAD
                                )
                            })
                    }
                }
            } else {
                // M 이하 버전
                onDownloadStart(url, userAgent, contentDisposition, mimeType)

            }
        }
    }

    /**
     * 파일 다운로드 시작
     */
    @SuppressLint("SimpleDateFormat")
    fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String
    ) {
        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                val fileName: String

                val splitFileName: List<String> = contentDisposition.split("filename=")

                fileName = try {
                    if (splitFileName.isNotEmpty()) {
                        if (splitFileName.size >= 2) {
                            StringUtils.replaceLast(splitFileName[1], "\"", "")
                                .replaceFirst("\"", "")
                                .trim()
                        } else {
                            URLUtil.guessFileName(url, contentDisposition, mimeType)
                                .replace(";", "")
                        }
                    } else {
                        URLUtil.guessFileName(url, contentDisposition, mimeType).replace(";", "")
                    }
                } catch (e: Exception) {
                    // 에러시 파일네임 download현재시간(ex: download20200320130501)
                    "download${SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)}"
                }

                setMimeType(mimeType)
                val cookies = CookieManager.getInstance().getCookie(url)
                addRequestHeader("cookie", cookies)
                addRequestHeader("User-Agent", userAgent)
                setDescription("Downloading file...")
                setTitle(fileName.replace(";", ""))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName.replace(";", "")
                )
            }

            val dm =
                (this.context as Activity).getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
            downloadID.add(dm?.enqueue(request))

        } catch (e: Exception) {
            L.e(e.toString())
        }
    }

    /**
     * 파일 다운로드 리시버 등록
     */
    fun registerReceiver() {
        (this.context as Activity).registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    /**
     * 파일 다운로드 리시버 해제
     */
    fun unregisterReceiver() {
        downloadID.clear()
        (this.context as Activity).unregisterReceiver(onDownloadComplete)
    }

    /**
     * 파일 다운로드 리시버
     * 파일 다운로드의 완료, 실패 여부를 반환
     */
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    val query = DownloadManager.Query()
                        .setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                    val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status: Int =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                for (value in downloadID) {
                                    if (value != null) {
                                        if (value == id) {
                                            Toast.makeText(
                                                context,
                                                R.string.msg_file_download_success,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.msg_file_download_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                L.e(e.toString())
            }
        }
    }
}