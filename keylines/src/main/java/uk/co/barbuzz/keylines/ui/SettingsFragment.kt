package uk.co.barbuzz.keylines.ui

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceFragment
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.services.OverlayGridService

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    private val listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (OverlayGridService.isRunning) {
            activity.startService(OverlayGridService.getUpdateIntent(activity))
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(listener)
    }
}