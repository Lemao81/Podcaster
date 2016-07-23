package com.jueggs.podcaster.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Episode;
import com.jueggs.podcaster.utils.DaUtils;
import com.jueggs.utils.TextUtil;
import com.jueggs.utils.UIUtils;

import java.io.IOException;

import static com.jueggs.podcaster.utils.DaUtils.*;
import static com.jueggs.utils.NetUtils.*;
import static com.jueggs.utils.UIUtils.*;

public class DownloadService extends IntentService
{
    public static final String EXTRA_EPISODE = "com.jueggs.podcaster.EXTRA_EPISODE";

    private Episode episode;

    public DownloadService()
    {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        episode = (Episode) intent.getSerializableExtra(EXTRA_EPISODE);
        if (episode != null)
        {
            runOnUiThread(DownloadService.this, DownloadService.this::onDownloadStarted);
            downloadFileToExternalFilesDir(this, episode.getMediaLink(), episode.getShowId(), callback);
        }
    }

    private void onDownloadStarted()
    {
        longToast(this, R.string.download_started);
    }

    private DownloadCallback callback = new DownloadCallback()
    {
        @Override
        public void onProgress(int progress)
        {

        }

        @Override
        public void onDownloadFinished()
        {
            runOnUiThread(DownloadService.this, DownloadService.this::onDownloadFinished);
            insertDownloadEpisode(DownloadService.this, episode);
        }

        @Override
        public void onDownloadFailed(IOException e)
        {
            runOnUiThread(DownloadService.this, DownloadService.this::onDownloadFailed);
        }
    };

    private void onDownloadFinished()
    {
        shortToast(this, TextUtil.format(this, R.string.download_finished_format, episode.getTitle()));
    }

    private void onDownloadFailed()
    {
        shortToast(this, TextUtil.format(this, R.string.download_failed_format, episode.getTitle()));
    }

}
