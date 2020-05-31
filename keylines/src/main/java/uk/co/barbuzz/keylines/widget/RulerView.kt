package uk.co.barbuzz.keylines.widget


/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 *
 * https://github.com/kevalpatel2106/android-ruler-picker
 */

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.*
import uk.co.barbuzz.keylines.R

/**
 * Created by Keval Patel on 28 Mar 2018.
 *
 *
 * This is custom [View] which will draw a ruler with indicators.
 * There are two types of indicators:
 *  * **Long Indicators:** These indicators marks specific important value after some periodic interval.
 * e.g. Long indicator represents evert 10th (10, 20, 30...) value.
 *  * **Short Indicators:** There indicators represents single value.
 */
internal class RulerView : View {
    /**
     * Height of the view. This view height is measured in [.onMeasure].
     *
     * @see .onMeasure
     */
    private var mViewHeight = 0
    private var mViewWidth = 0

    /**
     * [Paint] for the line in the ruler view.
     *
     * @see .refreshPaint
     */
    private var mIndicatorPaint: Paint? = null

    /**
     * [Paint] to display the text on the ruler view.
     *
     * @see .refreshPaint
     */
    private var mTextPaint: Paint? = null

    /**
     * @return Get distance between two indicator in pixels.
     * @see .setIndicatorIntervalDistance
     */
    /**
     * Distance interval between two subsequent indicators on the ruler.
     *
     * @see .setIndicatorIntervalDistance
     * @see .getIndicatorIntervalWidth
     */
    var indicatorIntervalWidth = 14 /* Default value */
        private set

    /**
     * @return Get the minimum value displayed on the ruler.
     * @see .setValueRange
     */
    /**
     * Minimum value. This value will be displayed at the left-most end of the ruler. This value
     * must be less than [.mMaxValue].
     *
     * @see .setValueRange
     * @see .getMinValue
     */
    var minValue = 0 /* Default value */
        private set

    /**
     * @return Get the maximum value displayed on the ruler.
     * @see .setValueRange
     */
    /**
     * Maximum value. This value will be displayed at the right-most end of the ruler. This value
     * must be greater than [.mMinValue].
     *
     * @see .setValueRange
     * @see .getMaxValue
     */
    var maxValue = 100 /* Default maximum value */
        private set

    /**
     * interval value of long indicator
     */
    var intervalValue = 5 /* Default interval value */
        private set

    /**
     * is horizontal ruler or vertical
     */
    var isHorizontalRuler = true
        private set

    /**
     * @return Ratio of long indicator height to the ruler height.
     * @see .setIndicatorHeight
     */
    /**
     * Ratio of long indicator height to the ruler height. This value must be between 0 to 1. The
     * value should greater than [.mShortIndicatorHeight]. Default value is 0.6 (i.e. 60%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see .setIndicatorHeight
     * @see .getLongIndicatorHeightRatio
     */
    var longIndicatorHeightRatio = 0.6f /* Default value */
        private set

    /**
     * @return Ratio of short indicator height to the ruler height.
     * @see .setIndicatorHeight
     */
    /**
     * Ratio of short indicator height to the ruler height. This value must be between 0 to 1. The
     * value should less than [.mLongIndicatorHeight]. Default value is 0.4 (i.e. 40%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see .setIndicatorHeight
     * @see .getShortIndicatorHeightRatio
     */
    var shortIndicatorHeightRatio = 0.4f /* Default value */
        private set

    /**
     * Actual height of the long indicator in pixels. This height is derived from
     * [.mLongIndicatorHeightRatio].
     *
     * @see .updateIndicatorHeight
     */
    private var mLongIndicatorHeight = 0

    /**
     * Actual height of the short indicator in pixels. This height is derived from
     * [.mShortIndicatorHeightRatio].
     *
     * @see .updateIndicatorHeight
     */
    private var mShortIndicatorHeight = 0

    /**
     * Integer color of the text, that is displayed on the ruler.
     *
     * @see .setTextColor
     * @see .getTextColor
     */
    @ColorInt
    private var mTextColor = Color.WHITE

    /**
     * Integer color of the indicators.
     *
     * @see .setIndicatorColor
     * @see .getIndicatorColor
     */
    @ColorInt
    private var mIndicatorColor = Color.WHITE

    /**
     * Height of the text, that is displayed on ruler in pixels.
     *
     * @see .setTextSize
     * @see .getTextSize
     */
    @Dimension
    private var mTextSize = 36

    /**
     * @return Width of the indicator in pixels.
     * @see .setIndicatorWidth
     */
    /**
     * Width of the indicator in pixels.
     *
     * @see .setIndicatorWidth
     * @see .getIndicatorWidth
     */
    @get:CheckResult
    @Dimension
    var indicatorWidth = 4f
        private set

