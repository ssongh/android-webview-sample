package com.ssongh.webview.sample.utils

/**
 * String 유틸
 */
class StringUtils {
    companion object {
        /**
         * 마지막 문자열 치환
         * string : 원문
         * toReplace : 변경할 문자
         * replacement : 변경 문자
         */
        fun replaceLast(string: String, toReplace: String, replacement: String) =
            if (string.lastIndexOf(toReplace) > -1)
                string.substring(0, string.lastIndexOf(toReplace)) + replacement + string.substring(
                    string.lastIndexOf(toReplace) + toReplace.length,
                    string.length
                )
            else
                string

    }
}