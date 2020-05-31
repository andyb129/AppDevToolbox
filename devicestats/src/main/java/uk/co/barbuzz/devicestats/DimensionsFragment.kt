package uk.co.barbuzz.devicestats

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DimensionsFragment : Fragment() {

    private lateinit var mLabelArrowWidthTextView: TextView
    private lateinit var mLabelArrowHeightTextView: TextView
    private lateinit var mModelTextView: TextView
    private lateinit var mScreenDimensionDipsTextView: TextView
    private lateinit var mScreenDimensionPixelsTextView: TextView
    private lateinit var mDeviceStats: DeviceStats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //exitTransition = MaterialFadeThrough.create(requireContext())

        if (arguments != null) {
            mDeviceStats = arguments!!.getParcelable<Parcelable>(ARG_PARAM1) as DeviceStats
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dimensions, container, false)
        getViews(view)
        setData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //need to delay this as dimensions aren't available initially
        view.post { showViewDimensions() }
    }

    private fun getViews(view: View) {
        mScreenDimensionPixelsTextView = view.findViewById(R.id.screenDimensionsPixels) as TextView
        mScreenDimensionDipsTextView = view.findViewById(R.id.screenDimensionsDips) as TextView
        mModelTextView = view.findViewById(R.id.model) as TextView
        mLabelArrowWidthTextView = view.findViewById(R.id.horizontal_label) as TextView
        mLabelArrowHeightTextView = view.findViewById(R.id.vertical_label) as TextView
    }

    private fun setData() {
        mScreenDimensionPixelsTextView.text = getString(R.string.screen_dimensions_pixels,
                mDeviceStats.height, mDeviceStats.width)
        mScreenDimensionDipsTextView.text = getString(R.string.screen_dimensions_dips,
                mDeviceStats.heightDpi, mDeviceStats.widthDpi)
        mModelTextView.text = mDeviceStats.model
    }

    private fun showViewDimensions() {
        mLabelArrowWidthTextView.text = getString(R.string.view_width, mDeviceStats.widthDpi,
                mDeviceStats.width)
        mLabelArrowHeightTextView.text = getString(R.string.view_height, mDeviceStats.heightDpi,
                mDeviceStats.height)
    }

    companion object {
        private const val TAG = "DimensionsFragment"
        private const val ARG_PARAM1 = "param1"
        fun newInstance(deviceStats: DeviceStats?): DimensionsFragment {
            val fragment = DimensionsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, deviceStats)
            fragment.arguments = args
            return fragment
        }
    }
}