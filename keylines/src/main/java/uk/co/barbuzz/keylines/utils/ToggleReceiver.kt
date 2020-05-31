package uk.co.barbuzz.keylines.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import uk.co.barbuzz.keylines.services.OverlayGridService

class ToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, OverlayGridService::class.java)
        if (intent.getBooleanExtra(EXTRA_ENABLED, true)) {
            context.startService(service)
        } else {
            context.stopService(service)
        }
    }

    companion object {
        private const val EXTRA_ENABLED = "enabled"
        fun getIntent(enabled: Boolean): Intent {
            val intent = Intent("blue.aodev.materialkeylines.TOGGLE")
            intent.putExtra(EXTRA_ENABLED, enabled)
            return intent
        }
    }
}