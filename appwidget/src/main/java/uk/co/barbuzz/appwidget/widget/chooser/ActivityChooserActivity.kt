package uk.co.barbuzz.appwidget.widget.chooser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_chooser_list.activityChooserList
import kotlinx.android.synthetic.main.activity_chooser_list.activityChooserTitle
import kotlinx.android.synthetic.main.activity_chooser_list.resolverDrawerLayout
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.toast
import uk.co.barbuzz.appwidget.base.settings.ClickBehavior
import uk.co.barbuzz.appwidget.base.settings.ClickBehaviorPreferences
import javax.inject.Inject

internal class ActivityChooserActivity : DaggerAppCompatActivity() {

    @Inject lateinit var adapter: ResolveListAdapter
    @Inject lateinit var launcherApps: LauncherApps
    @Inject lateinit var clickBehaviorPreferences: ClickBehaviorPreferences

    private val extraPackageName
        get() = intent.getStringExtra(EXTRA_PACKAGE_NAME)

    private val extraUser
        get() = intent.getParcelableExtra<UserHandle>(EXTRA_USER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_list)
        resolverDrawerLayout.setOnDismissedListener(::finish)

        val apps = if (shouldResolveAllActivities()) {
            resolveAllActivities()
        } else {
            resolveLauncherActivities(extraUser)
        }

        if (apps.isEmpty()) {
            Toast.makeText(this, R.string.widget_error_activity_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (apps.size == 1) {
            launch(apps.first())
            finish()
            return
        }
        adapter.submitList(apps)
        adapter.itemClickListener = {
            launch(it)
        }
        activityChooserList.adapter = adapter
        activityChooserTitle.setText(R.string.activity_chooser_open_with)
    }

    private fun resolveAllActivities(): List<DisplayResolveInfo> =
        packageManager
            .getPackageInfo(extraPackageName, PackageManager.GET_ACTIVITIES)
            .activities
            .filter { it.exported }
            .map {
                DisplayResolveInfo(
                    it.componentName(),
                    it.loadLabel(packageManager),
                    it.loadIcon(packageManager)
                )
            }

    private fun resolveLauncherActivities(userHandle: UserHandle): List<DisplayResolveInfo> =
        launcherApps.getActivityList(extraPackageName, userHandle)
            .map {
                DisplayResolveInfo(
                    it.componentName,
                    it.label,
                    it.getBadgedIcon(0)
                )
            }

    private fun launch(it: DisplayResolveInfo) {
        if (shouldResolveAllActivities()) {
            Intent().apply {
                component = it.component
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }.start()
        } else {
            launcherApps.startMainActivity(it.component, extraUser, null, null)
        }
    }

    private fun shouldResolveAllActivities(): Boolean {
        return extraUser == Process.myUserHandle() &&
                clickBehaviorPreferences.clickBehavior == ClickBehavior.EXPORTED
    }

    fun Intent.start() =
        try {
            startActivity(this)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.widget_error_activity_cannot_be_launched)
        }

    companion object {

        private const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        private const val EXTRA_USER = "EXTRA_USER"

        fun createIntent(context: Context, packageName: String, user: UserHandle) =
            Intent(context, ActivityChooserActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_USER, user)
            }
    }
}
