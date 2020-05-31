package uk.co.barbuzz.appwidget.configure

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposables
import uk.co.barbuzz.appwidget.R
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy
import uk.co.barbuzz.appwidget.base.extensions.inflate
import uk.co.barbuzz.appwidget.data.AppDao
import uk.co.barbuzz.appwidget.data.FilterDao
import javax.inject.Inject

internal class PackageMatcherViewHolder(
    private val textView: TextView,
    private val appDao: AppDao,
    private val filterDao: FilterDao,
    private val scheduling: SchedulingStrategy
) : RecyclerView.ViewHolder(textView) {

    private var disposable = Disposables.empty()

    fun bind(packageMatcher: String) {
        textView.text = packageMatcher
        textView.setOnCreateContextMenuListener { menu, _, _ ->
            menu.setup(packageMatcher)
        }
        textView.setupContextMenuClickListener()
    }

    private fun Menu.setup(packageMatcher: String) {
        MenuInflater(textView.context).inflate(R.menu.configure_package_matcher, this)
        findItem(R.id.package_matcher_delete).setOnMenuItemClickListener {
            delete(packageMatcher)
            true
        }
    }

    private fun delete(packageMatcher: String) {
        disposable.dispose()
        disposable = filterDao.deletePackageMatcher(packageMatcher)
            .andThen(appDao.deleteAppsByPackageMatcher(packageMatcher))
            .compose(scheduling.forCompletable())
            .subscribe()
    }

    fun unbind() {
        disposable.dispose()
    }

    private class ContextMenuClickListener : View.OnClickListener, View.OnTouchListener {

        var lastX = 0f
        var lastY = 0f

        override fun onClick(view: View) {
            if (SDK_INT >= N) {
                view.showContextMenu(lastX, lastY)
            } else {
                view.showContextMenu()
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                lastX = event.x
                lastY = event.y
            }
            return false
        }
    }

    private fun View.setupContextMenuClickListener() {
        ContextMenuClickListener().let {
            setOnClickListener(it)
            setOnTouchListener(it)
        }
    }

    class Factory @Inject constructor(
        appDao: AppDao,
        filterDao: FilterDao,
        scheduling: SchedulingStrategy
    ) {

        private val creator = { view: TextView ->
            PackageMatcherViewHolder(view, appDao, filterDao, scheduling)
        }

        fun createWith(parent: ViewGroup) = creator(
            parent.inflate(R.layout.configure_package_matcher) as TextView
        )
    }
}
