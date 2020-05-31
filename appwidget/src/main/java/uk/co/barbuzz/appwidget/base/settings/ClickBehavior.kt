package uk.co.barbuzz.appwidget.base.settings

import androidx.annotation.StringRes
import uk.co.barbuzz.appwidget.R

enum class ClickBehavior(
    @StringRes override val value: Int,
    @StringRes override val entry: Int
) : PreferenceEntries {

    LAUNCHER(
        R.string.pref_value_click_behavior_launcher,
        R.string.pref_entry_click_behavior_launcher
    ),
    EXPORTED(
        R.string.pref_value_click_behavior_exported,
        R.string.pref_entry_click_behavior_exported
    ),
}
