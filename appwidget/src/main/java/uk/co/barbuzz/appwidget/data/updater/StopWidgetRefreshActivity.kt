package uk.co.barbuzz.appwidget.data.updater

import android.content.Intent
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import uk.co.barbuzz.appwidget.base.settings.AutoUpdatePreferences
import javax.inject.Inject

class StopWidgetRefreshActivity : DaggerAppCompatActivity() {

    @Inject lateinit var autoUpdatePreferences: AutoUpdatePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autoUpdatePreferences.autoUpdate = false
        stopService(Intent(this, WidgetRefreshService::class.java))
        finish()
    }
}
