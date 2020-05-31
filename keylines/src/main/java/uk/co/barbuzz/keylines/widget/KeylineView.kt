package uk.co.barbuzz.keylines.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.utils.ColorUtil

open class KeylineView : View {

    protected var paint: Paint = Paint()
    var direction = 0
    var color = 0
        set(value) {
            field = value
            paint.color = color
            paint.alpha = (opacity * 255).toInt()
            invalidate()
        }
    var opacity = 0f
        set(value) {
            field = value
            paint.alpha = (opacity * 255).toInt()
            invalidate()
        }

    constructor(context: Context?) : super(context) {
        setupDefaultValues()
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupDefaultValues()
        readAttributes(attrs, 0, 0)
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupDefaultValues()
        readAttributes(attrs, defStyleAttr, 0)
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        setupDefaultValues()
        readAttributes(attrs, defStyleAttr, defStyleRes)
        initPaint()
    }

    protected open fun setupDefaultValues() {
        val r = resources
        color = ColorUtil.getColor(r, context.theme, R.color.material_color_red_500)
        opacity = 0.5f
        direction = DIRECTION_VERTICAL
    }

    protected open fun readAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.KeylineView, defStyleAttr, defStyleRes)
        try {
            color = a.getColor(R.styleable.KeylineView_color, color)
            opacity = a.getFloat(R.styleable.KeylineView_opacity, opacity)
            direction = a.getInt(R.styleable.KeylineView_direction, direction)
        } finally {
            a.recycle()
        }
    }

    protected open fun initPaint() {
        paint.color = color
        paint.alpha = (opacity * 255).toInt()
    }

    companion object {
        const val DIRECTION_VERTICAL = 0
        const val DIRECTION_HORIZONTAL = 1
        const val DIRECTION_BOTH = 2
    }
}