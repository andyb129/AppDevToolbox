package uk.co.barbuzz.appdevtoolbox.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import uk.co.barbuzz.appdevtoolbox.R
import uk.co.barbuzz.snippet.service.SnippetTextAccessibilityService

class SnippetIntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val introAdapter = IntroAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.isUserInputEnabled = false
        viewPager.adapter = introAdapter

        if (Settings.canDrawOverlays(this)) {
            viewPager.currentItem = 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {

        if (requestCode == OVERLAY_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                viewPager.setCurrentItem(1, true)
            }
        } else if (requestCode == ACCESSIBILITY_REQUEST_CODE) {
            if (isAccessibilitySettingsOn(this)) {
                viewPager.setCurrentItem(2, true)
            }
        }

        super.onActivityResult(requestCode, resultCode, intentData)
    }

    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service =
            packageName + "/" + SnippetTextAccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            //Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } else {
            //Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }
}

const val OVERLAY_REQUEST_CODE = 1
const val ACCESSIBILITY_REQUEST_CODE = 2
