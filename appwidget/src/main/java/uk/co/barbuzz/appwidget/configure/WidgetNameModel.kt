package uk.co.barbuzz.appwidget.configure

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.extensions.onlyTrue
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Widget
import uk.co.barbuzz.appwidget.data.WidgetDao
import javax.inject.Inject

internal class WidgetNameModel @Inject constructor(
    private val widgetDao: WidgetDao,
    private val widgetUpdater: WidgetUpdater,
    val appWidgetId: Int,
    scheduling: SchedulingStrategy
) : ViewModel() {

    private val disposable: Disposable

    init {
        disposable = insertIfNotFound()
            .andThen(widgetUpdater.updateAll())
            .compose(scheduling.forCompletable())
            .subscribe()
    }

    private fun insertIfNotFound() =
        findWidget()
            .isEmpty
            .onlyTrue()
            .flatMapCompletable {
                widgetDao.insertWidget(Widget(appWidgetId))
            }

    @CheckReturnValue
    fun updateWidgetName(widgetName: String): Completable {
        val widget = Widget(appWidgetId, widgetName)
        return widgetDao.updateWidget(widget)
            .andThen(widgetUpdater.update(appWidgetId, widgetName))
    }

    @CheckReturnValue
    fun currentWidgetName() = findWidget().map(Widget::name)

    private fun findWidget() = widgetDao.findWidgetById(appWidgetId)

    override fun onCleared() = disposable.dispose()
}
