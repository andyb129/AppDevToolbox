package uk.co.barbuzz.keylines.utils

import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 *
 *
 * Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 *
 * https://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action
 */
class RepeatListener(
    initialInterval: Int, normalInterval: Int,
    clickListener: View.OnClickListener?
) : OnTouchListener {
    private val handler = Handler()
    private val initialInterval: Int
    private val normalInterval: Int
    private val clickListener: View.OnClickListener
    private var touchedView: View? = null
    private val handlerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (touchedView!!.isEnabled) {
                handler.postDelayed(this, normalInterval.toLong())
                clickListener!!.onClick(touchedView)
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(this)
                touchedView!!.isPressed = false
                touchedView = null
            }
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, initialInterval.toLong())
                touchedView = view
                touchedView!!.isPressed = true
                clickListener.onClick(view)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(handlerRunnable)
                touchedView!!.isPressed = false
                touchedView = null
                return true
            }
        }
        return false
    }

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval The interval after second and subsequent click
     * events
     * @param clickListener The OnClickListener, that will be called
     * periodically
     */
    init {
        requireNotNull(clickListener) { "null runnable" }
        require(!(initialInterval < 0 || normalInterval < 0)) { "negative interval" }
        this.initialInterval = initialInterval
        this.normalInterval = normalInterval
        this.clickListener = clickListener
    }
}
