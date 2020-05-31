package uk.co.barbuzz.appwidget.widget.click

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import uk.co.barbuzz.appwidget.base.LifecycleScopeModule

@Module(includes = [LifecycleScopeModule::class])
internal interface WidgetRefreshModule {

    @Binds
    fun fragmentActivity(activity: WidgetRefreshActivity): FragmentActivity
}
