package uk.co.barbuzz.devtiles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.co.barbuzz.devtiles.model.Feature
import uk.co.barbuzz.devtiles.ui.FeaturesAdapter

class DevTilesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_devtiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val featuresRecyclerView = view.findViewById<RecyclerView>(R.id.featuresRecyclerView)
        featuresRecyclerView.layoutManager = LinearLayoutManager(context)
        featuresRecyclerView.setHasFixedSize(true)
        featuresRecyclerView.adapter = FeaturesAdapter(getFeaturesList()) {}

        val shareButton = view.findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener { sharePermissionsCommand() }
    }

    private fun getFeaturesList(): List<Feature> {
        return listOf(
            Feature("Toggle USB Debugging", "Enable/disable USB debugging from your notification drawer", R.drawable.ic_toggle_usb_debugging),
            Feature("Keep Screen On", "Keep screen on when connected via USB, but turn it off when connected to a charger", R.drawable.ic_toggle_keep_screen_on),
            Feature("Show Touches", "Show touch points when you touch the screen, ideal for demos", R.drawable.ic_toggle_show_taps),
            Feature("Demo Mode", "Cleans up the status bar for those perfect screenshots", R.drawable.ic_toggle_demo_mode),
            Feature("Change Animator Duration", "Change the default animator duration to easily debug animations", R.drawable.ic_animator_duration),
            Feature("Toggle Animation Scale", "Enable/disable all animations with one click, perfect for running Espresso tests", R.drawable.ic_animation)
        )
    }

    private fun share(resId: Int) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(resId))
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun sharePermissionsCommand() {
        share(R.string.permission_command)
    }
}