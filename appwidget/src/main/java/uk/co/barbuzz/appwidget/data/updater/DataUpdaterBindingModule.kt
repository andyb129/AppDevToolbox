package uk.co.barbuzz.appwidget.data.updater

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface DataUpdaterBindingModule {

    @ContributesAndroidInjector
    fun packageAddedReceiver(): PackageAddedReceiver

    @ContributesAndroidInjector
    fun packageRemovedReceiver(): PackageRemovedReceiver

    @ContributesAndroidInjector
    fun powerSaveChangedReceiver(): PowerSaveChangedReceiver

    @ContributesAndroidInjector
    fun stopWidgetRefreshActivity(): StopWidgetRefreshActivity

    @ContributesAndroidInjector
    fun widgetRefreshRescheduleJob(): WidgetRefreshRescheduleJob
}
