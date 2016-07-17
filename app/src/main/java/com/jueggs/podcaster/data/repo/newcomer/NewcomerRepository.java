package com.jueggs.podcaster.data.repo.newcomer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.data.repo.Callback;
import com.jueggs.podcaster.data.repo.DataResultReceiver;
import com.jueggs.podcaster.data.repo.chart.ChartFetchService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_LANGUAGE;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_RECEIVER;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_TYPE;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.Utils.hasElements;

public class NewcomerRepository implements DataResultReceiver.Receiver
{
    private static NewcomerRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>(CHANNEL_TYPES.size());
    private Callback.ChannelsLoaded callback;
    private Context context;
    private int type;
    private DataResultReceiver resultReceiver = new DataResultReceiver(new Handler(), this);

    public NewcomerRepository(Context context)
    {
        this.context = context;
    }

    public void loadNewcomer(String language, int type, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache.get(type)) && callback != null)
        {
            writeNetworkState(context, Result.SUCCESS);
            callback.onChannelsLoaded(cache.get(type));
        }
        else
        {
            this.callback = callback;
            this.type = type;
            context.startService(new Intent(context, NewcomerFetchService.class)
                    .putExtra(EXTRA_LANGUAGE, language)
                    .putExtra(EXTRA_TYPE,CHANNEL_TYPES.get(type))
                    .putExtra(EXTRA_RECEIVER, resultReceiver));
        }
    }

    @Override
    public void onReceiveResult(Serializable result)
    {
        List<Channel> channels = (List<Channel>) result;
        cache.put(type, channels);
        if (callback != null)
            callback.onChannelsLoaded(channels);
    }

    public static NewcomerRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new NewcomerRepository(context);
        return instance;
    }
}
