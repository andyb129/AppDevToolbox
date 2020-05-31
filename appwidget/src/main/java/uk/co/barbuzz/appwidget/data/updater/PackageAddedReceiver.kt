package uk.co.barbuzz.appwidget.data.updater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.android.DaggerBroadcastReceiver
import io.reactivex.Completable
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.extensions.flatten
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.App
import uk.co.barbuzz.appwidget.data.AppDao
import uk.co.barbuzz.appwidget.data.FilterDao
import javax.inject.Inject

class PackageAddedReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var filterDao: FilterDao
    @Inject lateinit var appDao: AppDao
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var widgetUpdater: WidgetUpdater

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != Intent.ACTION_PACKAGE_ADDED) {
            throw IllegalStateException("Unexpected receiver with action: ${intent.action}")
        }
        val pendingResult = goAsync()
        val installedPackage = intent.data!!.schemeSpecificPart

        filterDao.allFilters()
            .flatten()
            .filter {
                matchPackage(it.packageMatcher).test(installedPackage)
            }
            .flatMapCompletable {
                appDao.insertApp(App(installedPackage, it.packageMatcher, it.appWidgetId))
                    .andThen(updateWidget(it.appWidgetId))
            }
            .compose(scheduling.forCompletable())
            .subscribe {
                pendingResult.finish()
            }
    }

    private fun updateWidget(appWidgetId: Int): Completable {
        return Completable.fromAction {
            widgetUpdater.notifyWidgetDataChanged(appWidgetId)
        }
    }
}
