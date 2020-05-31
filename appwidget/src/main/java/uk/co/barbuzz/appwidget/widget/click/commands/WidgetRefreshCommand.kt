package uk.co.barbuzz.appwidget.widget.click.commands

import android.content.Context
import uk.co.barbuzz.appwidget.base.navigation.IntentCommand
import uk.co.barbuzz.appwidget.widget.click.WidgetRefreshActivity

internal data class WidgetRefreshCommand(private val appWidgetId: Int) : IntentCommand {

    override fun createIntent(context: Context) = WidgetRefreshActivity.createIntent(context, appWidgetId)
}
