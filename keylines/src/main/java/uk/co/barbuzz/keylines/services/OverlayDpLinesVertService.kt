package uk.co.barbuzz.keylines.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.ui.KeylineFragment.Companion.SERVICE_STOPPED_INTENT_FILTER
import uk.co.barbuzz.keylines.utils.RepeatListener


class OverlayDpLinesVertService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var viewDpLinesVertical: View
    private lateinit var viewDpLinesButtons: View
    private lateinit var buttonText: TextView
    private lateinit var spaceGuidlineLayout: RelativeLayout
    private lateinit var spaceGuidlineVert: View
    private lateinit var leftGuideline: View
    private lateinit var rightGuideline: View
    private lateinit var arrowLeft: ImageView
    private lateinit var arrowRight: ImageView
    private lateinit var spaceText: TextView
    private var spaceBias: Float = 0.5f
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var yOffset: Int = 0
    private var xOffset: Int = 0
    private var yOffsetButtons: Int = 0
    private var xOffsetButtons: Int = 0
    private var layoutFlag: Int = 0
    private var dpLinesSpacing: Int = 8

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        removeNotification()
        removeDpLines()
        isRunning = false
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            showNotification()
            showDpLinesOverlayVertical()
            dragAndDropVerticalDpLines()
            showDpLinesButton()
            dragAndDropDpLinesButtons()

            isRunning = true
        } else if (intent.hasExtra(UPDATE_EXTRA)) {
            setDpLinesColour()
        } else if (intent?.action == CANCEL_EXTRA) {
            stopSelf()
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(SERVICE_STOPPED_INTENT_FILTER))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showDpLinesOverlayVertical() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        viewDpLinesVertical = LayoutInflater.from(this).inflate(R.layout.overlay_dplines_vertical, null)

        leftGuideline = viewDpLinesVertical.findViewById<View>(R.id.top_guideline_vert)
        rightGuideline = viewDpLinesVertical.findViewById<View>(R.id.bottom_guideline_vert)
        spaceGuidlineLayout = viewDpLinesVertical.findViewById<RelativeLayout>(R.id.space_guideline_vert_layout)
        spaceGuidlineVert = viewDpLinesVertical.findViewById<View>(R.id.space_guideline_vert)
        arrowLeft = viewDpLinesVertical.findViewById<ImageView>(R.id.arrow_left_guideline_vert)
        arrowRight = viewDpLinesVertical.findViewById<ImageView>(R.id.arrow_right_guideline_vert)
        spaceText = viewDpLinesVertical.findViewById<TextView>(R.id.space_text_guideline_vert)

        setDpLinesColour()

        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            0, 0,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT)
        windowManager.addView(viewDpLinesVertical, params)
    }

    private fun dragAndDropVerticalDpLines() {
        viewDpLinesVertical.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                xOffset = v.width / 2
                yOffset = v.height / 2
                val x = event.rawX.toInt() - xOffset
                val y = event.rawY.toInt() - yOffset
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    x, y,
                    layoutFlag,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.LEFT
                windowManager.updateViewLayout(viewDpLinesVertical, params)
                return@OnTouchListener true
            }
            false
        })
    }

    private fun showDpLinesButton() {
        viewDpLinesButtons = LayoutInflater.from(this).inflate(R.layout.overlay_dplines_buttons, null)

        viewDpLinesButtons.findViewById<ImageView>(R.id.plus_button).setOnTouchListener(RepeatListener(100, 50, View.OnClickListener {
            increaseDpLinesSpacing()
        }))

        viewDpLinesButtons.findViewById<ImageView>(R.id.minus_button).setOnTouchListener(RepeatListener(100, 50, View.OnClickListener {
            decreaseDpLinesSpacing()
        }))

        viewDpLinesButtons.findViewById<ImageView>(R.id.up_button).setOnClickListener{
            moveLabelUp()
        }

        viewDpLinesButtons.findViewById<ImageView>(R.id.down_button).setOnClickListener{
            moveLabelDown()
        }
        viewDpLinesButtons.findViewById<TextView>(R.id.button_label).text = getString(R.string.db_line_vert_buttons_label)

        readPreferences()
        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        val x = screenWidth - viewDpLinesButtons.width
        val y = screenHeight - (viewDpLinesButtons.height + (viewDpLinesButtons.height/2))
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            x, y,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT)
        windowManager.addView(viewDpLinesButtons, params)
    }

    private fun dragAndDropDpLinesButtons() {
        viewDpLinesButtons.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                xOffsetButtons = v.width / 2
                yOffsetButtons = v.height / 2
                val x = event.rawX.toInt() - xOffsetButtons
                val y = event.rawY.toInt() - yOffsetButtons
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    x, y,
                    layoutFlag,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.LEFT
                windowManager.updateViewLayout(viewDpLinesButtons, params)
                return@OnTouchListener true
            }
            false
        })
    }

    private fun setDpLinesColour() {
        val dpLinesVertColour = OverlayPreference(this).getDpLinesVertColour()
        spaceGuidlineVert.setBackgroundColor(dpLinesVertColour)
        leftGuideline.setBackgroundColor(dpLinesVertColour)
        rightGuideline.setBackgroundColor(dpLinesVertColour)
        arrowLeft.setColorFilter(dpLinesVertColour)
        arrowRight.setColorFilter(dpLinesVertColour)
        spaceText.setTextColor(dpLinesVertColour)
    }

    private fun increaseDpLinesSpacing() {
        val newSpacing = dpLinesSpacing + 1
        if (newSpacing < screenWidth) {
            dpLinesSpacing = newSpacing
        }
        if (newSpacing > 32) {
            arrowLeft.visibility = View.VISIBLE
            arrowRight.visibility = View.VISIBLE
        } else {
            arrowLeft.visibility = View.GONE
            arrowRight.visibility = View.GONE
        }
        setDpLinesSpacing()
    }

    private fun decreaseDpLinesSpacing() {
        val newSpacing = dpLinesSpacing - 1
        if (newSpacing > 0) {
            dpLinesSpacing = newSpacing
        }
        if (newSpacing > 32) {
            arrowLeft.visibility = View.VISIBLE
            arrowRight.visibility = View.VISIBLE
        } else {
            arrowLeft.visibility = View.GONE
            arrowRight.visibility = View.GONE
        }
        setDpLinesSpacing()
    }

    private fun moveLabelUp() {
        val layoutParamsSpace = getSpaceLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.1f
        if (spaceBias == 0.9f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsSpace.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineLayout.layoutParams = layoutParamsSpace
        spaceText.layoutParams = layoutParamsText
    }

    private fun moveLabelDown() {
        val layoutParamsSpace = getSpaceLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.9f
        if (spaceBias == 0.1f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsSpace.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineLayout.layoutParams = layoutParamsSpace
        spaceText.layoutParams = layoutParamsText
    }

    private fun setDpLinesSpacing() {
        spaceText.text = "${dpLinesSpacing}dp"
        val layoutParamsSpace = getSpaceLayoutParam()
        layoutParamsSpace.verticalBias = spaceBias
        spaceGuidlineLayout.layoutParams = layoutParamsSpace
    }

    private fun getSpaceLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsSpace = ConstraintLayout.LayoutParams(
            dpLinesSpacing.px,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsSpace.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsSpace.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        return layoutParamsSpace
    }

    private fun getTextLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsText = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsText.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsText.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsText.startToEnd = R.id.bottom_guideline_vert
        layoutParamsText.setMargins(16.dp, 0, 0, 0)
        return layoutParamsText
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
            mChannel.description = "DpLnes Vert Overlay channel for ongoing notification"
            notificationManager!!.createNotificationChannel(mChannel)
            builder = Notification.Builder(this, CHANNEL_ID)
        } else {
            builder = Notification.Builder(this)
        }
        val notification = builder
            .setSmallIcon(R.drawable.ic_keylines)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(getText(R.string.active_overlay_dplines_vert))
            .addAction(createAction(CANCEL_EXTRA, "Cancel"))
            .setOngoing(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createAction(action: String, text: String): Notification.Action {
        val intent = Intent(this, OverlayDpLinesVertService::class.java).setAction(action)
        val pendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        return Notification.Action(0, text, pendingIntent)
    }

    private fun removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun removeDpLines() {
        if (viewDpLinesVertical != null) {
            windowManager.removeView(viewDpLinesVertical)
        }
        if (viewDpLinesButtons != null) {
            windowManager.removeView(viewDpLinesButtons)
        }
    }

    val Int.dp: Int
        get() = (this /resources.displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * resources.displayMetrics.density).toInt()

    inner class OverlayBinder : Binder() {
        val overlayDpLinesVertService: OverlayDpLinesVertService
            get() = this@OverlayDpLinesVertService
    }

    companion object {
        private const val CHANNEL_ID = "uk.co.barbuzz.appdevtoolbox"
        private const val UPDATE_EXTRA = "update_dp_lines"
        private const val CANCEL_EXTRA = "cancel_dplines_vert"
        private const val NOTIFICATION_ID = 2
        var isRunning = false

        fun getUpdateIntent(context: Context): Intent {
            val intent = Intent(context, OverlayDpLinesVertService::class.java)
            intent.putExtra(UPDATE_EXTRA, true)
            return intent
        }

    }
}
