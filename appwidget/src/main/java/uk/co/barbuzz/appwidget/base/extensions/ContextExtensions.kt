@file:Suppress("NOTHING_TO_INLINE")

package uk.co.barbuzz.appwidget.base.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, res, duration).show()
