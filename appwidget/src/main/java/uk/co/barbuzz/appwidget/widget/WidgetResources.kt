package uk.co.barbuzz.appwidget.widget

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.os.PowerManager
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.settings.NightMode
import uk.co.barbuzz.appwidget.base.settings.NightModePreferences
import uk.co.barbuzz.appwidget.base.settings.Opacity
import uk.co.barbuzz.appwidget.data.Action
import javax.inject.Inject

class WidgetResources @Inject constructor(
    private val nightModePreferences: NightModePreferences,
    private val powerManager: PowerManager,
    private val resources: Resources
) {

    @get:DrawableRes
    val deleteIcon
        get() = if (isNightModeOn) R.drawable.ic_delete_light else R.drawable.ic_delete

    @get:DrawableRes
    val settingsIcon
        get() = if (isNightModeOn) R.drawable.ic_settings_light else R.drawable.ic_settings

    @get:DrawableRes
    val moreActionsIcon
        get() = if (isNightModeOn) R.drawable.ic_more_actions_light else R.drawable.ic_more_actions

    @get:DrawableRes
    val playStoreIcon
        get() = if (isNightModeOn) R.drawable.ic_play_store_light else R.drawable.ic_play_store

    @get:DrawableRes
    val devOptionsIcon
        get() = if (isNightModeOn) R.drawable.ic_dev_options_light else R.drawable.ic_dev_options

    @get:DrawableRes
    val refreshIcon
        get() = if (isNightModeOn) R.drawable.ic_refresh_light else R.drawable.ic_refresh

    @get:DrawableRes
    val languageIcon
        get() = if (isNightModeOn) R.drawable.ic_language else R.drawable.ic_language

    private val foregroundDark = ResourcesCompat.getColor(resources, R.color.foregroundDark, null)
    private val foregroundLight = ResourcesCompat.getColor(resources, R.color.foregroundLight, null)

    val foregroundColor
        @ColorInt get() = if (isNightModeOn) foregroundLight else foregroundDark

    val foregroundColorInverse
        @ColorInt get() = if (isNightModeOn) foregroundDark else foregroundLight

    private val isNightModeOn
        get() = when (nightModePreferences.mode) {
            NightMode.ON -> true
            NightMode.OFF -> false
            NightMode.BATTERY -> powerManager.isPowerSaveMode
            NightMode.SYSTEM -> (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES
        }

    @ColorInt @Suppress("MagicNumber")
    fun resolveBackgroundColor(opacity: Opacity): Int {
        val backgroundColor = foregroundColorInverse
        val opacityInt = opacity.stringVale(resources).toInt()
        return backgroundColor and 0xffffff or (opacityInt * 255 / 100 shl 24)
    }

    @Dimension
    fun resolveAppIconSize(widgetWidth: Int): Int {
        return when {
            widgetWidth < TINY_WIDGET_LIMIT -> 0
            widgetWidth < FAV_ACTION_LIMIT -> resources.getDimensionPixelSize(R.dimen.app_widget_icon_size_small)
            else -> resources.getDimensionPixelSize(R.dimen.app_widget_icon_size)
        }
    }

    fun resolveFavIcon(favAction: Action) = when (favAction) {
        Action.UNINSTALL -> deleteIcon
        Action.APP_DETAILS -> settingsIcon
        Action.PLAY_STORE -> playStoreIcon
    }

    fun shouldDisplayFavAction(widgetWidth: Int): Boolean {
        return widgetWidth >= FAV_ACTION_LIMIT
    }

    fun shouldDisplayExtendedHeader(widgetWidth: Int): Boolean {
        return widgetWidth >= TINY_WIDGET_LIMIT
    }

    companion object {
        private const val TINY_WIDGET_LIMIT = 214
        private const val FAV_ACTION_LIMIT = 291
    }
}
