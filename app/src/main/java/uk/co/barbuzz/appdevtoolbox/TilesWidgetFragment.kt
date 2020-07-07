package uk.co.barbuzz.appdevtoolbox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class TilesWidgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tiles_widget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = activity?.getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText )
            inputMethodManager.hideSoftInputFromWindow( activity?.currentFocus?.windowToken,0)

        val viewPager = view.findViewById<ViewPager>(R.id.viewpager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val adapter = TilesWidgetAdapter(fragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }
}
