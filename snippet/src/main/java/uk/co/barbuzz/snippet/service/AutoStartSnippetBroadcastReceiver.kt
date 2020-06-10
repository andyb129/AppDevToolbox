package uk.co.barbuzz.snippet.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AutoStartSnippetBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        val intent = Intent(context, SnippetTextAccessibilityService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        Log.i("AutoStartSnippet", "started")
    }
}