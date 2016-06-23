package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.utils.Utils.*;

public class ChartRepository
{
    private static ChartRepository instance;

    private List<Channel> cache = new ArrayList<>();
    private Callback.ChannelsLoaded callback;

    public void loadCharts(String language, Callback.ChannelsLoaded callback)
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

    public static ChartRepository getInstance()
    {
        if (instance == null)
            instance = new ChartRepository();
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
                ChannelArrayRoot root = service.loadCharts((String) params[0]).execute().body();
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
