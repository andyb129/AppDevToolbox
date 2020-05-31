package uk.co.barbuzz.appwidget.main

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainBindingsModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun mainActivity(): MainActivity

}
