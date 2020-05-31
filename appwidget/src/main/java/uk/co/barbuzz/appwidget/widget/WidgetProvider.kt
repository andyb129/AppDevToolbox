package uk.co.barbuzz.appwidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.extensions.flatten
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Widget
import uk.co.barbuzz.appwidget.data.WidgetDao
import uk.co.barbuzz.appwidget.data.updater.ShouldStartWidgetRefreshService
import uk.co.barbuzz.appwidget.data.updater.WidgetRefreshService
import javax.inject.Inject

internal class WidgetProvider : AppWidgetProvider() {

    @Inject lateinit var widgetDao: WidgetDao
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var widgetUpdater: WidgetUpdater
    @Inject lateinit var shouldStartWidgetRefreshService: ShouldStartWidgetRefreshService

    private val disposables = CompositeDisposable()

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if (SDK_INT >= O && shouldStartWidgetRefreshService.check()) {
            context.startForegroundService(Intent(context, WidgetRefreshService::class.java))
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, vararg appWidgetIds: Int) {
        if (appWidgetIds.isEmpty()) return

        val pendingResult = goAsync()

        disposables.clear()
        disposables.add(widgetDao.findWidgetsById(appWidgetIds)
            .flatten()
            .flatMapCompletable { (appWidgetId, widgetName) ->
                widgetUpdater.update(appWidgetId, widgetName)
            }
            .compose(scheduling.forCompletable())
            .subscribe(pendingResult::finish))
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        val pendingResult = goAsync()

        disposables.clear()
        disposables.add(widgetDao.findWidgetById(appWidgetId)
            .flatMapCompletable { (_, widgetName) ->
                widgetUpdater.update(appWidgetId, widgetName, newOptions)
            }
            .compose(scheduling.forCompletable())
            .subscribe(pendingResult::finish))
    }

    override fun onDeleted(context: Context, vararg appWidgetIds: Int) {
        val widgets = appWidgetIds.map { Widget(it) }
        widgetDao.deleteWidgets(widgets)
            .compose(scheduling.forCompletable())
            .subscribe()
    }
}
