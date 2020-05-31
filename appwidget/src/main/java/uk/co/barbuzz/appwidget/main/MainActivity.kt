package uk.co.barbuzz.appwidget.main

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.main_content.mainEmptyInfo
import kotlinx.android.synthetic.main.main_content.mainWidgetList
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.data.Analytics
import uk.co.barbuzz.appwidget.settings.SettingsActivity
import javax.inject.Inject

internal class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelProvider: ViewModelProvider
    @Inject lateinit var scopeProvider: ScopeProvider
    @Inject lateinit var navigation: MainNavigation
    @Inject lateinit var widgetListAdapter: WidgetListAdapter
    @Inject lateinit var appWidgetManager: AppWidgetManager
    @Inject lateinit var scheduling: SchedulingStrategy
    @Inject lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupList()

        if (isPinningSupported()) {
            val mainAddNewWidget = findViewById<View>(R.id.mainAddNewWidget)
            mainAddNewWidget.visibility = View.VISIBLE
            mainAddNewWidget.setOnClickListener {
                navigation.navigateForPinning()
            }
        }

        viewModelProvider.get<MainModel>()
            .data
            .compose(scheduling.forFlowable())
            .autoDisposable(scopeProvider)
            .subscribe { (data, diff) ->
                updateEmptyView(data.isEmpty())
                widgetListAdapter.data = data
                diff.dispatchUpdatesTo(widgetListAdapter)
            }

        if (savedInstanceState == null) analytics.sendScreenView(this, "Main")
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            mainEmptyInfo.visibility = View.VISIBLE
            mainEmptyInfo.setText(
                if (isPinningSupported())
                    R.string.main_empty_info_with_pinning
                else
                    R.string.main_empty_info
            )
        } else {
            mainEmptyInfo.visibility = View.GONE
        }
    }

    private fun isPinningSupported() =
        SDK_INT >= O && appWidgetManager.isRequestPinAppWidgetSupported

    private fun setupList() {
        mainWidgetList.adapter = widgetListAdapter
        mainWidgetList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> false
    }
}
