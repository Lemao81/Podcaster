package com.jueggs.podcaster.data.repo.chart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.data.repo.Callback;
import com.jueggs.podcaster.data.repo.DataResultReceiver;
import com.jueggs.podcaster.data.repo.channel.ChannelFetchService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.Utils.*;

public class ChartRepository implements Receiver
{
    private static ChartRepository instance;

    private List<Channel> cache;
    private Context context;
    private Callback.ChannelsLoaded callback;
    private DataResultReceiver resultReceiver = new DataResultReceiver(new Handler(), this);

    public ChartRepository(Context context)
    {
        this.context = context;
    }

    public void loadCharts(String language, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache) && callback != null)
        {
            writeNetworkState(context, Result.SUCCESS);
            callback.onChannelsLoaded(cache);
        }
        else
        {
            this.callback = callback;
            context.startService(new Intent(context, ChartFetchService.class)
                    .putExtra(EXTRA_LANGUAGE, language)
                    .putExtra(EXTRA_RECEIVER, resultReceiver));
        }
    }

    @Override
    public void onReceiveResult(Serializable result)
    {
        List<Channel> channels = (List<Channel>) result;
        cache = channels;
        if (callback != null)
            callback.onChannelsLoaded(channels);
    }

    public static ChartRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new ChartRepository(context);
        return instance;
    }
}
