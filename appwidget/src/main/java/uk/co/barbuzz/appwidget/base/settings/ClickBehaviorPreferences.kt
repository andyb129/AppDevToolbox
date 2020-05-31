package uk.co.barbuzz.appwidget.base.settings

import android.content.SharedPreferences
import android.content.res.Resources
import uk.co.barbuzz.appwidget.R
import javax.inject.Inject

class ClickBehaviorPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) {

    private val key = resources.getString(R.string.pref_key_click_behavior)

    val clickBehavior: ClickBehavior
        get() {
            val value = sharedPreferences.getString(key, null)
            return PreferenceEntries.fromValue(resources, value) ?: ClickBehavior.LAUNCHER
        }
}
