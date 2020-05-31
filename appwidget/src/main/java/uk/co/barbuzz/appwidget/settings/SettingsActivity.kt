package uk.co.barbuzz.appwidget.settings

import android.app.backup.BackupManager
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.transaction
import dagger.android.support.DaggerAppCompatActivity
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.data.Analytics
import javax.inject.Inject

class SettingsActivity : DaggerAppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject lateinit var analytics: Analytics
    @Inject lateinit var sharedPreferences: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            supportFragmentManager.transaction {
                add(R.id.fragment_container, SettingsFragment.newInstance())
            }
            analytics.sendScreenView(this, "Settings")
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        BackupManager(this).dataChanged()
    }
}
