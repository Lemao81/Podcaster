package com.jueggs.podcaster;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.jueggs.podcaster.data.PodcastContract;

public class App extends Application
{
    private static App instance;

    public static String LANGUAGE = PodcastContract.LANG_DE;

    public static final String TRACK_CAT_CHANNEL = "Channel";
    public static final String TRACK_ACTION_PLAYED = "Started episode of channel";

    private boolean twoPane;
    private Tracker tracker;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());

        instance = this;
    }

    public void startTracking()
    {
        if (tracker == null)
        {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            tracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker()
    {
        startTracking();
        return tracker;
    }

    public boolean isTwoPane()
    {
        return twoPane;
    }

    public void setTwoPane(boolean twoPane)
    {
        this.twoPane = twoPane;
    }

    public static App getInstance()
    {
        return instance;
    }
}
