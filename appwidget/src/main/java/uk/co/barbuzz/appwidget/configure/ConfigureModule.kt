package uk.co.barbuzz.appwidget.configure

import android.appwidget.AppWidgetManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import uk.co.barbuzz.appwidget.base.LifecycleScopeModule
import uk.co.barbuzz.appwidget.base.ViewModelKey
import uk.co.barbuzz.appwidget.base.ViewModelProviderModule

typealias ConfigurePinning = Boolean

@Module(
    includes = [
        ActivityBindingModule::class,
        ConfigureViewModelModule::class,
        LifecycleScopeModule::class,
        ViewModelProviderModule::class
    ]
)
internal object ConfigureModule {

    @Provides
    @JvmStatic
    fun appWidgetId(activity: ConfigureActivity): Int {
        return activity.intent.data?.getQueryParameter(AppWidgetManager.EXTRA_APPWIDGET_ID)?.toInt()
            ?: activity.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    }

    @Provides
    @JvmStatic
    fun configurePinning(activity: ConfigureActivity) = activity.configurePin
}

@Module
internal interface ActivityBindingModule {

    @Binds
    fun fragmentActivity(activity: ConfigureActivity): FragmentActivity

    @ContributesAndroidInjector
    fun configurePreferenceFragment(): ConfigurePreferenceFragment
}

@Module
internal interface ConfigureViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WidgetNameModel::class)
    fun widgetNameModel(model: WidgetNameModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PackageMatcherModel::class)
    fun packageMatcherModel(model: PackageMatcherModel): ViewModel
}
