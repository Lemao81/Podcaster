package com.jueggs.podcaster.data.repo.episode;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.model.ChannelRoot;
import com.jueggs.podcaster.model.Episode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;

public class EpisodeFetchService extends IntentService
{
    public static final String TAG = EpisodeFetchService.class.getSimpleName();

    public EpisodeFetchService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        List<Episode> episodes = null;
        int id = intent.getIntExtra(EXTRA_ID, 0);
        String language = intent.getStringExtra(EXTRA_LANGUAGE);
        ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        PodcastService service = PodcastContract.createPodcastService();

        try
        {
            ChannelRoot root = service.loadChannel(id, language).execute().body();

            if (root.getHead() != null)
            {
                writeNetworkState(this, Result.SUCCESS);
                episodes = root.getChannel().getEpisodes();
            }
            else
                writeNetworkState(this, Result.INVALID_DATA);
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
            int result = new NetworkValidator(this).validate(e);
            writeNetworkState(this, result);
        }

        Bundle data = new Bundle();
        data.putSerializable(EXTRA_DATA, (ArrayList) episodes);
        resultReceiver.send(RESULT_CODE, data);
    }
}