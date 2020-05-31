package uk.co.barbuzz.devicestats

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class DeviceStatsAdapter(
    private val mContext: Context,
    fm: FragmentManager?,
    private val mDeviceStats: DeviceStats
) : FragmentStatePagerAdapter(fm!!) {

    private val imageResId = intArrayOf(
            R.drawable.ic_developer_mode_white_24dp,
            R.drawable.ic_perm_device_information_white_24dp,
            R.drawable.ic_speaker_phone_white_24dp,
            R.drawable.ic_lock_white_24dp,
            R.drawable.ic_vpn_key_white_24dp,
            R.drawable.ic_phonelink_lock_white_24dp
    )

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DimensionsFragment.newInstance(mDeviceStats)
            1 -> StatsFragment.newInstance(mDeviceStats, StatsFragment.DeviceInfoType.DEVICE_INFO)
            2 -> StatsFragment.newInstance(mDeviceStats, StatsFragment.DeviceInfoType.HARDWARE)
            3 -> StatsFragment.newInstance(mDeviceStats, StatsFragment.DeviceInfoType.CIPHERS)
            4 -> StatsFragment.newInstance(mDeviceStats, StatsFragment.DeviceInfoType.ALGORITHMS)
            else -> StatsFragment.newInstance(mDeviceStats, StatsFragment.DeviceInfoType.ALGORITHMS_CRYPTO)
        }
    }

    override fun getCount(): Int {
        return ITEM_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return getImageInSpannableString(imageResId[position])
    }

    private fun getImageInSpannableString(i: Int): CharSequence {
        val image = ContextCompat.getDrawable(mContext, i)
        image!!.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
        val sb = SpannableString(" ")
        val imageSpan = ImageSpan(image, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    companion object {
        private const val ITEM_COUNT = 6
    }

}