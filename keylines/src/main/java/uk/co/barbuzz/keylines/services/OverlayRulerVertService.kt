package uk.co.barbuzz.keylines.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import uk.co.barbuzz.keylines.R


class OverlayRulerVertService : Service() {

    private var yOffset: Int = 0
    private var xOffset: Int = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var viewRulerVertical: View
    private var layoutFlag: Int = 0

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        removeNotification()
        removeRuler()
        isRunning = false
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            showNotification()
            showRulerOverlayVertical()
            dragAndDropVerticalRuler()

            isRunning = true
        } else if (intent.hasExtra(UPDATE_EXTRA)) {
            //readPreferences()
        } else if (intent?.action == CANCEL_EXTRA) {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showRulerOverlayVertical() {
        viewRulerVertical = LayoutInflater.from(this).inflate(R.layout.overlay_ruler_vertical, null)
        readPreferences()
        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            0, 0,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        windowManager.addView(viewRulerVertical, params)
    }

    private fun dragAndDropVerticalRuler() {
        viewRulerVertical.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                /* You can play around with the offset to set where you want the users finger to be on the view. Currently it should be centered.*/
                xOffset = v.width / 2
                yOffset = v.height / 2
                val x = event.rawX.toInt() - xOffset
                val y = event.rawY.toInt() - yOffset
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    x, y,
                    layoutFlag,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.LEFT
                windowManager.updateViewLayout(viewRulerVertical, params)
                return@OnTouchListener true
            }
            false
        })
    }

    private fun readPreferences() {

    }

    /**
    * Show a notification while this service is running.
    */
    private fun showNotification() {
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, "DpLinesVert Channel", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "Ruler vert Overlay channel for ongoing notification"
            notificationManager!!.createNotificationChannel(mChannel)
            builder = Notification.Builder(this, CHANNEL_ID)
        } else {
            builder = Notification.Builder(this)
        }
        val notification = builder
            .setSmallIcon(R.drawable.ic_keylines)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(getText(R.string.active_overlay_ruler_vert))
            .addAction(createAction(CANCEL_EXTRA, "Cancel"))
            .setOngoing(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createAction(action: String, text: String): Notification.Action {
        val intent = Intent(this, OverlayRulerVertService::class.java).setAction(action)
        val pendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        return Notification.Action(0, text, pendingIntent)
    }

    private fun removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun removeRuler() {
        if (viewRulerVertical != null) {
            windowManager.removeView(viewRulerVertical)
        }
    }

    inner class OverlayBinder : Binder() {
        val overlayRulerVertService: OverlayRulerVertService
            get() = this@OverlayRulerVertService
    }

    companion object {
        private const val CHANNEL_ID = "uk.co.barbuzz.appdevtoolbox"
        private const val UPDATE_EXTRA = "update_ruler_vert"
        private const val CANCEL_EXTRA = "cancel_ruler_vert"
        var isRunning = false
        private const val NOTIFICATION_ID = 1

        fun getUpdateIntent(context: Context): Intent {
            val intent = Intent(context, OverlayRulerVertService::class.java)
            intent.putExtra(UPDATE_EXTRA, true)
            return intent
        }

    }
}
