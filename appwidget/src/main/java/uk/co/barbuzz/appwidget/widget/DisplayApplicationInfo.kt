package uk.co.barbuzz.appwidget.widget

import android.graphics.drawable.Drawable
import android.os.UserHandle

data class DisplayApplicationInfo(
    val label: CharSequence,
    val packageName: String,
    val icon: Drawable,
    val user: UserHandle,
    val version: String
)
