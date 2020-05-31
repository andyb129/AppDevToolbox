package uk.co.barbuzz.appwidget.configure

internal interface WidgetNameView {
    var widgetNameChanged: (widgetName: String) -> Unit
}
