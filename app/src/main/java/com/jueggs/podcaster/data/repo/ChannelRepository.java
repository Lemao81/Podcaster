package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.model.Channel;

import java.io.IOException;
import java.util.List;

import static com.jueggs.utils.Utils.*;

public class ChannelRepository
{
    private static ChannelRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>();
    private Callback.ChannelsLoaded callback;
    private int id;

    public void loadChannels(int id, String language, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache.get(id)) && callback != null)
            callback.onChannelsLoaded(cache.get(id));
        else
        {
            this.callback = callback;
            this.id = id;
            new FetchService(this::onChannelsLoaded).execute(id, language);
        }
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        cache.put(id, channels);
        if (callback != null)
            callback.onChannelsLoaded(channels);
    }

    public static ChannelRepository getInstance()
    {
        if (instance == null)
            instance = new ChannelRepository();
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
            PodcastService service = PodcastContract.createPodcastService();

            try
            {
                ChannelArrayRoot root = service.loadCategory((int) params[0], (String) params[1]).execute().body();
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
