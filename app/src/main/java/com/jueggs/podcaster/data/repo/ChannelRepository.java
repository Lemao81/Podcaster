package com.jueggs.podcaster.data.repo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.model.Channel;

import java.io.IOException;
import java.util.List;

import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.Utils.*;

public class ChannelRepository
{
    private static ChannelRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>();
    private Callback.ChannelsLoaded callback;
    private Context context;
    private int id;

    public ChannelRepository(Context context)
    {
        this.context = context;
    }

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

    public static ChannelRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new ChannelRepository(context);
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
                int id = (int) params[0];
                String language = (String) params[1];
                ChannelArrayRoot root = service.loadCategory(id, language).execute().body();
                writeNetworkState(context, Result.SUCCESS);
                return root.getChannels();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
                int result = new NetworkValidator(context).validate(e);
                writeNetworkState(context, result);
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
