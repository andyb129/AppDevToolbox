package uk.co.barbuzz.appwidget.configure

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.rxkotlin.combineLatest
import uk.co.barbuzz.appwidget.data.Filter
import uk.co.barbuzz.appwidget.data.FilterDao
import uk.co.barbuzz.appwidget.data.updater.PackageResolver
import uk.co.barbuzz.appwidget.data.updater.WidgetAppsDataUpdater
import javax.inject.Inject

internal class PackageMatcherModel @Inject constructor(
    private val packageResolver: PackageResolver,
    private val filterDao: FilterDao,
    private val widgetAppsDataUpdater: WidgetAppsDataUpdater,
    val appWidgetId: Int
) : ViewModel() {

    @CheckReturnValue
    fun insertPackageMatcher(packageMatcher: String): Completable {
        return filterDao.insertFilter(Filter(packageMatcher, appWidgetId))
    }

    @CheckReturnValue
    fun findAndInsertMatchingApps() = widgetAppsDataUpdater.findAndInsertMatchingApps(appWidgetId)

    @CheckReturnValue
    fun findPossiblePackageMatchers(): Flowable<List<String>> {
        return Flowable.fromCallable { packageResolver.allApplications().toPackageMatchers() }
            .combineLatest(findAvailablePackageMatchers())
            .map { (possible, available) ->
                possible - available
            }
    }

    @CheckReturnValue
    fun findAvailablePackageMatchers(): Flowable<List<String>> =
        filterDao.findFiltersByWidgetId(appWidgetId)
}
