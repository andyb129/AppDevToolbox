package uk.co.barbuzz.keylines.services

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.utils.ColorUtil
import uk.co.barbuzz.keylines.widget.IrregularLineView
import uk.co.barbuzz.keylines.widget.KeylineView
import uk.co.barbuzz.keylines.widget.LineView
import uk.co.barbuzz.keylines.widget.RegularLineView

class OverlayPreference(private val context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun updateBaselineGridView(view: RegularLineView) {
        val enable = readEnable(preferences,
            R.string.pref_key_baseline_grid_enabled
        )
        setVisible(
            view,
            enable
        )
        if (enable) {
            updateOpacity(preferences,
                R.string.pref_key_baseline_grid_opacity, view)
            updateColor(preferences,
                R.string.pref_key_baseline_grid_color, view,
                R.color.pref_baseline_grid_default_color
            )
            updateSize(preferences,
                R.string.pref_key_baseline_grid_size, view,
                R.string.pref_value_size_small
            )
        }
    }

    fun updateIncrementGridView(view: RegularLineView) {
        val enable = readEnable(preferences,
            R.string.pref_key_increment_grid_enabled
        )
        setVisible(
            view,
            enable
        )
        if (enable) {
            updateOpacity(preferences,
                R.string.pref_key_increment_grid_opacity, view)
            updateColor(preferences,
                R.string.pref_key_increment_grid_color, view,
                R.color.pref_increment_grid_default_color
            )
            updateSize(preferences,
                R.string.pref_key_increment_grid_size, view,
                R.string.pref_value_size_medium
            )
            updateIncrementSize(preferences, view)
        }
    }

    fun updateTypographyLinesView(view: RegularLineView) {
        val enable = readEnable(preferences,
            R.string.pref_key_typography_lines_enabled
        )
        setVisible(
            view,
            enable
        )
        if (enable) {
            updateOpacity(preferences,
                R.string.pref_key_typography_lines_opacity, view)
            updateColor(preferences,
                R.string.pref_key_typography_lines_color, view,
                R.color.pref_typography_lines_default_color
            )
            updateSize(preferences,
                R.string.pref_key_typography_lines_size, view,
                R.string.pref_value_size_small
            )
        }
    }

    fun updateContentKeylinesView(view: IrregularLineView) {
        val enable = readEnable(preferences,
            R.string.pref_key_content_keylines_enabled
        )
        setVisible(
            view,
            enable
        )
        if (enable) {
            updateOpacity(preferences,
                R.string.pref_key_content_keylines_opacity, view)
            updateColor(preferences,
                R.string.pref_key_content_keylines_color, view,
                R.color.pref_content_keylines_default_color
            )
            updateSize(preferences,
                R.string.pref_key_content_keylines_size, view,
                R.string.pref_value_size_large
            )
        }
    }

    private fun readEnable(preferences: SharedPreferences, @StringRes keyStringId: Int): Boolean {
        return preferences.getBoolean(context.getString(keyStringId), false)
    }

    private fun updateOpacity(preferences: SharedPreferences, @StringRes keyStringId: Int,
                              view: KeylineView
    ) {
        val r = context.resources
        val defaultString = r.getString(R.string.pref_value_opacity_default)
        val opacityString = preferences.getString(r.getString(keyStringId), defaultString)
        val opacity = opacityString?.toInt()
        if (opacity != null) {
            view.opacity = opacity / 100f
        }
    }

    private fun updateColor(preferences: SharedPreferences, @StringRes keyStringId: Int,
                            view: KeylineView, @ColorRes defaultColorId: Int) {
        val r = context.resources
        val defaultColor = ColorUtil.getColor(r, context.theme, defaultColorId)
        val color = preferences.getInt(r.getString(keyStringId), defaultColor)
        view.color = color
    }

    private fun updateSize(preferences: SharedPreferences, @StringRes keyStringId: Int,
                           view: LineView, @StringRes defaultSizeId: Int) {
        val r = context.resources
        val defaultSizeString = r.getString(defaultSizeId)
        val sizeString = preferences.getString(r.getString(keyStringId), defaultSizeString)
        val size = sizeString?.toInt()
        if (size != null) {
            view.strokeWidth = size.toFloat()
        }
    }

    private fun updateIncrementSize(preferences: SharedPreferences,
                                    view: RegularLineView) {
        val r = context.resources
        val defaultSizeString = r.getString(R.string.pref_value_increment_size_auto)
        val key = r.getString(R.string.pref_key_increment_grid_increment_size)
        val sizeString = preferences.getString(key, defaultSizeString)
        val sizeDp = sizeString?.toInt()
        var size: Float = 0f
        if (sizeDp != null) {
            size = if (sizeDp != 0) {
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeDp.toFloat(), r.displayMetrics)
            } else {
                // Automatic
                r.getDimension(R.dimen.material_increment_1x)
            }
        }
        view.spacing = size
    }

    fun setDpLinesHorizColour(colour: Int) {
        with (preferences.edit()) {
            putInt(context.getString(R.string.pref_dp_lines_horiz_colour), colour)
            commit()
        }
    }

    fun getDpLinesHorizColour(): Int {
        return preferences.getInt(context.getString(R.string.pref_dp_lines_horiz_colour), 0) ?: 0
    }

    fun setDpLinesVertColour(colour: Int) {
        with (preferences.edit()) {
            putInt(context.getString(R.string.pref_dp_lines_vert_colour), colour)
            commit()
        }
    }

    fun getDpLinesVertColour(): Int {
        return preferences.getInt(context.getString(R.string.pref_dp_lines_vert_colour), 0) ?: 0
    }

    companion object {
        private fun setVisible(view: View, visible: Boolean) {
            val visibility = if (visible) View.VISIBLE else View.INVISIBLE
            view.visibility = visibility
        }
    }

}