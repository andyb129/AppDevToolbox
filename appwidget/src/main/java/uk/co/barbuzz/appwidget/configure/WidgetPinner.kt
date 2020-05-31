package uk.co.barbuzz.appwidget.configure

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import uk.co.barbuzz.appwidget.base.extensions.toPendingBroadcast
import javax.inject.Inject

internal class WidgetPinner @Inject constructor(
    private val app: Application,
    private val appWidgetManager: AppWidgetManager
) {

    @RequiresApi(O)
    fun requestPin() {
        val widgetProvider = ComponentName(app, "uk.co.barbuzz.appwidget.widget.WidgetProvider")
        val successCallback = Intent(app, WidgetPinnedReceiver::class.java)
            .toPendingBroadcast(app)

        appWidgetManager.requestPinAppWidget(widgetProvider, null, successCallback)
    }
}
