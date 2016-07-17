package com.jueggs.podcaster.data.repo.channel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.data.repo.Callback;
import com.jueggs.podcaster.data.repo.DataResultReceiver;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.model.Channel;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.Utils.*;

public class ChannelRepository implements Receiver
{
    private static ChannelRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>();
    private Callback.ChannelsLoaded callback;
    private Context context;
    private int id;
    private DataResultReceiver resultReceiver = new DataResultReceiver(new Handler(), this);

    public ChannelRepository(Context context)
    {
        this.context = context;
    }

    public void loadChannels(int id, String language, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache.get(id)) && callback != null)
        {
            writeNetworkState(context, Result.SUCCESS);
            callback.onChannelsLoaded(cache.get(id));
        }
        else
        {
            this.callback = callback;
            this.id = id;
            context.startService(new Intent(context, ChannelFetchService.class)
                    .putExtra(EXTRA_ID, id)
                    .putExtra(EXTRA_LANGUAGE, language)
                    .putExtra(EXTRA_RECEIVER, resultReceiver));
        }
    }

    @Override
    public void onReceiveResult(Serializable result)
    {
        List<Channel> channels = (List<Channel>) result;
        cache.put(id, channels);
        if (callback != null)
            callback.onChannelsLoaded(channels);
    }

    public static ChannelRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new ChannelRepository(context);
        return instance;
    }
}
