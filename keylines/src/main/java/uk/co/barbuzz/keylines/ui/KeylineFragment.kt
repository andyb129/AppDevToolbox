package uk.co.barbuzz.keylines.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import uk.co.barbuzz.keylines.R
import uk.co.barbuzz.keylines.services.*
import uk.co.barbuzz.keylines.utils.ColorUtil.getColor


class KeylineFragment : Fragment() {

    private lateinit var keylineToggleSwitch: Switch
    private lateinit var rulerHorizToggleSwitch: Switch
    private lateinit var rulerVertToggleSwitch: Switch
    private lateinit var dplinesHorizToggleSwitch: Switch
    private lateinit var dplinesVertToggleSwitch: Switch
    private lateinit var dplinesHorizColourSelector: View
    private lateinit var dplinesVertColourSelector: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_keylines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = activity?.getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText )
            inputMethodManager.hideSoftInputFromWindow( activity?.currentFocus?.windowToken,0)

        setupKeylineTools(view)

        checkToggles()
    }

    private fun checkToggles() {
        if (OverlayDpLinesHorizService.isRunning && !dplinesHorizToggleSwitch.isChecked) {
            dplinesHorizToggleSwitch.toggle()
        }
        if (OverlayDpLinesVertService.isRunning && !dplinesVertToggleSwitch.isChecked) {
            dplinesVertToggleSwitch.toggle()
        }
        if (OverlayRulerHorizService.isRunning && !rulerHorizToggleSwitch.isChecked) {
            rulerHorizToggleSwitch.toggle()
        }
        if (OverlayRulerVertService.isRunning && !rulerVertToggleSwitch.isChecked) {
            rulerVertToggleSwitch.toggle()
        }
        if (OverlayGridService.isRunning && !keylineToggleSwitch.isChecked) {
            keylineToggleSwitch.toggle()
        }
    }

    private fun setupKeylineTools(view: View) {
        dplinesHorizToggleSwitch = view.findViewById(R.id.dplines_horiz_switch)
        dplinesHorizToggleSwitch.setOnClickListener {
            if (OverlayDpLinesHorizService.isRunning) {
                stopOverlayService(OverlayDpLinesHorizService::class.java)
            } else {
                startOverlayService(OverlayDpLinesHorizService::class.java)
            }
        }

        dplinesHorizColourSelector = view.findViewById(R.id.dplines_horiz_colour)
        dplinesHorizColourSelector.setOnClickListener {
            ColorPickerDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Dp Lines Colour")
                .setPreferenceName("DpColourPicker")
                .setPositiveButton(getString(R.string.confirm), ColorEnvelopeListener { envelope, fromUser -> setDpLinesHorizColour(envelope) })
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> dialogInterface.dismiss() }
                .show()
        }

        dplinesVertToggleSwitch = view.findViewById(R.id.dplines_vert_switch)
        dplinesVertToggleSwitch.setOnClickListener {
            if (OverlayDpLinesVertService.isRunning) {
                stopOverlayService(OverlayDpLinesVertService::class.java)
            } else {
                startOverlayService(OverlayDpLinesVertService::class.java)
            }
        }

        dplinesVertColourSelector = view.findViewById(R.id.dplines_vert_colour)
        dplinesVertColourSelector.setOnClickListener {
            ColorPickerDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Dp Lines Colour")
                .setPreferenceName("DpColourPicker")
                .setPositiveButton(getString(R.string.confirm), ColorEnvelopeListener { envelope, fromUser -> setDpLinesVertColour(envelope) })
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> dialogInterface.dismiss() }
                .show()
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

        setDpLinesColourSquares()
    }

    private fun setDpLinesColourSquares() {
        val overlayPref = OverlayPreference(activity!!)
        if (overlayPref.getDpLinesHorizColour() == 0) {
            overlayPref.setDpLinesHorizColour(ContextCompat.getColor(activity!!, R.color.dp_lines_default_colour))
        }
        if (overlayPref.getDpLinesVertColour() == 0) {
            overlayPref.setDpLinesVertColour(ContextCompat.getColor(activity!!, R.color.dp_lines_default_colour))
        }
        dplinesHorizColourSelector.setBackgroundColor(overlayPref.getDpLinesHorizColour())
        dplinesVertColourSelector.setBackgroundColor(overlayPref.getDpLinesVertColour())
    }

    private fun setDpLinesVertColour(envelope: ColorEnvelope?) {
        val overlayPref = OverlayPreference(activity!!)
        overlayPref.setDpLinesVertColour(envelope?.color ?: 0)
        dplinesVertColourSelector.setBackgroundColor(envelope?.color ?: 0)
        if (OverlayDpLinesVertService.isRunning) {
            activity?.startService(OverlayDpLinesVertService.getUpdateIntent(activity!!))
        }
    }

    private fun setDpLinesHorizColour(envelope: ColorEnvelope?) {
        val overlayPref = OverlayPreference(activity!!)
        overlayPref.setDpLinesHorizColour(envelope?.color ?: 0)
        dplinesHorizColourSelector.setBackgroundColor(envelope?.color ?: 0)
        if (OverlayDpLinesHorizService.isRunning) {
            activity?.startService(OverlayDpLinesHorizService.getUpdateIntent(activity!!))
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
