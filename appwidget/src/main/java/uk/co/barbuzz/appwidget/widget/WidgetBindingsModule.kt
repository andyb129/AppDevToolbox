package uk.co.barbuzz.appwidget.widget

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.widget.chooser.ActivityChooserActivity

@Module
abstract class WidgetBindingsModule {

    @Binds
    abstract fun widgetUpdater(widgetUpdater: WidgetUpdaterImpl): WidgetUpdater

    @ContributesAndroidInjector
    internal abstract fun widgetProvider(): WidgetProvider

    @ContributesAndroidInjector
    internal abstract fun widgetViewsService(): WidgetViewsService

    @ContributesAndroidInjector
    internal abstract fun activityChooserActivity(): ActivityChooserActivity
}
