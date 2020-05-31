package uk.co.barbuzz.keylines.utils

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Build

object ColorUtil {
    fun getColor(
        resource: Resources, theme: Theme,
        colorRes: Int
    ): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resource.getColor(colorRes)
        } else {
            resource.getColor(colorRes, theme)
        }
    }
}