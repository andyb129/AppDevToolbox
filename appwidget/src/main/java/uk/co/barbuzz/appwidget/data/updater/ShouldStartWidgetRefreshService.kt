package uk.co.barbuzz.appwidget.data.updater

import uk.co.barbuzz.appwidget.base.settings.AutoUpdatePreferences
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import javax.inject.Inject

class ShouldStartWidgetRefreshService @Inject constructor(
    private val autoUpdatePreferences: AutoUpdatePreferences,
    private val widgetUpdater: WidgetUpdater
) {

    fun check(): Boolean {
        return autoUpdatePreferences.autoUpdate && widgetUpdater.hasWidgets()
    }
}
