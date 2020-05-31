package uk.co.barbuzz.appwidget.widget.click

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import dagger.android.support.DaggerAppCompatActivity
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.navigation.FinishCommand
import uk.co.barbuzz.appwidget.base.navigation.Navigator
import uk.co.barbuzz.appwidget.base.navigation.UriCommand
import uk.co.barbuzz.appwidget.widget.WidgetResources
import uk.co.barbuzz.appwidget.widget.click.commands.DevOptionsCommand
import uk.co.barbuzz.appwidget.widget.click.commands.WidgetRefreshCommand
import javax.inject.Inject

internal class HeaderOptionsActivity : DaggerAppCompatActivity() {

    @Inject lateinit var widgetResources: WidgetResources
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, -1)

        val actions = listOf(
            Action(
                R.string.widget_content_description_refresh,
                widgetResources.refreshIcon,
                command = WidgetRefreshCommand(appWidgetId)
            ),
            Action(
                R.string.widget_content_description_dev_options,
                widgetResources.devOptionsIcon,
                command = DevOptionsCommand
            ),
            Action(
                R.string.widget_configure,
                widgetResources.settingsIcon,
                command = UriCommand("devwidget://configure/?appWidgetId=$appWidgetId")
            )
        )

        val adapter = ActionDialogAdapter(this, actions)
        AlertDialog.Builder(this)
            .setTitle(R.string.widget_choose_action)
            .setAdapter(adapter) { _, position ->
                val command = adapter.getItem(position)!!.command!!
                navigator.navigate(command)
            }
            .setOnDismissListener {
                navigator.navigate(FinishCommand)
            }
            .show()
    }

    companion object {

        private const val EXTRA_APP_WIDGET_ID = "EXTRA_APP_WIDGET_ID"

        fun createIntent(context: Context, appWidgetId: Int) =
            Intent(context, HeaderOptionsActivity::class.java).apply {
                putExtra(EXTRA_APP_WIDGET_ID, appWidgetId)
            }
    }
}
