package uk.co.barbuzz.appwidget.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import uk.co.barbuzz.appwidget.base.LifecycleScopeModule
import uk.co.barbuzz.appwidget.base.ViewModelKey
import uk.co.barbuzz.appwidget.base.ViewModelProviderModule

@Module(
    includes = [
        LifecycleScopeModule::class,
        ViewModelProviderModule::class
    ]
)
internal interface MainModule {

    @Binds
    fun fragmentActivity(activity: MainActivity): FragmentActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainModel::class)
    fun mainModel(mainModel: MainModel): ViewModel
}
