package uk.co.barbuzz.appwidget.data.updater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED
import dagger.android.DaggerBroadcastReceiver
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import javax.inject.Inject

class PowerSaveChangedReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var widgetUpdater: WidgetUpdater

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_POWER_SAVE_MODE_CHANGED) {
            throw IllegalStateException("Unexpected receiver with action: ${intent.action}")
        }

        val pendingResult = goAsync()

        widgetUpdater.updateAll()
            .compose(scheduling.forCompletable())
            .subscribe {
                pendingResult.finish()
            }
    }
}
