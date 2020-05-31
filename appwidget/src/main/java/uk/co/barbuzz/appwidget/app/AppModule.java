package uk.co.barbuzz.appwidget.app;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.PowerManager;
import android.os.UserManager;
import android.preference.PreferenceManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.co.barbuzz.appwidget.base.extensions.SchedulingStrategy;
import uk.co.barbuzz.appwidget.settings.Version;

@Module
abstract class AppModule {

    @Binds
    abstract Application application(DevWidgetApp devWidgetApp);

    @Binds
    abstract Version version(AppVersion version);

    @Provides
    static PackageManager packageManager(Application app) {
        return app.getPackageManager();
    }

    @Provides
    static PowerManager powerManager(Application app) {
        return (PowerManager) app.getSystemService(Context.POWER_SERVICE);
    }

    @Provides
    static UserManager userManager(Application app) {
        return (UserManager) app.getSystemService(Context.USER_SERVICE);
    }

    @Provides
    static LauncherApps launcherApps(Application app) {
        return (LauncherApps) app.getSystemService(Context.LAUNCHER_APPS_SERVICE);
    }

    @Provides
    static AppWidgetManager appWidgetManager(Application app) {
        return (AppWidgetManager) app.getSystemService(Context.APPWIDGET_SERVICE);
    }

    @Provides
    static SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    static Resources resources(Application app) {
        return app.getResources();
    }

    @Provides
    static SchedulingStrategy schedulingStrategy() {
        return new SchedulingStrategy(
                Schedulers.io(),
                AndroidSchedulers.mainThread()
        );
    }
}
