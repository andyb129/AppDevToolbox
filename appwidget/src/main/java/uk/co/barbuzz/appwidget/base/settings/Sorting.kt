package uk.co.barbuzz.appwidget.base.settings

import androidx.annotation.StringRes
import uk.co.barbuzz.appwidget.R

enum class Sorting(
    @StringRes override val value: Int,
    @StringRes override val entry: Int
) : PreferenceEntries {

    ORDER_ADDED(R.string.pref_value_sorting_order_added, R.string.pref_entry_sorting_order_added),
    ALPHABETICALLY_PACKAGES(
        R.string.pref_value_sorting_alphabetically_packages,
        R.string.pref_entry_sorting_alphabetically_packages
    ),
    ALPHABETICALLY_NAMES(
        R.string.pref_value_sorting_alphabetically_names,
        R.string.pref_entry_sorting_alphabetically_names
    );
}
