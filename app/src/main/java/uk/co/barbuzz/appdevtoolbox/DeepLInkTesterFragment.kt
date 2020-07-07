package uk.co.barbuzz.appdevtoolbox

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class DeepLInkTesterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deep_link_tester, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentLayout = view.findViewById<ConstraintLayout>(R.id.deep_link_parent_layout)
        val editText = view.findViewById<TextInputEditText>(R.id.deep_link_uri_edit_text)
        view.findViewById<Button>(R.id.deep_link_clear_button).setOnClickListener {
            editText.setText("")
        }
        view.findViewById<Button>(R.id.deep_link_launch_button).setOnClickListener {
            val uriString = editText.text.toString()
            when (uriString.isBlank()) {
                true -> {
                    Snackbar.make(parentLayout, getString(R.string.deep_link_blank_error), Snackbar.LENGTH_LONG)
                        .show()
                }
                else -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(uriString)
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(
                            parentLayout,
                            getString(R.string.deep_link_no_activity_for_intent_error),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }
}
