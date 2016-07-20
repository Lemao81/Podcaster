package com.jueggs.podcaster.service;

import android.app.IntentService;
import android.content.Intent;
import com.jueggs.podcaster.model.Episode;

public class DownloadService extends IntentService
{
    public static final String EXTRA_EPISODE = "com.jueggs.podcaster.EXTRA_EPISODE";

    public DownloadService()
    {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Episode episode = (Episode) intent.getSerializableExtra(EXTRA_EPISODE);
        if (episode != null)
        {

        }
    }
}
