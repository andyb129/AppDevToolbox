package uk.co.barbuzz.appwidget.configure

import android.annotation.TargetApi
import android.os.Build.VERSION_CODES.O
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.navigation.Navigator
import uk.co.barbuzz.appwidget.base.navigation.settingsCommand
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Analytics
import uk.co.barbuzz.appwidget.data.FullWidgetDao
import uk.co.barbuzz.appwidget.data.Widget
import uk.co.barbuzz.appwidget.widget.ApplicationInfoResolver
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData
import javax.inject.Inject

internal class ConfigurePresenter @Inject constructor(
    viewModelProvider: ViewModelProvider,
    widgetUpdater: WidgetUpdater,
    widgetPinner: WidgetPinner,
    configurePinning: ConfigurePinning,
    private val navigator: Navigator,
    private val fullWidgetDao: FullWidgetDao,
    private val applicationInfoResolver: ApplicationInfoResolver,
    private val appWidgetId: Int,
    private val scheduling: SchedulingStrategy,
    private val scopeProvider: ScopeProvider,
    private val analytics: Analytics
) {

    @TargetApi(O)
    private val updateWidget = Completable.fromAction {
        if (configurePinning) {
            widgetPinner.requestPin()
        } else {
            widgetUpdater.notifyWidgetDataChanged(appWidgetId)
        }
    }

    private val packageMatcherModel = viewModelProvider.get<PackageMatcherModel>()

    fun bind(view: ConfigureView) {
        fullWidgetDao.findWidgetById(appWidgetId)
            .map {
                val apps = it.packageNames
                    .flatMap(applicationInfoResolver::resolve)
//                    .sort() TODO
                WidgetListData(Widget(it.appWidgetId, it.name), apps, it.favAction)
            }
            .compose(scheduling.forFlowable())
            .autoDisposable(scopeProvider)
            .subscribe(view::updateWidgetPreview)

        packageMatcherModel.findPossiblePackageMatchers()
            .compose(scheduling.forFlowable())
            .autoDisposable(scopeProvider)
            .subscribe(view::setItems)

        packageMatcherModel.findAvailablePackageMatchers()
            .compose(scheduling.forFlowable())
            .autoDisposable(scopeProvider)
            .subscribe(view::setFilters)

        view.onConfirmClicked = {
            packageMatcherModel.findAndInsertMatchingApps()
                .andThen(updateWidget)
                .compose(scheduling.forCompletable())
                .doOnComplete {
                    trackConfirm()
                }
                .subscribe {
                    view.finishWith(appWidgetId)
                }
        }

        view.onPackageMatcherAdded = {
            packageMatcherModel.insertPackageMatcher(it)
                .compose(scheduling.forCompletable())
                .subscribe()
        }

        view.onSettingsClicked = {
            navigator.navigate(settingsCommand())
        }
    }

    private fun trackConfirm() {
        analytics.sendEvent("Confirm Clicked")
    }
}
