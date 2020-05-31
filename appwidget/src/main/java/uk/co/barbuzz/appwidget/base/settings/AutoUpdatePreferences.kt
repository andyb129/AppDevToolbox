package uk.co.barbuzz.appwidget.base.settings

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import uk.co.barbuzz.appwidget.R
import javax.inject.Inject

class AutoUpdatePreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    resources: Resources
) {

    private val key = resources.getString(R.string.pref_key_auto_updater)

    var autoUpdate: Boolean
        get() = sharedPreferences.getBoolean(key, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(key, value)
            }
        }
}
