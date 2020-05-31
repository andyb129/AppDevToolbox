package uk.co.barbuzz.appwidget.app

import com.github.venom.Venom
import dagger.android.AndroidInjector
import timber.log.Timber
import uk.co.barbuzz.appwidget.BuildConfig

class DevWidgetApp : BaseDevWidgetApp() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        //setup library for process death notification trigger
        val venom = Venom.createInstance(this)
        venom.initialize()
        Venom.setGlobalInstance(venom)
    }

    override fun applicationInjector(): AndroidInjector<DevWidgetApp> =
        DaggerAppComponent.factory().create(this)
}
