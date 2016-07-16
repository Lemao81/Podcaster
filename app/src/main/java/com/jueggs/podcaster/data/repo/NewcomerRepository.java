package com.jueggs.podcaster.data.repo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.model.ChannelArrayRoot;

import java.io.IOException;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.Utils.hasElements;

public class NewcomerRepository
{
    private static NewcomerRepository instance;

    private SparseArray<List<Channel>> cache = new SparseArray<>(CHANNEL_TYPES.size());
    private Callback.ChannelsLoaded callback;
    private Context context;
    private int type;

    public NewcomerRepository(Context context)
    {
        this.context = context;
    }

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

    public static NewcomerRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new NewcomerRepository(context);
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
            String language = (String) params[0];
            String type = (String) params[1];

            try
            {
                ChannelArrayRoot root = service.loadNewcomerWithType(language, type).execute().body();

                if (root.getHead() != null)
                {
                    writeNetworkState(context, Result.SUCCESS);
                    return root.getChannels();
                }
                else
                {
                    writeNetworkState(context, Result.INVALID_DATA);
                    return null;
                }
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
