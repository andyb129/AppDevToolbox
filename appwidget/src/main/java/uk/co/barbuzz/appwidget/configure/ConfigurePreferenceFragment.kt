package uk.co.barbuzz.appwidget.configure

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable
import dagger.android.support.AndroidSupportInjection
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.widget.WidgetUpdater
import uk.co.barbuzz.appwidget.data.Action
import uk.co.barbuzz.appwidget.data.FavAction
import uk.co.barbuzz.appwidget.data.FavActionDao
import uk.co.barbuzz.appwidget.data.findFavActionByWidgetId
import javax.inject.Inject
import kotlin.text.Typography.dagger

class ConfigurePreferenceFragment : PreferenceFragmentCompat(), WidgetNameView {

    @JvmField @Inject var appWidgetId: Int = -1
    @Inject lateinit var favActionDao: FavActionDao
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var scopeProvider: ScopeProvider
    @Inject lateinit var viewModelProvider: ViewModelProvider
    @Inject lateinit var widgetUpdater: WidgetUpdater

    private val widgetNameModel get() = viewModelProvider.get<WidgetNameModel>()

    override var widgetNameChanged: (String) -> Unit = {}

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.configure_preferences)
        setupFavAction()
        setupWidgetName()
    }

    private fun setupFavAction() {
        val favAction = findPreference("pref_key_fav_action") as ListPreference
        favAction.setOnPreferenceChangeListener { _, newValue ->
            favActionDao.insertFavAction(FavAction(Action.valueOf(newValue.toString()), appWidgetId))
                .compose(scheduling.forCompletable())
                .autoDisposable(scopeProvider)
                .subscribe {
                    widgetUpdater.notifyWidgetDataChanged(appWidgetId)
                }
            true
        }

        favActionDao.findFavActionByWidgetId(appWidgetId)
            .compose(scheduling.forSingle())
            .autoDisposable(scopeProvider)
            .subscribe { action ->
                favAction.value = action.name
            }
    }

    private fun setupWidgetName() {
        val widgetName = findPreference("pref_key_widget_name") as EditTextPreference
        widgetName.setOnPreferenceChangeListener { _, newValue ->
            updateWidgetName(newValue.toString())
            widgetName.summary = newValue.toString()
            true
        }

        widgetNameModel.currentWidgetName()
            .compose(scheduling.forMaybe())
            .autoDisposable(scopeProvider)
            .subscribe {
                widgetName.summary = it
                widgetName.text = it
            }
    }

    private fun updateWidgetName(newValue: String) {
        widgetNameModel.updateWidgetName(newValue)
            .compose(scheduling.forCompletable())
            .autoDisposable(scopeProvider)
            .subscribe()
    }
}
