package uk.co.barbuzz.appwidget.settings

import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.settings.SortingPreferences
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Analytics
import javax.inject.Inject

internal class GeneralSettings @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val sortingPreferences: SortingPreferences,
    private val widgetUpdater: WidgetUpdater,
    private val analytics: Analytics,
    private val scheduling: SchedulingStrategy,
    fragment: SettingsFragment
) : Settings(fragment),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(owner: LifecycleOwner) {
        addPreferencesFromResource(R.xml.pref_general)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.isKeyEquals(R.string.pref_key_sorting)) {

            widgetUpdater.updateAll()
                .compose(scheduling.forCompletable())
                .subscribe()

            val selectedValue = sortingPreferences.sorting.stringVale(context.resources)
            analytics.sendValueEvent("Sorting", selectedValue)
        }
    }
}
