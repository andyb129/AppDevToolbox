package uk.co.barbuzz.appwidget.app

import uk.co.barbuzz.appwidget.BuildConfig
import uk.co.barbuzz.appwidget.settings.Version
import javax.inject.Inject

class AppVersion @Inject constructor() : Version {
    override val versionName: String
        get() = BuildConfig.VERSION_NAME
    override val versionCode: Int
        get() = BuildConfig.VERSION_CODE
}
