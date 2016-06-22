package com.jueggs.podcaster;

import android.app.Application;
import com.facebook.stetho.Stetho;

public class App extends Application
{
    private static App instance;

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
