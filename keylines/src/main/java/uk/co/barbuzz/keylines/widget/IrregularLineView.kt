package uk.co.barbuzz.keylines.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class IrregularLineView : LineView {
    lateinit var coordinates: FloatArray

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (coordinates == null) {
            return
        }
        val direction = direction
        val drawVertical = direction == KeylineView.Companion.DIRECTION_VERTICAL || direction == KeylineView.Companion.DIRECTION_BOTH
        val drawHorizontal = direction == KeylineView.Companion.DIRECTION_HORIZONTAL || direction == KeylineView.Companion.DIRECTION_BOTH
        for (coordinate in coordinates!!) {
            if (drawVertical) {
                canvas.drawLine(coordinate, 0f, coordinate, height.toFloat(), paint)
            }
            if (drawHorizontal) {
                canvas.drawLine(0f, coordinate, width.toFloat(), coordinate, paint)
            }
        }
    }
}