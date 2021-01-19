package com.ssongh.webview.sample.manager

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import com.ssongh.webview.sample.utils.SingletonHolder


class WebViewFileManager private constructor(private val context: Context) {
    companion object : SingletonHolder<WebViewFileManager, Context>(::WebViewFileManager)

    // 파일 업로드시 파일의 정보
    private var _filePathCallback: ValueCallback<Array<Uri>>? = null

    // 파일 다운로드시 파일의 정보
    private var _downloadFileInfo: DownloadFileInfo = DownloadFileInfo()

    var filePathCallback: ValueCallback<Array<Uri>>?
        set(value) {
            _filePathCallback = value
        }
        get() = _filePathCallback

    var downloadFileInfo: DownloadFileInfo
        set(value) {
            _downloadFileInfo = value
        }
        get() = _downloadFileInfo

    /**
     * filePathCallback 초기화
     */
    fun resetFilePathCallback() {
        if (_filePathCallback != null) {
            _filePathCallback!!.onReceiveValue(null)
            _filePathCallback = null
        }
    }

    /**
     * downloadFileInfo 초기화
     */
    fun resetDownloadFileInfo() {
        _downloadFileInfo = DownloadFileInfo()
    }

    /**
     * Manager 초기화
     */
    fun reset() {
        _filePathCallback = null
        _downloadFileInfo = DownloadFileInfo()
    }

    /**
     * 다운로드 파일의 정보
     */
    data class DownloadFileInfo(
        var url: String = "",
        var userAgent: String = "",
        var contentDisposition: String = "",
        var mimeType: String = ""
    )
}