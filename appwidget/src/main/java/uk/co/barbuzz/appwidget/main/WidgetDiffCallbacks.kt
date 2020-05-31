package uk.co.barbuzz.appwidget.main

import androidx.recyclerview.widget.DiffUtil
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData

internal class WidgetDiffCallbacks(
    private val oldItems: List<WidgetListData>,
    private val newItems: List<WidgetListData>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size
    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val (oldWidget, _) = oldItems[oldPosition]
        val (newWidget, _) = newItems[newPosition]
        return oldWidget.appWidgetId == newWidget.appWidgetId
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
        oldItems[oldPosition] == newItems[newPosition]
}
