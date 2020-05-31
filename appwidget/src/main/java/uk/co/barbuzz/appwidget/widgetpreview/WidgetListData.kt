package uk.co.barbuzz.appwidget.widgetpreview

import uk.co.barbuzz.appwidget.data.Action
import uk.co.barbuzz.appwidget.data.Widget
import uk.co.barbuzz.appwidget.widget.DisplayApplicationInfo

data class WidgetListData(
    val widget: Widget,
    val apps: List<DisplayApplicationInfo>,
    val favAction: Action = Action.UNINSTALL
)
