package uk.co.barbuzz.appwidget.widget.click.commands

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import uk.co.barbuzz.appwidget.base.navigation.IntentCommand

internal data class AppDetailsCommand(private val packageName: String) : IntentCommand {
    override fun createIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
    }
}
