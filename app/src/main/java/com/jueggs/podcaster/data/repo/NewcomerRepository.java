package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.utils.Utils.hasElements;

public class NewcomerRepository
{
    private static NewcomerRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>(CHANNEL_TYPES.size());
    private Callback.ChannelsLoaded callback;
    private int type;

    public void loadNewcomer(String language, int type, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache.get(type)) && callback != null)
            callback.onChannelsLoaded(cache.get(type));
        else
        {
            this.callback = callback;
            this.type = type;
            new FetchService(this::onChannelsLoaded).execute(language, CHANNEL_TYPES.get(type));
        }
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        cache.put(type, channels);
        if (callback != null)
            callback.onChannelsLoaded(channels);
    }

    public static NewcomerRepository getInstance()
    {
        if (instance == null)
            instance = new NewcomerRepository();
        return instance;
    }

    private class FetchService extends AsyncTask<Object, Void, List<Channel>>
    {
        public final String TAG = FetchService.class.getSimpleName();

        private Callback.ChannelsLoaded callback;

        public FetchService(Callback.ChannelsLoaded callback)
        {
            this.callback = callback;
        }

        @Override
        protected List<Channel> doInBackground(Object... params)
        {
            PodcastService service = createPodcastService();

            try
            {
                ChannelArrayRoot root = service.loadNewcomer((String) params[0], (String) params[1]).execute().body();
                return root.getChannels();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Channel> channels)
        {
            callback.onChannelsLoaded(channels);
        }
    }
}
