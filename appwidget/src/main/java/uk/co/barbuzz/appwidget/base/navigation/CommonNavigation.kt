package uk.co.barbuzz.appwidget.base.navigation

fun widgetConfigureCommand(appWidgetId: Int) =
    UriCommand("devwidget://configure?appWidgetId=$appWidgetId")

fun settingsCommand() = UriCommand("devwidget://settings")
