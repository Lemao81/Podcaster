package com.jueggs.podcaster.data.repo;

import android.os.AsyncTask;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.utils.ParseUtils;
import com.jueggs.utils.NetUtils;
import com.jueggs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.ParseUtils.*;
import static com.jueggs.utils.NetUtils.*;

public class CategoryRepository
{
    private static CategoryRepository instance;

    private List<Category> cache;
    private Callback.CategoriesLoaded callback;

    public void loadCategories(String language, Callback.CategoriesLoaded callback)
    {
        if (Utils.hasElements(cache) && callback != null)
            callback.onCategoriesLoaded(cache);
        else
        {
            this.callback = callback;
            new FetchService(this::onCategoriesLoaded).execute(language);
        }
    }

    private void onCategoriesLoaded(List<Category> categories)
    {
        cache = categories;
        if (callback != null)
            callback.onCategoriesLoaded(categories);
    }

    public static CategoryRepository getInstance()
    {
        if (instance == null)
            instance = new CategoryRepository();
        return instance;
    }

    private class FetchService extends AsyncTask<String, Void, List<Category>>
    {
        private Callback.CategoriesLoaded callback;

        public FetchService(Callback.CategoriesLoaded callback)
        {
            this.callback = callback;
        }

        @Override
        protected List<Category> doInBackground(String... param)
        {
            String jsonString = getJsonDataStetho(createCategoriesUri(param[0]));
            return parseCategories(jsonString);
        }

        @Override
        protected void onPostExecute(List<Category> categories)
        {
            callback.onCategoriesLoaded(categories);
        }
    }
}
