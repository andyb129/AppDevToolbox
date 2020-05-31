package uk.co.barbuzz.keylines.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import uk.co.barbuzz.keylines.R


class OverlayDpLinesVertService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var viewDpLinesVertical: View
    private lateinit var viewDpLinesButtons: View
    private lateinit var buttonText: TextView
    private lateinit var spaceGuidlineVert: View
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
            //readPreferences()
        } else if (intent?.action == CANCEL_EXTRA) {
            stopSelf()
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

        spaceGuidlineVert = viewDpLinesVertical.findViewById<View>(R.id.space_guideline_vert)
        arrowLeft = viewDpLinesVertical.findViewById<ImageView>(R.id.arrow_left_guideline_vert)
        arrowRight = viewDpLinesVertical.findViewById<ImageView>(R.id.arrow_right_guideline_vert)
        spaceText = viewDpLinesVertical.findViewById<TextView>(R.id.space_text_guideline_vert)

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
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
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

        viewDpLinesButtons.findViewById<ImageView>(R.id.plus_button).setOnClickListener{
            increaseDpLinesSpacing()
        }

        viewDpLinesButtons.findViewById<ImageView>(R.id.minus_button).setOnClickListener{
            decreaseDpLinesSpacing()
        }

        viewDpLinesButtons.findViewById<ImageView>(R.id.up_button).setOnClickListener{
            moveLabelUp()
        }

        viewDpLinesButtons.findViewById<ImageView>(R.id.down_button).setOnClickListener{
            moveLabelDown()
        }
        viewDpLinesButtons.findViewById<TextView>(R.id.button_label).text = "Vertical"

        readPreferences()
        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val x = screenWidth - viewDpLinesButtons.width
        val y = screenHeight - (viewDpLinesButtons.height + (viewDpLinesButtons.height/2))
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            x, y,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
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
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.LEFT
                windowManager.updateViewLayout(viewDpLinesButtons, params)
                return@OnTouchListener true
            }
            false
        })
    }

    private fun increaseDpLinesSpacing() {
        val newSpacing = dpLinesSpacing + 8
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
        val newSpacing = dpLinesSpacing - 8
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
        val layoutParamsArrowLeft = getLeftArrowLayoutParam()
        val layoutParamsArrowRight = getRightArrowLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.1f
        if (spaceBias == 0.9f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsSpace.verticalBias = bias
        layoutParamsArrowLeft.verticalBias = bias
        layoutParamsArrowRight.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineVert.layoutParams = layoutParamsSpace
        arrowLeft.layoutParams = layoutParamsArrowLeft
        arrowRight.layoutParams = layoutParamsArrowRight
        spaceText.layoutParams = layoutParamsText
    }

    private fun moveLabelDown() {
        val layoutParamsSpace = getSpaceLayoutParam()
        val layoutParamsArrowLeft = getLeftArrowLayoutParam()
        val layoutParamsArrowRight = getRightArrowLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.9f
        if (spaceBias == 0.1f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsSpace.verticalBias = bias
        layoutParamsArrowLeft.verticalBias = bias
        layoutParamsArrowRight.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineVert.layoutParams = layoutParamsSpace
        arrowLeft.layoutParams = layoutParamsArrowLeft
        arrowRight.layoutParams = layoutParamsArrowRight
        spaceText.layoutParams = layoutParamsText
    }

    private fun setDpLinesSpacing() {
        spaceText.text = "${dpLinesSpacing}dp"
        val layoutParamsSpace = getSpaceLayoutParam()
        layoutParamsSpace.verticalBias = spaceBias
        spaceGuidlineVert.layoutParams = layoutParamsSpace
    }

    private fun getSpaceLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsSpace = ConstraintLayout.LayoutParams(
            dpLinesSpacing.px,
            2.px
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

    private fun getRightArrowLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsArrowLRight = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsArrowLRight.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrowLRight.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrowLRight.endToStart = R.id.bottom_guideline_vert
        return layoutParamsArrowLRight
    }

    private fun getLeftArrowLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsArrowLeft = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsArrowLeft.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrowLeft.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrowLeft.startToEnd = R.id.top_guideline_vert
        return layoutParamsArrowLeft
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