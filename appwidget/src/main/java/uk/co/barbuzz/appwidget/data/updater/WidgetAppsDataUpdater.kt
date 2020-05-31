package uk.co.barbuzz.appwidget.data.updater

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import uk.co.barbuzz.appwidget.base.extensions.flatten
import uk.co.barbuzz.appwidget.data.App
import uk.co.barbuzz.appwidget.data.AppDao
import uk.co.barbuzz.appwidget.data.FilterDao
import javax.inject.Inject

class WidgetAppsDataUpdater @Inject constructor(
    private val filterDao: FilterDao,
    private val appDao: AppDao,
    private val packageResolver: PackageResolver
) {

    @CheckReturnValue
    fun findAndInsertMatchingApps(appWidgetId: Int): Completable {
        return filterDao.findFiltersByWidgetId(appWidgetId)
            .firstOrError()
            .flatten()
            .flatMapCompletable { packageMatcher ->
                insertMatchingApps(appWidgetId, packageMatcher)
            }
    }

    private fun insertMatchingApps(appWidgetId: Int, packageMatcher: String): Completable {
        return Observable
            .fromIterable(packageResolver.allApplications())
            .filter(matchPackage(packageMatcher))
            .toList()
            .flatMapCompletable { packageNames ->
                val apps = packageNames.map {
                    App(it, packageMatcher, appWidgetId)
                }
                appDao.insertApps(apps)
            }
    }
}
