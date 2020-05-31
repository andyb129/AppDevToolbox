package uk.co.barbuzz.appwidget.main

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.inflate
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData
import uk.co.barbuzz.appwidget.widgetpreview.WidgetView

class WidgetViewHolder(
    private val widgetView: WidgetView,
    containerView: View
) : RecyclerView.ViewHolder(containerView) {

    fun bind(data: WidgetListData, clickAction: (appWidgetId: Int) -> Unit) {
        widgetView.bind(data)
        itemView.setOnClickListener {
            clickAction(data.widget.appWidgetId)
        }
    }

    companion object {
        fun create(parent: ViewGroup, widgetViewFactory: WidgetView.Factory): WidgetViewHolder {
            val view = parent.inflate(R.layout.main_widget_wrapper)
            return WidgetViewHolder(widgetViewFactory.createWith(view), view)
        }
    }
}
