package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.CategoryRoot;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.createCategoriesUri;
import static com.jueggs.podcaster.utils.ParseUtils.parseCategories;
import static com.jueggs.utils.NetUtils.getJsonDataStetho;
import static com.jueggs.utils.Utils.*;

public class ChannelRepository
{
    private static ChannelRepository instance;

    private List<Channel> cache = new ArrayList<>();
    private Callback.ChannelsLoaded callback;

    private ChannelRepository()
    {
    }

    public void loadChannels(int id, String language, Callback.ChannelsLoaded callback)
    {
        if (hasElements(cache) && callback != null)
            callback.onChannelsLoaded(cache);
        else
        {
            this.callback = callback;
            new FetchService(this::onChannelsLoaded).execute(id, language);
        }
    }

    private void onChannelsLoaded(List<Channel> channels)
    {
        this.cache = channels;
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
                CategoryRoot root = service.loadCategory((int) params[0], (String) params[1]).execute().body();
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
