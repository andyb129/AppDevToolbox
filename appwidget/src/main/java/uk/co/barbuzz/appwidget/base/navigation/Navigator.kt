package uk.co.barbuzz.appwidget.base.navigation

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class Navigator @Inject constructor(
    private val activity: FragmentActivity
) {

    fun navigate(command: Command) {
        command.start(activity)
    }
}
