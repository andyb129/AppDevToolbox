package uk.co.barbuzz.devicestats

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class StatsFragment : Fragment() {

    private lateinit var mCardParentLayout: LinearLayout

    enum class DeviceInfoType {
        DEVICE_INFO, HARDWARE, CIPHERS, ALGORITHMS, ALGORITHMS_CRYPTO
    }

    private lateinit var mDeviceStats: DeviceStats
    private lateinit var mCardView: CardView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCardHeaderImageView: ImageView
    private lateinit var mCardHeaderTextView: TextView
    private lateinit var mDeviceInfoType: DeviceInfoType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        if (arguments != null) {
            mDeviceStats = arguments!!.getParcelable<Parcelable>(ARG_DEVICE_STATS) as DeviceStats
            mDeviceInfoType = arguments!!.getSerializable(ARG_DEVICE_INFO_TYPE) as DeviceInfoType
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        mCardParentLayout = view.findViewById(R.id.fragment_stats_card_parent_layout) as LinearLayout
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")

        //needed to add cards here as onViewCreated doesn't get called on rotation of device
        addCards()
    }

    private fun addCards() {
        Log.i(TAG, "addCards")
        mCardParentLayout!!.removeAllViews()
        val valueImageMap: MutableMap<String, Int> = HashMap()
        valueImageMap[DeviceStats.HAS_HARDWARE] = R.drawable.ic_check_circle_green_24dp
        valueImageMap[DeviceStats.NO_HARDWARE] = R.drawable.ic_cancel_red_24dp
        when (mDeviceInfoType) {
            DeviceInfoType.DEVICE_INFO -> addRowsToCard(mDeviceStats.deviceInfoLabelMap, null)
            DeviceInfoType.HARDWARE -> addRowsToCard(mDeviceStats.hardwareLabelMap, valueImageMap)
            DeviceInfoType.CIPHERS -> {
                val isCollapseContent = true
                addRowsToCardSingle("Default Protocols", null, arrayOf(TextUtils.join(", ", mDeviceStats.protocolsDefault)), isCollapseContent)
                addRowsToCardSingle("Default Cipher Suites", null, mDeviceStats.cipherSuitesDefault, isCollapseContent)
                addRowsToCardSingle("Supported Protocols", null, arrayOf(TextUtils.join(", ", mDeviceStats.protocolsSupport)), isCollapseContent)
                addRowsToCardSingle("Supported Cipher Suites", null, mDeviceStats.cipherSuitesSupport, isCollapseContent)
            }
            DeviceInfoType.ALGORITHMS -> addRowsToCardSingle("Algorithms", null, mDeviceStats.algorithmsList, false)
            DeviceInfoType.ALGORITHMS_CRYPTO -> {
                addRowsToCardSingle("Algorithm Filter: AES", "Provider: ", mDeviceStats.cryptoAlgorithmsAesMap, true)
                addRowsToCardSingle("Algorithm Filter: EC", "Provider: ", mDeviceStats.cryptoAlgorithmsAesMap, true)
            }
            else -> {
            }
        }
    }

    private fun addRowsToCard(labelMap: LinkedHashMap<String, String>, valueImageMap: Map<String, Int>?) {
        val inflater = LayoutInflater.from(activity)
        val cardView = inflater.inflate(R.layout.element_card_view, null, false) as FrameLayout
        val cardBodyLayout = cardView.findViewById(R.id.fragment_stats_card_body_layout) as LinearLayout
        for ((key, value) in labelMap) {
            val rowView = inflater.inflate(R.layout.element_card_double_column_row, null, false)
            val textViewLabel = rowView.findViewById(R.id.element_card_row_label) as TextView
            val imageViewValue = rowView.findViewById(R.id.element_card_row_value_image) as ImageView
            val textViewValue = rowView.findViewById(R.id.element_card_row_value) as TextView
            textViewLabel.text = key
            if (valueImageMap != null) {
                imageViewValue.setImageDrawable(resources
                        .getDrawable(valueImageMap[value]!!))
                setImageColumnVisibility(imageViewValue, textViewValue, true)
            } else {
                textViewValue.text = value
                setImageColumnVisibility(imageViewValue, textViewValue, false)
            }
            cardBodyLayout.addView(rowView)
        }
        mCardParentLayout!!.addView(cardView)
    }

    private fun setImageColumnVisibility(imageViewValue: ImageView, textViewValue: TextView,
                                         isVisible: Boolean) {
        imageViewValue.visibility = if (isVisible) View.VISIBLE else View.GONE
        textViewValue.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun addRowsToCardSingle(title: String, subTitle: String, valuesMap: Map<String, List<String>>, isCollapseContent: Boolean) {
        for ((key, value) in valuesMap) {
            addRowsToCardSingle(title, subTitle + key, value, isCollapseContent)
        }
    }

    private fun addRowsToCardSingle(title: String, subTitle: String?, valuesList: List<String>, isCollapseContent: Boolean) {
        addRowsToCardSingle(title, subTitle, valuesList.toTypedArray(), isCollapseContent)
    }

    private fun addRowsToCardSingle(title: String?, subTitle: String?, valuesList: Array<String>, isCollapseContent: Boolean) {
        val inflater = LayoutInflater.from(activity)
        val cardView = inflater.inflate(R.layout.element_card_view, null, false) as FrameLayout
        val cardBodyLayout = cardView.findViewById(R.id.fragment_stats_card_body_layout) as LinearLayout
        //add title if required
        if (title != null) {
            val headerParentLayout = cardView.findViewById(R.id.fragment_stats_header_parent_layout) as LinearLayout
            val textViewTitle = cardView.findViewById(R.id.fragment_stats_header_text) as TextView
            textViewTitle.text = title
            if (subTitle != null) {
                val textViewSubTitle = cardView.findViewById(R.id.fragment_stats_header_subtitle_text) as TextView
                textViewSubTitle.text = subTitle
                textViewSubTitle.visibility = View.VISIBLE
            }
            if (isCollapseContent) {
                cardBodyLayout.visibility = View.GONE
                headerParentLayout.setOnClickListener {
                    if (cardBodyLayout.visibility == View.GONE) {
                        cardBodyLayout.visibility = View.VISIBLE
                    } else {
                        cardBodyLayout.visibility = View.GONE
                    }
                }
            }
            headerParentLayout.visibility = View.VISIBLE
        }
        for (value in valuesList) {
            val rowView = inflater.inflate(R.layout.element_card_single_column_row, null, false)
            val textViewValue = rowView.findViewById(R.id.element_card_row_value) as TextView
            textViewValue.text = value
            cardBodyLayout.addView(rowView)
        }
        mCardParentLayout!!.addView(cardView)
    }

    companion object {
        private const val TAG = "StatsFragment"
        private const val ARG_DEVICE_STATS = TAG + "_device_info_type"
        private const val ARG_DEVICE_INFO_TYPE = TAG + "_display_hardware"
        private const val EXTRA_DEVICE_STATS = TAG + "_extra_device_stats"
        private const val EXTRA_DEVICE_INFO_TYPE = TAG + "_extra_device_info_type"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param deviceStats
         * @return A new instance of fragment StatsFragment.
         */
        fun newInstance(deviceStats: DeviceStats?, deviceInfoType: DeviceInfoType?): StatsFragment {
            val fragment = StatsFragment()
            val args = Bundle()
            args.putParcelable(ARG_DEVICE_STATS, deviceStats)
            args.putSerializable(ARG_DEVICE_INFO_TYPE, deviceInfoType)
            fragment.arguments = args
            fragment.retainInstance = true
            return fragment
        }
    }
}