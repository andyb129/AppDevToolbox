package uk.co.barbuzz.snippet.service

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import uk.co.barbuzz.snippet.R

class OverlaySnippetPastePreference(private val context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setPastTextCoordinates(overlayCoordinates: Pair<Int, Int>) {
        with (preferences.edit()) {
            putInt(context.getString(R.string.pref_key_paste_text_overlay_x_coord), overlayCoordinates.first)
            putInt(context.getString(R.string.pref_key_paste_text_overlay_y_coord), overlayCoordinates.second)
            commit()
        }
    }

    fun getPasteTextCoordinates(): Pair<Int, Int> {
        return Pair(
            preferences.getInt(context.getString(R.string.pref_key_paste_text_overlay_x_coord), 0),
            preferences.getInt(context.getString(R.string.pref_key_paste_text_overlay_y_coord), 0)
        )
    }

    fun setPasteText(pasteText: String) {
        with (preferences.edit()) {
            putString(context.getString(R.string.pref_key_paste_text), pasteText)
            commit()
        }
    }

    fun getPasteText(): String {
        return preferences.getString(context.getString(R.string.pref_key_paste_text), "") ?: ""
    }

}
