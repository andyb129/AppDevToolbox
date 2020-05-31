package uk.co.barbuzz.appwidget.configure

import android.view.View
import uk.co.barbuzz.appwidget.base.settings.OpacityPreferences
import uk.co.barbuzz.appwidget.widget.WidgetResources
import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData
import uk.co.barbuzz.appwidget.widgetpreview.WidgetView
import javax.inject.Inject

internal class WidgetPreview @Inject constructor(
    private val widgetViewFactory: WidgetView.Factory,
    private val widgetResources: WidgetResources,
    private val opacityPreferences: OpacityPreferences
) {

    fun updateWidgetPreview(widgetPreview: View, widgetListData: WidgetListData) {
        val shadeColor = widgetResources.resolveBackgroundColor(opacityPreferences.opacity)
        widgetPreview.setBackgroundColor(shadeColor)
        widgetViewFactory.createWith(widgetPreview).bind(widgetListData)
    }
}
