package uk.co.barbuzz.appwidget.widget.click.commands

import android.content.Context
import android.os.UserHandle
import uk.co.barbuzz.appwidget.base.navigation.IntentCommand
import uk.co.barbuzz.appwidget.widget.chooser.ActivityChooserActivity

internal data class ActivityChooserCommand(
    private val packageName: String,
    private val user: UserHandle
) : IntentCommand {

    override fun createIntent(context: Context) =
        ActivityChooserActivity.createIntent(context, packageName, user)
}
