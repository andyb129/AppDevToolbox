package uk.co.barbuzz.appwidget.data.updater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.android.DaggerBroadcastReceiver
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.AppDao
import javax.inject.Inject

class PackageRemovedReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var appDao: AppDao
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var widgetUpdater: WidgetUpdater

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != Intent.ACTION_PACKAGE_REMOVED) {
            throw IllegalStateException("Unexpected receiver with action: ${intent.action}")
        }

        val pendingResult = goAsync()
        val uninstalledPackage = intent.data!!.schemeSpecificPart

        appDao.deleteApp(uninstalledPackage)
            .andThen(widgetUpdater.updateAll())
            .compose(scheduling.forCompletable())
            .subscribe {
                pendingResult.finish()
            }
    }
}
