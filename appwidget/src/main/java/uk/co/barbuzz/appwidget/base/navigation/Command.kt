package uk.co.barbuzz.appwidget.base.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.toast

interface Command {
    fun start(activity: Activity)

    fun Intent.safeStart(activity: Activity) = activity.apply {
        try {
            startActivity(this@safeStart)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.widget_error_activity_cannot_be_launched)
        }
    }

    infix fun and(other: Command) = object : Command {
        override fun start(activity: Activity) {
            this@Command.start(activity)
            other.start(activity)
        }
    }
}
