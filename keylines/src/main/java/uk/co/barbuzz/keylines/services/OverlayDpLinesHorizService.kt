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


class OverlayDpLinesHorizService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var viewDpLinesHorizontal: View
    private lateinit var viewDpLinesButtons: View
    private lateinit var buttonText: TextView
    private lateinit var spaceGuidline: View
    private lateinit var topGuideline: View
    private lateinit var bottomGuideline: View
    private lateinit var spaceGuidlineLayout: View
    private lateinit var arrowTop: ImageView
    private lateinit var arrowBottom: ImageView
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
            showDpLinesOverlayHorizontal()
            dragAndDropHorizontalDpLines()
            showDpLinesButton()
            dragAndDropDpLinesButtons()

            isRunning = true
        } else if (intent.hasExtra(UPDATE_EXTRA)) {
            setDpLinesColour()
        } else if (intent?.action == CANCEL_EXTRA) {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showDpLinesOverlayHorizontal() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        viewDpLinesHorizontal = LayoutInflater.from(this).inflate(R.layout.overlay_dplines_horizontal, null)

        topGuideline = viewDpLinesHorizontal.findViewById<View>(R.id.top_guideline)
        bottomGuideline = viewDpLinesHorizontal.findViewById<View>(R.id.bottom_guideline)
        spaceGuidlineLayout = viewDpLinesHorizontal.findViewById<View>(R.id.space_guideline_layout)
        spaceGuidline = viewDpLinesHorizontal.findViewById<View>(R.id.space_guideline)
        arrowTop = viewDpLinesHorizontal.findViewById<ImageView>(R.id.arrow_top_guideline)
        arrowBottom = viewDpLinesHorizontal.findViewById<ImageView>(R.id.arrow_bottom_guideline)
        spaceText = viewDpLinesHorizontal.findViewById<TextView>(R.id.space_text_guideline)

        setDpLinesColour()

        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0, 0,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        windowManager.addView(viewDpLinesHorizontal, params)
    }

    private fun dragAndDropHorizontalDpLines() {
        viewDpLinesHorizontal.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                xOffset = v.width / 2
                yOffset = v.height / 2
                val x = event.rawX.toInt() - xOffset
                val y = event.rawY.toInt() - yOffset
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    x, y,
                    layoutFlag,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.LEFT
                windowManager.updateViewLayout(viewDpLinesHorizontal, params)
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
        viewDpLinesButtons.findViewById<TextView>(R.id.button_label).text = getString(R.string.db_line_horiz_buttons_label)

        readPreferences()
        layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        val x = screenWidth/2 * -1
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

    private fun setDpLinesColour() {
        val dpLinesHorizColour = OverlayPreference(this).getDpLinesHorizColour()
        spaceGuidline.setBackgroundColor(dpLinesHorizColour)
        topGuideline.setBackgroundColor(dpLinesHorizColour)
        bottomGuideline.setBackgroundColor(dpLinesHorizColour)
    }

    private fun increaseDpLinesSpacing() {
        val newSpacing = dpLinesSpacing + 1
        if (newSpacing < screenWidth) {
            dpLinesSpacing = newSpacing
        }
        if (newSpacing > 32) {
            arrowTop.visibility = View.VISIBLE
            arrowBottom.visibility = View.VISIBLE
        } else {
            arrowTop.visibility = View.GONE
            arrowBottom.visibility = View.GONE
        }
        setDpLinesSpacing()
    }

    private fun decreaseDpLinesSpacing() {
        val newSpacing = dpLinesSpacing - 1
        if (newSpacing > 0) {
            dpLinesSpacing = newSpacing
        }
        if (newSpacing > 32) {
            arrowTop.visibility = View.VISIBLE
            arrowBottom.visibility = View.VISIBLE
        } else {
            arrowTop.visibility = View.GONE
            arrowBottom.visibility = View.GONE
        }
        setDpLinesSpacing()
    }

    private fun moveLabelUp() {
        val layoutParamsArrows = getArrowsLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.1f
        if (spaceBias == 0.9f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsArrows.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineLayout.layoutParams = layoutParamsArrows
        spaceText.layoutParams = layoutParamsText
    }

    private fun moveLabelDown() {
        val layoutParamsArrows = getArrowsLayoutParam()
        val layoutParamsText = getTextLayoutParam()

        var bias = 0.9f
        if (spaceBias == 0.1f) {
            bias = 0.5f
        }
        spaceBias = bias
        layoutParamsArrows.verticalBias = bias
        layoutParamsText.verticalBias = bias
        spaceGuidlineLayout.layoutParams = layoutParamsArrows
        spaceText.layoutParams = layoutParamsText
    }

    private fun setDpLinesSpacing() {
        spaceText.text = "${dpLinesSpacing}dp"
        val layoutParamsSpace = getSpaceLayoutParam()
        layoutParamsSpace.verticalBias = spaceBias
        spaceGuidline.layoutParams = layoutParamsSpace
    }

    private fun getArrowsLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsArrows = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsArrows.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrows.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsArrows.topToBottom = R.id.top_guideline
        return layoutParamsArrows
    }

    private fun getSpaceLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsSpace = ConstraintLayout.LayoutParams(
            2.px,
            dpLinesSpacing.px
        )
        layoutParamsSpace.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsSpace.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsSpace.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsSpace.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        return layoutParamsSpace
    }

    private fun getTextLayoutParam(): ConstraintLayout.LayoutParams {
        val layoutParamsText = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsText.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsText.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParamsText.topToBottom = R.id.bottom_guideline
        layoutParamsText.setMargins(0, 16.dp, 0, 0)
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
            val mChannel = NotificationChannel(OverlayDpLinesHorizService.CHANNEL_ID, "DpLinesVert Channel", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "DpLines Horiz Overlay channel for ongoing notification"
            notificationManager!!.createNotificationChannel(mChannel)
            builder = Notification.Builder(this, OverlayDpLinesHorizService.CHANNEL_ID)
        } else {
            builder = Notification.Builder(this)
        }
        val notification = builder
            .setSmallIcon(R.drawable.ic_keylines)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(getText(R.string.active_overlay_dplines_horiz))
            .addAction(createAction(OverlayDpLinesHorizService.CANCEL_EXTRA, "Cancel"))
            .setOngoing(true)
            .build()
        notificationManager.notify(OverlayDpLinesHorizService.NOTIFICATION_ID, notification)
    }

    private fun createAction(action: String, text: String): Notification.Action {
        val intent = Intent(this, OverlayDpLinesHorizService::class.java).setAction(action)
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
        if (viewDpLinesHorizontal != null) {
            windowManager.removeView(viewDpLinesHorizontal)
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
        val overlayDpLinesService: OverlayDpLinesHorizService
            get() = this@OverlayDpLinesHorizService
    }

    companion object {
        private const val CHANNEL_ID = "uk.co.barbuzz.appdevtoolbox"
        private const val UPDATE_EXTRA = "update_dp_lines"
        private const val CANCEL_EXTRA = "cancel_dpline_horiz"
        private const val NOTIFICATION_ID = 3
        var isRunning = false

        fun getUpdateIntent(context: Context): Intent {
            val intent = Intent(context, OverlayDpLinesHorizService::class.java)
            intent.putExtra(UPDATE_EXTRA, true)
            return intent
        }

    }
}
