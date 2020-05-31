package uk.co.barbuzz.appwidget.configure

import android.content.Context
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import uk.co.barbuzz.appwidget.base.navigation.IntentCommand

@RequiresApi(O)
object WidgetPinRequestCommand : IntentCommand {

    override fun createIntent(context: Context) = ConfigureActivity.createIntentForPinning(context)
}
