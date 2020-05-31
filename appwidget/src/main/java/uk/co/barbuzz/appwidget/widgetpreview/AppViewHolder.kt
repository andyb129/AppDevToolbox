package uk.co.barbuzz.appwidget.widgetpreview

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.inflate
import uk.co.barbuzz.appwidget.data.Action
import uk.co.barbuzz.appwidget.widget.DisplayApplicationInfo
import uk.co.barbuzz.appwidget.widget.WidgetResources
import javax.inject.Inject

internal class AppViewHolder(
    private val widgetResources: WidgetResources,
    private val favAction: Action,
    containerView: View
) : RecyclerView.ViewHolder(containerView) {

    private val appWidgetContainer = containerView.findViewById<View>(R.id.appWidgetContainer)
    private val appWidgetDetails = containerView.findViewById<ImageView>(R.id.appWidgetDetails)
    private val appWidgetFavAction = containerView.findViewById<ImageView>(R.id.appWidgetFavAction)
    private val appWidgetIcon = containerView.findViewById<ImageView>(R.id.appWidgetIcon)
    private val appWidgetLabel = containerView.findViewById<TextView>(R.id.appWidgetLabel)
    private val appWidgetVersion = containerView.findViewById<TextView>(R.id.appWidgetVersion)
    private val appWidgetPackageName = containerView.findViewById<TextView>(R.id.appWidgetPackageName)

    fun bind(app: DisplayApplicationInfo) {
        appWidgetContainer.background = null

        appWidgetIcon.setImageDrawable(app.icon)
        val iconSize = widgetResources.resolveAppIconSize(Int.MAX_VALUE)
        appWidgetIcon.maxHeight = iconSize
        appWidgetIcon.maxWidth = iconSize

        appWidgetLabel.text = app.label
        appWidgetLabel.setTextColor(widgetResources.foregroundColor)
        appWidgetPackageName.text = app.packageName
        appWidgetPackageName.setTextColor(widgetResources.foregroundColor)
        appWidgetVersion.text = app.version
        appWidgetVersion.setTextColor(widgetResources.foregroundColor)

        appWidgetFavAction.setImageResource(widgetResources.resolveFavIcon(favAction))
        appWidgetDetails.setImageResource(widgetResources.moreActionsIcon)
    }

    class Factory @Inject constructor(widgetResources: WidgetResources) {

        private val creator = { view: View, favAction: Action ->
            AppViewHolder(widgetResources, favAction, view)
        }

        fun createWith(parent: ViewGroup, favAction: Action) = creator(
            parent.inflate(R.layout.app_widget_list_item),
            favAction
        )
    }
}
