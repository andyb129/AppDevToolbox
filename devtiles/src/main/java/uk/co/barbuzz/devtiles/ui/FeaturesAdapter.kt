package uk.co.barbuzz.devtiles.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feature_item_layout.view.*
import uk.co.barbuzz.devtiles.R
import uk.co.barbuzz.devtiles.model.Feature
import xyz.mustafaali.devqstiles.util.inflate

class FeaturesAdapter(val features: List<Feature>, val listener: (Feature) -> Unit) : RecyclerView.Adapter<FeaturesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(feature: Feature, listener: (Feature) -> Unit) = with(itemView) {
            featureImageView.setImageResource(feature.drawableId)
            featureTitle.text = feature.title
            featureDescription.text = feature.description
            setOnClickListener { listener(feature) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(
        R.layout.feature_item_layout))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(features[position], listener)

    override fun getItemCount(): Int = features.size
}