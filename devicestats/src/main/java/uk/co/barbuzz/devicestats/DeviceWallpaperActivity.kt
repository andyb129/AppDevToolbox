package uk.co.barbuzz.devicestats

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import java.io.IOException
import kotlin.coroutines.CoroutineContext


class DeviceWallpaperActivity : AppCompatActivity() {

    companion object {
        private const val DEVICE_STATS_EXTRA_KEY = "device_stats_key"
        fun createIntent(
            context: Context,
            deviceStats: DeviceStats
        ): Intent {
            return Intent(context, DeviceWallpaperActivity::class.java).apply {
                putExtra(
                    DEVICE_STATS_EXTRA_KEY,
                    deviceStats
                )
            }
        }
    }

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var wallpaperReceiver: WallpaperObserver
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_wallpaper)

        fragmentContainer = findViewById<FrameLayout>(R.id.main_container)
        var progressLayout = findViewById<LinearLayout>(R.id.progress_layout)
        val deviceStats = intent.getParcelableExtra<DeviceStats>(DEVICE_STATS_EXTRA_KEY)
        if (deviceStats != null) {
            supportFragmentManager.commit {
                add(R.id.main_container, DimensionsFragment.newInstance(deviceStats))
            }
        }

        val filter = IntentFilter(Intent.ACTION_WALLPAPER_CHANGED)
        wallpaperReceiver = WallpaperObserver()
        registerReceiver(wallpaperReceiver, filter)

        setScreenAsWallpaper()
        progressLayout.visibility = GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wallpaperReceiver)
    }

    private fun setScreenAsWallpaper() {
        var hasSetWallpaper = false
        scope.async(Dispatchers.IO) {
            val success = async { setDeviceWallpaper() }
            hasSetWallpaper = success.await()
        }
        if (hasSetWallpaper) {
            Toast.makeText(this, "Wallpaper captured!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error setting Wallpaper", Toast.LENGTH_LONG).show()
        }

    }

    private fun setDeviceWallpaper(): Boolean {
        val bitmap: Bitmap? = takeScreenShot(fragmentContainer)
        val wallpaperManager =
            WallpaperManager.getInstance(this)
        try {
            wallpaperManager.setBitmap(bitmap)
        } catch (e: IOException) {
            return false
        }
        return true
    }

    private fun takeScreenShot(view: View): Bitmap? {
        //var view = view
        /*val view = getWindow()?.getDecorView()?.getRootView() ?: view
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        view.buildDrawingCache()
        if (view.drawingCache == null) return null
        val snapshot = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return snapshot*/

        /*val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap*/

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
}
