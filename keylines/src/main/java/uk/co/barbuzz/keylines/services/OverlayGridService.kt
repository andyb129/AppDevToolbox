package uk.co.barbuzz.keylines.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.ui.KeylineFragment.Companion.SERVICE_STOPPED_INTENT_FILTER
import uk.co.barbuzz.keylines.widget.IrregularLineView
import uk.co.barbuzz.keylines.widget.RegularLineView

class OverlayGridService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var view: View
    private lateinit var baselineGridView: RegularLineView
    private lateinit var incrementGridView: RegularLineView
    private lateinit var typographyLinesView: RegularLineView
    private lateinit var contentKeylinesView: IrregularLineView

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        removeNotification()
        removeGrid()
        isRunning = false
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            showNotification()
            showOverlay()
            isRunning = true
        } else if (intent.hasExtra(UPDATE_EXTRA)) {
            readPreferences()
        } else if (intent?.action == CANCEL_EXTRA) {
            stopSelf()
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(SERVICE_STOPPED_INTENT_FILTER))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showOverlay() {
        view = LayoutInflater.from(this).inflate(R.layout.overlay, null)
        bindViews()
        setupContentKeylines()
        readPreferences()
        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT)
        windowManager.addView(view, params)
    }

    private fun bindViews() {
        baselineGridView = view.findViewById<View>(R.id.baseline_grid) as RegularLineView
        incrementGridView = view.findViewById<View>(R.id.increment_grid) as RegularLineView
        typographyLinesView = view.findViewById<View>(R.id.typography_lines) as RegularLineView
        contentKeylinesView = view.findViewById<View>(R.id.content_keylines) as IrregularLineView
    }

    private fun setupContentKeylines() {
        val contentEdgeMargin = resources.getDimension(
            R.dimen.material_content_edge_margin_horizontal
        )
        val secondaryContentEdgeMargin = resources.getDimension(
            R.dimen.material_content_secondary_edge_margin_start
        )
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val screenWidth = metrics.widthPixels.toFloat()
        val coordinates = floatArrayOf(
                contentEdgeMargin,
                secondaryContentEdgeMargin,
                screenWidth - contentEdgeMargin
        )
        contentKeylinesView.coordinates = coordinates
    }

    private fun readPreferences() {
        val overlayPref =
            OverlayPreference(this)
        overlayPref.updateBaselineGridView(baselineGridView)
        overlayPref.updateIncrementGridView(incrementGridView)
        overlayPref.updateTypographyLinesView(typographyLinesView)
        overlayPref.updateContentKeylinesView(contentKeylinesView)
    }

    private fun readIncrementPreferences() {
        val overlayPref =
            OverlayPreference(this)
        overlayPref.updateIncrementGridView(incrementGridView)
    }

    /**
     * Show a notification while this service is running.
     */
    private fun showNotification() {
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, "Grid Channel", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "Grid channel for ongoing notification"
            notificationManager!!.createNotificationChannel(mChannel)
            builder = Notification.Builder(this, CHANNEL_ID)
        } else {
            builder = Notification.Builder(this)
        }
        val notification = builder
                .setSmallIcon(R.drawable.ic_keylines)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.active_overlay_grid))
                .addAction(createAction(CANCEL_EXTRA, "Cancel"))
                .setOngoing(true)
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createAction(action: String, text: String): Notification.Action {
        val intent = Intent(this, OverlayGridService::class.java).setAction(action)
        val pendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        return Notification.Action(0, text, pendingIntent)
    }

    private fun removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun removeGrid() {
        if (view != null) {
            windowManager.removeView(view)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupContentKeylines()
        readIncrementPreferences()
    }

    inner class OverlayBinder : Binder() {
        val gridService: OverlayGridService
            get() = this@OverlayGridService
    }

    companion object {
        private const val CHANNEL_ID = "uk.co.barbuzz.appdevtoolbox"
        private const val UPDATE_EXTRA = "update_grid"
        private const val CANCEL_EXTRA = "cancel_grid"
        var isRunning = false
        private const val NOTIFICATION_ID = 1

        fun getUpdateIntent(context: Context): Intent {
            val intent = Intent(context, OverlayGridService::class.java)
            intent.putExtra(UPDATE_EXTRA, true)
            return intent
        }

    }
}
