package com.haberturm.homeworks.util

import android.content.Context

object Util {

    fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }
    fun dp2px(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5).toInt()
    }
    const val WIDTH = "WIDTH"
    const val HEIGHT = "HEIGHT"

}