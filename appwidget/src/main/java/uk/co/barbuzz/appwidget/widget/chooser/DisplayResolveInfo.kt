package uk.co.barbuzz.appwidget.widget.chooser

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class DisplayResolveInfo(
    val component: ComponentName,
    val label: CharSequence,
    val icon: Drawable
)
