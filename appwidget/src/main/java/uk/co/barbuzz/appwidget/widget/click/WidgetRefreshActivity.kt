package uk.co.barbuzz.appwidget.widget.click

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Completable
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.updater.WidgetAppsDataUpdater
import javax.inject.Inject

internal class WidgetRefreshActivity : DaggerAppCompatActivity() {

    @Inject lateinit var widgetAppsDataUpdater: WidgetAppsDataUpdater
    @Inject lateinit var widgetUpdater: WidgetUpdater
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var scopeProvider: ScopeProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, -1)
        widgetAppsDataUpdater
            .findAndInsertMatchingApps(appWidgetId)
            .andThen(Completable.fromAction {
                widgetUpdater.notifyWidgetDataChanged(appWidgetId)
            })
            .compose(scheduling.forCompletable())
            .autoDisposable(scopeProvider)
            .subscribe {
                finish()
            }
    }

    companion object {

        private const val EXTRA_APP_WIDGET_ID = "EXTRA_APP_WIDGET_ID"

        fun createIntent(context: Context, appWidgetId: Int) =
            Intent(context, WidgetRefreshActivity::class.java).apply {
                putExtra(EXTRA_APP_WIDGET_ID, appWidgetId)
            }
    }
}
