package uk.co.barbuzz.appwidget.main

import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import uk.co.barbuzz.appwidget.base.navigation.Navigator
import uk.co.barbuzz.appwidget.configure.WidgetPinRequestCommand
import javax.inject.Inject

class MainNavigation @Inject constructor(private val navigator: Navigator) {

    @RequiresApi(O)
    fun navigateForPinning() {
        navigator.navigate(WidgetPinRequestCommand)
    }
}
