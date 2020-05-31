package uk.co.barbuzz.appwidget.main

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.barbuzz.appwidget.base.navigation.Navigator
import uk.co.barbuzz.appwidget.base.navigation.widgetConfigureCommand
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData
import uk.co.barbuzz.appwidget.widgetpreview.WidgetView
import javax.inject.Inject

class WidgetListAdapter @Inject constructor(
    private val navigator: Navigator,
    private val widgetViewFactory: WidgetView.Factory
) : RecyclerView.Adapter<WidgetViewHolder>() {

    var data = emptyList<WidgetListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WidgetViewHolder.create(parent, widgetViewFactory)

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(data[position], clickAction = { appWidgetId ->
            navigator.navigate(widgetConfigureCommand(appWidgetId))
        })
    }

    override fun getItemCount() = data.size
}