    constructor(@NonNull context: Context?) : super(context) {
        parseAttr(null)
    }

    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?
    ) : super(context, attrs) {
        parseAttr(attrs)
    }

    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttr(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttr(attrs)
    }

    private fun parseAttr(@Nullable attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.RulerView,
                0,
                0
            )
            try { //Parse params
                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
                    mTextColor = a.getColor(
                        R.styleable.RulerView_ruler_text_color,
                        Color.WHITE
                    )
                }
                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
                    mTextSize = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_size, 14)
                }
                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
                    mIndicatorColor = a.getColor(
                        R.styleable.RulerView_indicator_color,
                        Color.WHITE
                    )
                }
                if (a.hasValue(R.styleable.RulerView_indicator_width)) {
                    indicatorWidth = a.getDimensionPixelSize(
                        R.styleable.RulerView_indicator_width,
                        4
                    ).toFloat()
                }
                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
                    indicatorIntervalWidth = a.getDimensionPixelSize(
                        R.styleable.RulerView_indicator_interval,
                        4
                    )
                }
                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
                    longIndicatorHeightRatio = a.getFraction(
                        R.styleable.RulerView_long_height_height_ratio,
                        1, 1, 0.6f
                    )
                }
                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
                    shortIndicatorHeightRatio = a.getFraction(
                        R.styleable.RulerView_short_height_height_ratio,
                        1, 1, 0.4f
                    )
                }
                setIndicatorHeight(longIndicatorHeightRatio, shortIndicatorHeightRatio)
                if (a.hasValue(R.styleable.RulerView_min_value)) {
                    minValue = a.getInteger(R.styleable.RulerView_min_value, 0)
                }
                if (a.hasValue(R.styleable.RulerView_max_value)) {
                    maxValue = a.getInteger(R.styleable.RulerView_max_value, 100)
                }
                setValueRange(minValue, maxValue)
                if (a.hasValue(R.styleable.RulerView_interval_value)) {
                    intervalValue = a.getInteger(R.styleable.RulerView_interval_value, 5)
                }
                if (a.hasValue(R.styleable.RulerView_is_horizontal)) {
                    isHorizontalRuler = a.getBoolean(R.styleable.RulerView_is_horizontal, true)
                }
            } finally {
                a.recycle()
            }
        }
        refreshPaint()
    }

    /**
     * Create the indicator paint and value text color.
     */
    private fun refreshPaint() {
        mIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mIndicatorPaint!!.color = mIndicatorColor
        mIndicatorPaint!!.strokeWidth = indicatorWidth
        mIndicatorPaint!!.style = Paint.Style.STROKE
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.color = mTextColor
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.CENTER
        invalidate()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        //Iterate through all value
        for (value in 1 until maxValue - minValue) {
            if (value % intervalValue == 0) {
                drawLongIndicator(canvas, value)
                //get long interval value as dp count
                val indicatorDpWidth = px2dp(context, indicatorIntervalWidth.toFloat())
                drawValueText(canvas, value, indicatorDpWidth * value)
            } else {
                drawSmallIndicator(canvas, value)
            }
        }

        //Draw the first indicator.
        drawSmallIndicator(canvas, 0)

        //Draw the last indicator.
        drawSmallIndicator(canvas, width)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Measure dimensions
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec)
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val rulerLength = (maxValue - minValue - 1) * indicatorIntervalWidth
        updateIndicatorHeight(longIndicatorHeightRatio, shortIndicatorHeightRatio)
        if (isHorizontalRuler) {
            setMeasuredDimension(rulerLength, mViewHeight)
        } else {
            setMeasuredDimension(mViewWidth, rulerLength)
        }
    }

    /**
     * Calculate and update the height of the long and the short indicators based on new ratios.
     *
     * @param longIndicatorHeightRatio  Ratio of long indicator height to the ruler height.
     * @param shortIndicatorHeightRatio Ratio of short indicator height to the ruler height.
     */
    private fun updateIndicatorHeight(
        longIndicatorHeightRatio: Float,
        shortIndicatorHeightRatio: Float
    ) {
        val longIndicatorVal = if (isHorizontalRuler) {
            (mViewHeight * longIndicatorHeightRatio).toInt()
        } else {
            (mViewWidth * longIndicatorHeightRatio).toInt()
        }
        val shortIndicatorVal = if (isHorizontalRuler) {
            (mViewHeight * shortIndicatorHeightRatio).toInt()
        } else {
            (mViewWidth * shortIndicatorHeightRatio).toInt()
        }
        mLongIndicatorHeight = longIndicatorVal
        mShortIndicatorHeight = shortIndicatorVal
    }

    /**
     * Draw the vertical short line at every value.
     *
     * @param canvas [Canvas] on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private fun drawSmallIndicator(
        @NonNull canvas: Canvas,
        value: Int
    ) {
        val startX = if (isHorizontalRuler) {
            indicatorIntervalWidth * value.toFloat()
        } else {
            0f
        }
        val stopX = if (isHorizontalRuler) {
            indicatorIntervalWidth * value.toFloat()
        } else {
            mShortIndicatorHeight.toFloat()
        }
        val startY = if (isHorizontalRuler) {
            0f
        } else {
            indicatorIntervalWidth * value.toFloat()
        }
        val stopY = if (isHorizontalRuler) {
            mShortIndicatorHeight.toFloat()
        } else {
            indicatorIntervalWidth * value.toFloat()
        }
        canvas.drawLine(
            startX, startY,
            stopX, stopY,
            mIndicatorPaint!!
        )
    }

    /**
     * Draw the vertical long line.
     *
     * @param canvas [Canvas] on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private fun drawLongIndicator(
        @NonNull canvas: Canvas,
        value: Int
    ) {
        val startX = if (isHorizontalRuler) {
            indicatorIntervalWidth * value.toFloat()
        } else {
            0f
        }
        val stopX = if (isHorizontalRuler) {
            indicatorIntervalWidth * value.toFloat()
        } else {
            mLongIndicatorHeight.toFloat()
        }
        val startY = if (isHorizontalRuler) {
            0f
        } else {
            indicatorIntervalWidth * value.toFloat()
        }
        val stopY = if (isHorizontalRuler) {
            mLongIndicatorHeight.toFloat()
        } else {
            indicatorIntervalWidth * value.toFloat()
        }
        canvas.drawLine(
            startX, startY,
            stopX, stopY,
            mIndicatorPaint!!
        )
    }

    /**
     * Draw the value number below the longer indicator. This will use [.mTextPaint] to draw
     * the text.
     *
     * @param canvas [Canvas] on which the text will be drawn.
     * @param value  Value to draw.
     */
    private fun drawValueText(
        @NonNull canvas: Canvas,
        value: Int,
        textValue: Int
    ) {
        val startX = if (isHorizontalRuler) {
            indicatorIntervalWidth * value.toFloat()
        } else {
            mLongIndicatorHeight + mTextPaint!!.textSize
        }
        val startY = if (isHorizontalRuler) {
            mLongIndicatorHeight + mTextPaint!!.textSize
        } else {
            indicatorIntervalWidth * value.toFloat()
        }
        if (isHorizontalRuler) {
            canvas.rotate(90f, startX, startY);
        }
        canvas.drawText(
            (textValue + minValue).toString(),
            startX,
            startY,
            mTextPaint!!
        )
        if (isHorizontalRuler) {
            canvas.rotate(-90f, startX, startY);
        }
    }
    /////////////////////// Properties getter/setter ///////////////////////
    /**
     * @return Color integer value of the ruler text color.
     * @see .setTextColor
     */
    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     */
    @get:ColorInt
    @get:CheckResult
    var textColor: Int
        get() = mIndicatorColor
        set(color) {
            mTextColor = color
            refreshPaint()
        }

    /**
     * @return Size of the text of ruler in pixels.
     * @see .setTextSize
     */
    @get:CheckResult
    val textSize: Float
        get() = mTextSize.toFloat()

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param textSizeSp Text size dimension in dp.
     */
    fun setTextSize(textSizeSp: Int) {
        mTextSize = sp2px(context, textSizeSp.toFloat())
        refreshPaint()
    }

    /**
     * @return Color integer value of the indicator color.
     * @see .setIndicatorColor
     */
    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     */
    @get:ColorInt
    @get:CheckResult
    var indicatorColor: Int
        get() = mIndicatorColor
        set(color) {
            mIndicatorColor = color
            refreshPaint()
        }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     */
    fun setIndicatorWidth(widthPx: Int) {
        indicatorWidth = widthPx.toFloat()
        refreshPaint()
    }

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     * or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     * or zero.This value must be greater than min value. Default minimum value is 100.
     */
    fun setValueRange(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue
        invalidate()
    }

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @throws IllegalArgumentException if interval is negative or zero.
     */
    fun setIndicatorIntervalDistance(indicatorIntervalPx: Int) {
        require(indicatorIntervalPx > 0) { "Interval cannot be negative or zero." }
        indicatorIntervalWidth = indicatorIntervalPx
        invalidate()
    }

    /**
     * Set the height of the long and short indicators.
     *
     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
     * be between 0 to 1. The value should greater than [.mShortIndicatorHeight].
     * Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
     * be displayed. If the value is 1, indicator height will be same as the
     * ruler height.
     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
     * be between 0 to 1. The value should less than [.mLongIndicatorHeight].
     * Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
     * be displayed. If the value is 1, indicator height will be same as
     * the ruler height.
     * @throws IllegalArgumentException if any of the parameter is invalid.
     */
    fun setIndicatorHeight(
        longHeightRatio: Float,
        shortHeightRatio: Float
    ) {
        require(!(shortHeightRatio < 0 || shortHeightRatio > 1)) { "Sort indicator height must be between 0 to 1." }
        require(!(longHeightRatio < 0 || longHeightRatio > 1)) { "Long indicator height must be between 0 to 1." }
        require(shortHeightRatio <= longHeightRatio) { "Long indicator height cannot be less than sort indicator height." }
        longIndicatorHeightRatio = longHeightRatio
        shortIndicatorHeightRatio = shortHeightRatio
        updateIndicatorHeight(longIndicatorHeightRatio, shortIndicatorHeightRatio)
        invalidate()
    }

    /**
     * Convert SP to pixel.
     *
     * @param context Context.
     * @param spValue Value in sp to convert.
     *
     * @return Value in pixels.
     */
    private fun sp2px(
        @NonNull context: Context,
        spValue: Float
    ): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun px2dp(
        @NonNull context: Context,
        pxValue: Float
    ): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}