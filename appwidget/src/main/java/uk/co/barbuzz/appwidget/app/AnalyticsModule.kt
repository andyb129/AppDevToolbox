package uk.co.barbuzz.appwidget.app

import dagger.Binds
import dagger.Module
import uk.co.barbuzz.appwidget.data.Analytics
import javax.inject.Singleton

@Module
interface AnalyticsModule {

    @Binds
    @Singleton
    fun provideAnalytics(analytics: Analytics.DebugAnalytics): Analytics
}
