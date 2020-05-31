package uk.co.barbuzz.appwidget.widget.click

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.navigation.ComponentCommand
import uk.co.barbuzz.appwidget.widget.WidgetResources
import uk.co.barbuzz.appwidget.widget.chooser.componentName
import uk.co.barbuzz.appwidget.widget.click.commands.AppDetailsCommand
import uk.co.barbuzz.appwidget.widget.click.commands.PlayStoreCommand
import uk.co.barbuzz.appwidget.widget.click.commands.UninstallCommand
import javax.inject.Inject

internal class ActionListGenerator @Inject constructor(
    private val packageManager: PackageManager,
    private val widgetResources: WidgetResources,
    private val input: ClickHandlingActivity.Input
) {

    fun actionList(): List<Action> {
        return listOfNotNull(
            Action(R.string.widget_action_uninstall, widgetResources.deleteIcon, ::UninstallCommand),
            Action(R.string.widget_action_app_details, widgetResources.settingsIcon, ::AppDetailsCommand),
            Action(R.string.widget_action_play_store, widgetResources.playStoreIcon, ::PlayStoreCommand),
            appNotificationsAction(),
            appSettingsAction()
        )
    }

    private fun appNotificationsAction(): Action? {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Notification.INTENT_CATEGORY_NOTIFICATION_PREFERENCES)
            .setPackage(input.packageName)

        val resolveInfo = packageManager.queryIntentActivities(intent, 0).firstOrNull() ?: return null

        return Action(
            R.string.widget_action_app_notification_settings,
            command = ComponentCommand(resolveInfo.activityInfo.componentName())
        )
    }

    @SuppressLint("InlinedApi")
    private fun appSettingsAction(): Action? {
        val intent = Intent(Intent.ACTION_APPLICATION_PREFERENCES)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage(input.packageName)

        val resolveInfo = packageManager.queryIntentActivities(intent, 0).firstOrNull() ?: return null

        return Action(
            R.string.widget_action_app_settings,
            command = ComponentCommand(resolveInfo.activityInfo.componentName())
        )
    }
}
