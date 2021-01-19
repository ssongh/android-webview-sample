package com.ssongh.webview.sample.utils

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import com.ssongh.webview.sample.R


/**
 * 다이얼로그 유틸
 */
class DialogUtils {
    companion object {
        /**
         * showConfirm
         * context : Context
         * title : 제목 (int or String)
         * msg : 메시지 (int or String)
         * cancelable : 취소 가능 여부
         */
        fun showConfirm(
            context: Context,
            title: Any? = null,
            msg: Any? = null,
            cancelable: Boolean = true
        ) {
            try {
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        context,
                        R.style.Theme_AppCompat_Light_Dialog
                    )
                ).apply {
                    if (title != null) {
                        when (title) {
                            is Int -> setTitle(title)
                            is String -> setTitle(title)
                        }
                    }

                    if (msg != null) {
                        when (msg) {
                            is Int -> setMessage(msg)
                            is String -> setMessage(msg)
                        }
                    }

                    setCancelable(cancelable)

                    setPositiveButton(android.R.string.ok) { d, _ ->
                        d.dismiss()
                    }
                    setNegativeButton(android.R.string.cancel) { d, _ ->
                        d.dismiss()
                    }

                    show()

                }
            } catch (e: Exception) {
                L.e(e.toString())
            }
        }

        /**
         * showConfirm
         * context : Context
         * title : 제목 (int or String)
         * msg : 메시지 (int or String)
         * cancelable : 취소 가능 여부
         * positiveFun : Positive Button 함수
         * negativeFun : Negative Button 함수
         */
        fun showConfirm(
            context: Context,
            title: Any? = null,
            msg: Any? = null,
            cancelable: Boolean = true,
            positiveFun: () -> Unit = {},
            negativeFun: () -> Unit = {}
        ) {
            try {
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        context,
                        R.style.Theme_AppCompat_Light_Dialog
                    )
                ).apply {
                    if (title != null) {
                        when (title) {
                            is Int -> setTitle(title)
                            is String -> setTitle(title)
                        }
                    }

                    if (msg != null) {
                        when (msg) {
                            is Int -> setMessage(msg)
                            is String -> setMessage(msg)
                        }
                    }

                    setCancelable(cancelable)

                    setPositiveButton(android.R.string.ok) { d, _ ->
                        positiveFun()
                        d.dismiss()
                    }

                    setNegativeButton(android.R.string.cancel) { d, _ ->
                        negativeFun()
                        d.dismiss()
                    }

                    show()
                }
            } catch (e: Exception) {
                L.e(e.toString())
            }
        }

        /**
         * showAlert
         * context : Context
         * title : 제목 (int or String)
         * msg : 메시지 (int or String)
         * cancelable : 취소 가능 여부
         */
        fun showAlert(
            context: Context,
            title: Any? = null,
            msg: Any? = null,
            cancelable: Boolean = true
        ) {
            try {
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        context,
                        R.style.Theme_AppCompat_Light_Dialog
                    )
                ).apply {
                    if (title != null) {
                        when (title) {
                            is Int -> setTitle(title)
                            is String -> setTitle(title)
                        }
                    }

                    if (msg != null) {
                        when (msg) {
                            is Int -> setMessage(msg)
                            is String -> setMessage(msg)
                        }
                    }

                    setCancelable(cancelable)

                    setPositiveButton(android.R.string.ok) { d, _ ->
                        d.dismiss()
                    }

                    show()
                }
            } catch (e: java.lang.Exception) {
                L.e(e.toString())
            }
        }

        /**
         * showAlert
         * context : Context
         * title : 제목 (int or String)
         * msg : 메시지 (int or String)
         * cancelable : 취소 가능 여부
         * positiveFun : Positive Button 함수
         */
        fun showAlert(
            context: Context,
            title: Any? = null,
            msg: Any? = null,
            cancelable: Boolean = true,
            positiveFun: () -> Unit = {}
        ) {
            try {
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        context,
                        R.style.Theme_AppCompat_Light_Dialog
                    )
                ).apply {
                    if (title != null) {
                        when (title) {
                            is Int -> setTitle(title)
                            is String -> setTitle(title)
                        }
                    }

                    if (msg != null) {
                        when (msg) {
                            is Int -> setMessage(msg)
                            is String -> setMessage(msg)
                        }
                    }

                    setCancelable(cancelable)

                    setPositiveButton(android.R.string.ok) { d, _ ->
                        positiveFun()
                        d.dismiss()
                    }

                    show()
                }

            } catch (e: java.lang.Exception) {
                L.e(e.toString())
            }
        }
    }
}