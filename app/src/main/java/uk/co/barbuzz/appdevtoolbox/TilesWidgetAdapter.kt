package uk.co.barbuzz.appdevtoolbox

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uk.co.barbuzz.appwidget.main.AppWidgetFragment
import uk.co.barbuzz.devtiles.DevTilesFragment

class TilesWidgetAdapter(
    fm: FragmentManager?
) : FragmentStatePagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DevTilesFragment()
            else -> AppWidgetFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Dev Tiles"
            else -> "Other"
        }
    }
}