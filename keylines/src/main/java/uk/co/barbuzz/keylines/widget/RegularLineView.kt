package uk.co.barbuzz.keylines.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import uk.co.barbuzz.keylines.R

class RegularLineView : LineView {
    var spacing = 0f
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun setupDefaultValues() {
        super.setupDefaultValues()
        val r = resources
        spacing = r.getDimension(R.dimen.material_baseline_grid_1x)
    }

    override fun readAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        super.readAttributes(attrs, defStyleAttr, defStyleRes)
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.RegularLineView, defStyleAttr, defStyleRes)
        spacing = try {
            a.getDimension(R.styleable.RegularLineView_spacing, spacing)
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val spacing = spacing
        val direction = direction
        val drawVertical = direction == KeylineView.Companion.DIRECTION_VERTICAL || direction == KeylineView.Companion.DIRECTION_BOTH
        val drawHorizontal = direction == KeylineView.Companion.DIRECTION_HORIZONTAL || direction == KeylineView.Companion.DIRECTION_BOTH
        if (drawVertical) {
            var i = 0
            while (i * spacing < width) {
                canvas.drawLine(i * spacing, 0f, i * spacing, height.toFloat(), paint)
                i++
            }
        }
        if (drawHorizontal) {
            var i = 0
            while (i * spacing <= height) {
                canvas.drawLine(0f, i * spacing, width.toFloat(), i * spacing, paint)
                i++
            }
        }
    }
}