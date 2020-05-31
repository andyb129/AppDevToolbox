package uk.co.barbuzz.snippet.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import uk.co.barbuzz.snippet.R

class OverlaySnippetPasteService : Service() {

    private lateinit var pasteText: String
    private lateinit var coordinatesPair: Pair<Int, Int>
    private lateinit var windowManager: WindowManager
    private lateinit var viewOverlayPasteText: View
    private lateinit var handler: Handler
    private var layoutFlag: Int = 0

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        removeOverlay()
        isRunning = false
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            readPreferences()
            showOverlayPasteText()
            setOverlayPasteTextOnClick()
            setTimeoutDismiss()

            isRunning = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setTimeoutDismiss() {
        handler = Handler()
        handler.postDelayed({ stopSelf() }, SERVICE_STOP_SELF_TIMEOUT)
    }

    private fun readPreferences() {
        val prefs = OverlaySnippetPastePreference(this)
        coordinatesPair = prefs.getPasteTextCoordinates()
        pasteText = prefs.getPasteText()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showOverlayPasteText() {
        viewOverlayPasteText = LayoutInflater.from(this).inflate(R.layout.overlay_paste_text, null)
        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            coordinatesPair.first + 50, coordinatesPair.second,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.LEFT
        windowManager.addView(viewOverlayPasteText, params)
    }

    private fun setOverlayPasteTextOnClick() {
        val phraseText = viewOverlayPasteText.findViewById<TextView>(R.id.phrase_text)
        phraseText.text = pasteText
        phraseText.setOnClickListener{
            startService(SnippetTextAccessibilityService.Companion.getPasteTextIntent(applicationContext))
            stopSelf()
        }

        val closeButton = viewOverlayPasteText.findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener{
            stopSelf()
        }
    }

    private fun removeOverlay() {
        windowManager.removeView(viewOverlayPasteText)
    }

    companion object {
        var isRunning = false
    }
}

private const val SERVICE_STOP_SELF_TIMEOUT: Long = 5000
