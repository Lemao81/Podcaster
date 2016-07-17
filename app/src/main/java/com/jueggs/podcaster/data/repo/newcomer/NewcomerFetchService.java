package com.jueggs.podcaster.data.repo.newcomer;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;

public class NewcomerFetchService extends IntentService
{
    public static final String TAG = NewcomerFetchService.class.getSimpleName();

    public NewcomerFetchService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        List<Channel> channels = null;
        String language = intent.getStringExtra(EXTRA_LANGUAGE);
        String type = intent.getStringExtra(EXTRA_TYPE);
        ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        PodcastService service = PodcastContract.createPodcastService();

        try
        {
            ChannelArrayRoot root = service.loadNewcomerWithType(language, type).execute().body();

            if (root.getHead() != null)
            {
                writeNetworkState(this, Result.SUCCESS);
                channels = root.getChannels();
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
        data.putSerializable(EXTRA_DATA, (ArrayList) channels);
        resultReceiver.send(RESULT_CODE, data);
    }
}