package com.jueggs.podcaster.data.repo.episode;

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
import com.jueggs.podcaster.data.repo.channel.ChannelFetchService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.ChannelRoot;
import com.jueggs.podcaster.model.Episode;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_ID;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_LANGUAGE;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.EXTRA_RECEIVER;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.Utils.*;

public class EpisodeRepository implements DataResultReceiver.Receiver
{
    private static EpisodeRepository instance;

    private SparseArray<List<Episode>> cache = new SparseArray<>();
    private Context context;
    private Callback.EpisodesLoaded callback;
    private int id;
    private DataResultReceiver resultReceiver = new DataResultReceiver(new Handler(), this);

    public EpisodeRepository(Context context)
    {
        this.context = context;
    }

    public void loadEpisodes(int id, String language, Callback.EpisodesLoaded callback)
    {
        if (hasElements(cache.get(id)) && callback != null)
        {
            writeNetworkState(context, Result.SUCCESS);
            callback.onEpisodesLoaded(cache.get(id));
        }
        else
        {
            this.callback = callback;
            this.id = id;
            context.startService(new Intent(context, EpisodeFetchService.class)
                    .putExtra(EXTRA_ID, id)
                    .putExtra(EXTRA_LANGUAGE, language)
                    .putExtra(EXTRA_RECEIVER, resultReceiver));
        }
    }

    public static EpisodeRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new EpisodeRepository(context);
        return instance;
    }

    @Override
    public void onReceiveResult(Serializable result)
    {
        List<Episode> episodes = (List<Episode>) result;
        cache.put(id, episodes);
        if (callback != null)
            callback.onEpisodesLoaded(episodes);
    }
}
