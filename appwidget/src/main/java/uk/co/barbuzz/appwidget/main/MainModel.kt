package uk.co.barbuzz.appwidget.main

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import uk.co.barbuzz.appwidget.base.settings.Sorting
import uk.co.barbuzz.appwidget.base.settings.SortingPreferences
import uk.co.barbuzz.appwidget.data.FullWidgetDao
import uk.co.barbuzz.appwidget.data.Widget
import uk.co.barbuzz.appwidget.widget.ApplicationInfoResolver
import uk.co.barbuzz.appwidget.widget.DisplayApplicationInfo
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData
import javax.inject.Inject

private typealias MainResult = Pair<List<WidgetListData>, DiffUtil.DiffResult>

internal class MainModel @Inject constructor(
    private val sortingPreferences: SortingPreferences,
    applicationInfoResolver: ApplicationInfoResolver,
    fullWidgetDao: FullWidgetDao
) : ViewModel() {

    private val processor = BehaviorProcessor.create<MainResult>()
    private val disposable: Disposable

    init {
        disposable = fullWidgetDao.allWidgets()
            .map { widgets ->
                widgets.map {
                    val apps = it.packageNames
                        .flatMap(applicationInfoResolver::resolve)
                        .sort()
                    WidgetListData(Widget(it.appWidgetId, it.name), apps, it.favAction)
                }
            }
            .scan(INITIAL_PAIR) { (data, _), newData ->
                newData to DiffUtil.calculateDiff(WidgetDiffCallbacks(data, newData))
            }
            .skip(1)
            .subscribe(processor::onNext)
    }

    val data get() = processor.hide()

    override fun onCleared() = disposable.dispose()

    private fun List<DisplayApplicationInfo>.sort() = when (sortingPreferences.sorting) {
        Sorting.ORDER_ADDED -> asReversed()
        Sorting.ALPHABETICALLY_PACKAGES -> sortedBy { it.packageName }
        Sorting.ALPHABETICALLY_NAMES -> sortedBy { it.label.toString() }
    }

    companion object {

        private val INITIAL_PAIR = emptyList<WidgetListData>() to
                DiffUtil.calculateDiff(WidgetDiffCallbacks(emptyList(), emptyList()))
    }
}
