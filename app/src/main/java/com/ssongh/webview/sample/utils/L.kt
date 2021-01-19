package com.ssongh.webview.sample.utils

import android.util.Log
import com.ssongh.webview.sample.BuildConfig


/**
 * 개발 로그
 */
class L {
    companion object {
        // log tag
        private const val tag = "DLog"

        // 개발계 빌드 시 에만 로그 노출
        private val isDev = BuildConfig.DEBUG


        /**
         * debug
         * msg : 로그 메시지
         */
        @JvmStatic
        fun d(msg: String?) {
            if (isDev) {
                if (msg != null) {
                    Log.d(tag, msg)
                } else {
                    Log.d(tag, "msg = null")
                }
            }
        }

        /**
         * info
         * msg : 로그 메시지
         */
        @JvmStatic
        fun i(msg: String?) {
            if (isDev) {
                if (msg != null) {
                    Log.i(tag, msg)
                }else {
                    Log.i(tag, "msg = null")
                }
            }
        }

        /**
         * verbose
         * msg : 로그 메시지
         */
        @JvmStatic
        fun v(msg: String?) {
            if (isDev) {
                if (msg != null) {
                    Log.v(tag, msg)
                }else {
                    Log.v(tag, "msg = null")
                }
            }
        }

        /**
         * warning
         * msg : 로그 메시지
         */
        @JvmStatic
        fun w(msg: String?) {
            if (isDev) {
                Log.w(tag, "======================================================================")
                Log.w(
                    tag,
                    "${Throwable().stackTrace[1].className}[Line = ${Throwable().stackTrace[1].lineNumber}]\n"
                )
                if (msg != null) {
                    Log.w(tag, msg)
                }else {
                    Log.w(tag, "msg = null")
                }
                Log.w(tag, "======================================================================")
            }
        }

        /**
         * error
         * msg : 로그 메시지
         */
        @JvmStatic
        fun e(msg: String?) {
            if (isDev) {
                Log.e(tag, "======================================================================")
                Log.e(
                    tag,
                    "${Throwable().stackTrace[1].className}[Line = ${Throwable().stackTrace[1].lineNumber}]\n"
                )
                if (msg != null) {
                    Log.e(tag, msg)
                }else {
                    Log.e(tag, "msg = null")
                }
                Log.e(tag, "======================================================================")
            }
        }
    }
}