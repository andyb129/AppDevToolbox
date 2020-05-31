package uk.co.barbuzz.appwidget.widget.click

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.co.barbuzz.appwidget.base.navigation.Command

internal class Action(
    @StringRes val text: Int,
    @DrawableRes val icon: Int? = null,
    val commandForPackage: ((packageName: String) -> Command)? = null,
    val command: Command? = null
)
