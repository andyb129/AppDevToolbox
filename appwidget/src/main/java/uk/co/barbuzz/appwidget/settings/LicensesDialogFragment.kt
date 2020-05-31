package uk.co.barbuzz.appwidget.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import uk.co.barbuzz.appwidget.R

internal class LicensesDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams")
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null)

        val licenses = view.findViewById<WebView>(R.id.licenses)
        licenses.loadUrl("file:///android_asset/open_source_licenses.html")

        return AlertDialog.Builder(activity!!)
            .setTitle(R.string.pref_title_open_source)
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }

    companion object {

        fun newInstance() = LicensesDialogFragment()
    }
}
