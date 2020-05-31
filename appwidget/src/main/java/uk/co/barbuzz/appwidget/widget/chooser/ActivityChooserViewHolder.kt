package uk.co.barbuzz.appwidget.widget.chooser

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.inflate
import javax.inject.Inject

class ActivityChooserViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    private val text1 = containerView.findViewById<TextView>(R.id.text1)
    private val text2 = containerView.findViewById<TextView>(R.id.text2)
    private val icon = containerView.findViewById<ImageView>(R.id.icon)

    fun bind(info: DisplayResolveInfo, itemClickListener: (DisplayResolveInfo) -> Unit) {
        text1.text = info.label
        text2.text = info.component.shortClassName
        icon.setImageDrawable(info.icon)
        itemView.setOnClickListener {
            itemClickListener(info)
        }
    }

    class Factory @Inject constructor() {

        private val creator = { view: View ->
            ActivityChooserViewHolder(view)
        }

        fun createWith(parent: ViewGroup) = creator(parent.inflate(R.layout.resolve_list_item))
    }
}
