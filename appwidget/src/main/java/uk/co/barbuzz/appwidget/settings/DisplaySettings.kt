package uk.co.barbuzz.appwidget.settings

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.settings.NightModePreferences
import uk.co.barbuzz.appwidget.base.settings.OpacityPreferences
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Analytics
import javax.inject.Inject

internal class DisplaySettings @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val nightModePreferences: NightModePreferences,
    private val opacityPreferences: OpacityPreferences,
    private val widgetUpdater: WidgetUpdater,
    private val analytics: Analytics,
    private val scheduling: SchedulingStrategy,
    fragment: SettingsFragment
) : Settings(fragment), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(owner: LifecycleOwner) {
        addPreferencesFromResource(R.xml.pref_display)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.isKeyEquals(R.string.pref_key_night_mode)) {
            nightModePreferences.updateDefaultNightMode()
            activity.recreateTaskStack()

            updateAllWidgets()

            val selectedValue = nightModePreferences.mode.stringVale(context.resources)
            analytics.sendValueEvent("Night Mode", selectedValue)
        }
        if (key.isKeyEquals(R.string.pref_key_opacity)) {
            updateAllWidgets()

            val selectedValue = opacityPreferences.opacity.stringVale(context.resources)
            analytics.sendValueEvent("Opacity", selectedValue)
        }
    }

    private fun updateAllWidgets() {
        widgetUpdater.updateAll()
            .compose(scheduling.forCompletable())
            .subscribe()
    }

    private fun Activity.recreateTaskStack() {
        finish()
        TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intent)
            .startActivities()
    }
}
