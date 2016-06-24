package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.utils.Utils.hasElements;

public class NewcomerRepository
{
    private static NewcomerRepository instance;

    private List<Channel> cache;
    private Callback.ChannelsLoaded callback;

    public void loadNewcomer(String language, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache) && callback != null)
            callback.onChannelsLoaded(cache);
        else
        {
            this.callback = callback;
            new FetchService(this::onChannelsLoaded).execute(language);
        }
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        cache = channels;
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
            PodcastService service = PodcastContract.createPodcastService();

            try
            {
                ChannelArrayRoot root = service.loadNewcomer((String) params[0]).execute().body();
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
