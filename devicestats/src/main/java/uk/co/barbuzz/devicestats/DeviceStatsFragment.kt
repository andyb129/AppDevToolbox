package uk.co.barbuzz.devicestats

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import java.io.IOException
import kotlin.coroutines.CoroutineContext


class DeviceStatsFragment : Fragment() {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private lateinit var viewPager: ViewPager
    private lateinit var mDeviceStats: DeviceStats
    private lateinit var mDeviceStatsString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initStats()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager) as ViewPager
        val tabLayout = view.findViewById(R.id.tabs) as TabLayout
        val adapter = DeviceStatsAdapter(requireContext(), fragmentManager, mDeviceStats)
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        //TODO - maybe move this share back to action bar menu as does get in the way of data
        val fab = view.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { shareAllDeviceStatsText() }
    }

    private fun initStats() {
        //TODO - move to background thread
        val gatherer = StatsGatherer(requireActivity())
        mDeviceStatsString = gatherer.printStatsToString()
        mDeviceStats = gatherer.deviceStats
    }

    private fun shareAllDeviceStatsText() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, mDeviceStatsString)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.default_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId.toLong()
        if (R.id.menu_save_to_sd.toLong() == id) {
            try {
                Utils.dumpDataToSD(filename, mDeviceStatsString)
                Toast.makeText(
                    context, "Saved to $filename",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context, "Failed to save.",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        } else if (R.id.menu_screenshot.toLong() == id) {
            //startActivity(context?.let { DeviceWallpaperActivity.createIntent(it, mDeviceStats) })
            setScreenAsWallpaper()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setScreenAsWallpaper() {
        var hasSetWallpaper = false
        scope.async(Dispatchers.IO) {
            val success = async { setDeviceWallpaper() }
            hasSetWallpaper = success.await()
        }
        if (hasSetWallpaper) {
            Toast.makeText(activity, "Wallpaper captured!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(activity, "Error setting Wallpaper", Toast.LENGTH_LONG).show()
        }

    }

    private fun setDeviceWallpaper(): Boolean {
        val bitmap: Bitmap? = takeScreenShot(viewPager)
        val wallpaperManager =
            WallpaperManager.getInstance(activity)
        try {
            wallpaperManager.setBitmap(bitmap)
        } catch (e: IOException) {
            return false
        }
        return true
    }

    private fun takeScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return bitmap
    }

    private class WallpaperObserver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //Toast.makeText(context, "Wallpaper set", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val filename = "devicestats.txt"
    }
}
