package uk.co.barbuzz.appdevtoolbox.intro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import uk.co.barbuzz.appdevtoolbox.MainActivity
import uk.co.barbuzz.appdevtoolbox.R

class IntroFragment : Fragment() {

    private var index: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            index = getInt(ARG_OBJECT)
        }

        val title = view.findViewById<TextView>(R.id.intro_settings_title)
        val desc = view.findViewById<TextView>(R.id.intro_settings_desc)
        val button = view.findViewById<TextView>(R.id.intro_settings_button)
        var successMsg = view.findViewById<TextView>(R.id.intro_settings_success_message)
        var titleSuccess1 = view.findViewById<TextView>(R.id.intro_settings_enabled_title)
        var titleSuccess2 = view.findViewById<TextView>(R.id.intro_settings_enabled_title_2)
        when(index) {
            2 -> {
                titleSuccess1.visibility = View.VISIBLE
                title.text = getString(R.string.accessibility_setting_title)
                desc.text = getString(R.string.accessibility_setting_desc)
                button.text = getString(R.string.accessibility_setting_button)
            }
            3 -> {
                successMsg.visibility = View.VISIBLE
                titleSuccess1.visibility = View.VISIBLE
                titleSuccess2.visibility = View.VISIBLE
                title.visibility = View.GONE
                desc.visibility = View.GONE
                button.text = getString(R.string.complete_setting_button)
            }
        }
        button.setOnClickListener{
            when(index) {
                1 -> {
                    activity?.startActivityForResult(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${activity?.packageName}")
                        ), OVERLAY_REQUEST_CODE
                    )
                }
                2 -> {
                    activity?.startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), ACCESSIBILITY_REQUEST_CODE)
                }
                else -> {
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

}

