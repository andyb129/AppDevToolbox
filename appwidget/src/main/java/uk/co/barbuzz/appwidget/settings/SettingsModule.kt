package uk.co.barbuzz.appwidget.settings

import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet

@Module
internal class SettingsModule {

    @Provides
    @ElementsIntoSet
    fun settings(
        general: GeneralSettings,
        display: DisplaySettings,
        advancedSettings: AdvancedSettings,
        other: OtherSettings
    ) = setOf(general, display, advancedSettings, other)
}
