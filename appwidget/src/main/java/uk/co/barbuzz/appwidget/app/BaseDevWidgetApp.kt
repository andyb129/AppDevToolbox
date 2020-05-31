package uk.co.barbuzz.appwidget.app

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import dagger.android.support.DaggerApplication
import uk.co.barbuzz.appwidget.base.settings.NightModePreferences
import uk.co.barbuzz.appwidget.data.updater.ShouldStartWidgetRefreshService
import uk.co.barbuzz.appwidget.data.updater.WidgetRefreshService
import javax.inject.Inject

abstract class BaseDevWidgetApp : DaggerApplication() {

    @Inject lateinit var nightModePreferences: NightModePreferences
    @Inject lateinit var shouldStartWidgetRefreshService: ShouldStartWidgetRefreshService

    override fun onCreate() {
        super.onCreate()
        nightModePreferences.updateDefaultNightMode()

        if (SDK_INT >= O && shouldStartWidgetRefreshService.check()) {
            startForegroundService(Intent(this, WidgetRefreshService::class.java))
        }
    }
}
