package com.jueggs.podcaster;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.jueggs.podcaster.data.PodcastContract;

public class App extends Application
{
    private static App instance;
    public static String LANGUAGE = PodcastContract.LANG_DE;

    private boolean twoPane;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());

        instance = this;
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
