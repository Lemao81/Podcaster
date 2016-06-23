package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.data.PodcastService;
import com.jueggs.podcaster.model.ChannelRoot;
import com.jueggs.podcaster.model.Episode;

import java.io.IOException;
import java.util.List;

import static com.jueggs.utils.Utils.*;

public class EpisodeRepository
{
    private static EpisodeRepository instance;

    private SparseArray<List<Episode>> cache = new SparseArray<>();
    private Callback.EpisodesLoaded callback;
    private int id;

    public void loadEpisodes(int id, String language, Callback.EpisodesLoaded callback)
    {
        if (hasElements(cache.get(id)) && callback != null)
            callback.onEpisodesLoaded(cache.get(id));
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

    public static EpisodeRepository getInstance()
    {
        if (instance == null)
            instance = new EpisodeRepository();
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

            try
            {
                ChannelRoot root = service.loadChannel((int) params[0], (String) params[1]).execute().body();
                return root.getChannel().getEpisodes();
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
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
