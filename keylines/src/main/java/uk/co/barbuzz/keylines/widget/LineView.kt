package uk.co.barbuzz.keylines.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import uk.co.barbuzz.keylines.R

abstract class LineView : KeylineView {
    var strokeWidth = 0f
        set(value) {
            field = value
            paint.strokeWidth = value
            invalidate()
        }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun setupDefaultValues() {
        super.setupDefaultValues()
        strokeWidth = 1f
    }

    override fun readAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        super.readAttributes(attrs, defStyleAttr, defStyleRes)
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.LineView, defStyleAttr, defStyleRes)
        strokeWidth = try {
            a.getFloat(R.styleable.LineView_strokeWidthLv, strokeWidth)
        } finally {
            a.recycle()
        }
    }

    override fun initPaint() {
        super.initPaint()
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
    }
}