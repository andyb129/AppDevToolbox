package uk.co.barbuzz.appwidget.widget.click

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.data.Action
import uk.co.barbuzz.appwidget.base.navigation.FinishCommand
import uk.co.barbuzz.appwidget.base.navigation.Navigator
import uk.co.barbuzz.appwidget.widget.click.commands.ActivityChooserCommand
import uk.co.barbuzz.appwidget.widget.click.commands.AppDetailsCommand
import uk.co.barbuzz.appwidget.widget.click.commands.PlayStoreCommand
import uk.co.barbuzz.appwidget.widget.click.commands.UninstallCommand
import javax.inject.Inject

internal class ClickHandlingNavigation @Inject constructor(
    private val actionListGenerator: ActionListGenerator,
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val input: ClickHandlingActivity.Input
) {

    fun navigateToChooser() =
        navigator.navigate(ActivityChooserCommand(input.packageName, input.user) and FinishCommand)

    fun navigateToAction(favAction: Action) =
        navigator.navigate(favAction.toCommand() and FinishCommand)

    private fun Action.toCommand() = when (this) {
        Action.UNINSTALL -> UninstallCommand(input.packageName)
        Action.APP_DETAILS -> AppDetailsCommand(input.packageName)
        Action.PLAY_STORE -> PlayStoreCommand(input.packageName)
    }

    fun navigateToActionsDialog() {
        val adapter = ActionDialogAdapter(
            activity,
            actionListGenerator.actionList()
        )
        AlertDialog.Builder(activity)
            .setTitle(R.string.widget_choose_action)
            .setAdapter(adapter) { _, position ->
                val action = adapter.getItem(position)!!
                val command = action.commandForPackage?.invoke(input.packageName) ?: action.command!!
                navigator.navigate(command)
            }
            .setOnDismissListener {
                navigator.navigate(FinishCommand)
            }
            .show()
    }
}
