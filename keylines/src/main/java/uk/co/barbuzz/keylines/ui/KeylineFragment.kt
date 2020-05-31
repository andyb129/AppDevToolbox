package uk.co.barbuzz.keylines.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.services.*

class KeylineFragment : Fragment() {

    private lateinit var keylineToggleSwitch: Switch
    private lateinit var rulerHorizToggleSwitch: Switch
    private lateinit var rulerVertToggleSwitch: Switch
    private lateinit var dplinesHorizToggleSwitch: Switch
    private lateinit var dplinesVertToggleSwitch: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_keylines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkToggles()

        dplinesHorizToggleSwitch = view.findViewById(R.id.dplines_horiz_switch)
        dplinesHorizToggleSwitch.setOnClickListener {
            if (OverlayDpLinesHorizService.isRunning) {
                stopOverlayService(OverlayDpLinesHorizService::class.java)
            } else {
                startOverlayService(OverlayDpLinesHorizService::class.java)
            }
        }

        dplinesVertToggleSwitch = view.findViewById(R.id.dplines_vert_switch)
        dplinesVertToggleSwitch.setOnClickListener {
            if (OverlayDpLinesVertService.isRunning) {
                stopOverlayService(OverlayDpLinesVertService::class.java)
            } else {
                startOverlayService(OverlayDpLinesVertService::class.java)
            }
        }

        rulerHorizToggleSwitch = view.findViewById(R.id.ruler_horiz_switch)
        rulerHorizToggleSwitch.setOnClickListener {
            if (OverlayRulerHorizService.isRunning) {
                stopOverlayService(OverlayRulerHorizService::class.java)
            } else {
                startOverlayService(OverlayRulerHorizService::class.java)
            }
        }

        rulerVertToggleSwitch = view.findViewById(R.id.ruler_vert_switch)
        rulerVertToggleSwitch.setOnClickListener {
            if (OverlayRulerVertService.isRunning) {
                stopOverlayService(OverlayRulerVertService::class.java)
            } else {
                startOverlayService(OverlayRulerVertService::class.java)
            }
        }

        keylineToggleSwitch = view.findViewById(R.id.action_switch)
        keylineToggleSwitch.setOnClickListener {
            if (OverlayGridService.isRunning) {
                stopOverlayService(OverlayGridService::class.java)
            } else {
                startOverlayService(OverlayGridService::class.java)
            }
        }
    }

    private fun checkToggles() {
        if (OverlayDpLinesHorizService.isRunning && dplinesHorizToggleSwitch.isChecked) {
            dplinesHorizToggleSwitch.toggle()
        }
        if (OverlayDpLinesVertService.isRunning && dplinesVertToggleSwitch.isChecked) {
            dplinesVertToggleSwitch.toggle()
        }
        if (OverlayRulerHorizService.isRunning && rulerHorizToggleSwitch.isChecked) {
            rulerHorizToggleSwitch.toggle()
        }
        if (OverlayRulerVertService.isRunning && rulerVertToggleSwitch.isChecked) {
            rulerVertToggleSwitch.toggle()
        }
        if (OverlayGridService.isRunning && keylineToggleSwitch.isChecked) {
            keylineToggleSwitch.toggle()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MANAGE_OVERLAY_REQUEST_CODE) {
            if (rulerHorizToggleSwitch.isChecked) rulerHorizToggleSwitch.toggle()
            if (keylineToggleSwitch.isChecked) keylineToggleSwitch.toggle()
        }
    }

    private fun <T> startOverlayService(clazz: Class<T>) {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity?.packageName}"))
            startActivityForResult(intent,
                MANAGE_OVERLAY_REQUEST_CODE
            )
            return
        }
        activity?.startService(Intent(context, clazz))
    }

    private fun <T> stopOverlayService(clazz: Class<T>) {
        activity?.stopService(Intent(context, clazz))
    }

    companion object {
        private const val MANAGE_OVERLAY_REQUEST_CODE = 2
    }
}
