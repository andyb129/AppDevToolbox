package uk.co.barbuzz.appwidget.configure

import uk.co.barbuzz.appwidget.widgetpreview.WidgetListData

internal interface ConfigureView {
    fun updateWidgetPreview(widgetListData: WidgetListData)
    fun setFilters(filters: List<String>)
    fun setItems(items: Collection<String>)
    fun finishWith(appWidgetId: Int)

    var onConfirmClicked: () -> Unit
    var onSettingsClicked: () -> Unit
    var onPackageMatcherAdded: (packageMatcher: String) -> Unit
}
