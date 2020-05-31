package uk.co.barbuzz.appwidget.app

import dagger.Module
import uk.co.barbuzz.appwidget.configure.ConfigureBindingsModule
import uk.co.barbuzz.appwidget.data.updater.DataUpdaterBindingModule
import uk.co.barbuzz.appwidget.main.MainBindingsModule
import uk.co.barbuzz.appwidget.settings.SettingsBindingsModule
import uk.co.barbuzz.appwidget.widget.WidgetBindingsModule
import uk.co.barbuzz.appwidget.widget.click.ClickBindingsModule

@Module(
    includes = [
        MainBindingsModule::class,
        ConfigureBindingsModule::class,
        ClickBindingsModule::class,
        DataUpdaterBindingModule::class,
        SettingsBindingsModule::class,
        WidgetBindingsModule::class
    ]
)
interface BindingModule
