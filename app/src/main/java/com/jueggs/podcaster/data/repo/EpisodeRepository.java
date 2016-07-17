package com.jueggs.podcaster.data.repo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.ChannelRoot;
import com.jueggs.podcaster.model.Episode;

import java.io.IOException;
import java.util.List;

import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.Utils.*;

public class EpisodeRepository
{
    private static EpisodeRepository instance;

    private SparseArray<List<Episode>> cache = new SparseArray<>();
    private Context context;
    private Callback.EpisodesLoaded callback;
    private int id;

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
            new FetchService(this::onEpisodesLoaded).execute(id, language);
        }
    }

    private void onEpisodesLoaded(List<Episode> episodes)
    {
        cache.put(id, episodes);
        if (callback != null)
            callback.onEpisodesLoaded(episodes);
    }

    public static EpisodeRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new EpisodeRepository(context);
        return instance;
    }

    private class FetchService extends AsyncTask<Object, Void, List<Episode>>
    {
        public final String TAG = FetchService.class.getSimpleName();

        private Callback.EpisodesLoaded callback;

        public FetchService(Callback.EpisodesLoaded callback)
        {
            this.callback = callback;
        }

        @Override
        protected List<Episode> doInBackground(Object... params)
        {
            PodcastService service = PodcastContract.createPodcastService();
            int id = (int) params[0];
            String language = (String) params[1];

            try
            {
                ChannelRoot root = service.loadChannel(id, language).execute().body();

                if (root.getHead() != null)
                {
                    writeNetworkState(context, Result.SUCCESS);
                    return root.getChannel().getEpisodes();
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
        protected void onPostExecute(List<Episode> episodes)
        {
            callback.onEpisodesLoaded(episodes);
        }
    }
}
