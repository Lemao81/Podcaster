package com.jueggs.podcaster.data.repo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Category;
import com.jueggs.utils.Utils;
import org.json.JSONException;

import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.podcaster.utils.ParseUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.NetUtils.*;
import static com.jueggs.utils.Utils.*;

public class CategoryRepository
{
    public static final String TAG = CategoryRepository.class.getSimpleName();

    private static CategoryRepository instance;

    private List<Category> cache;
    private Callback.CategoriesLoaded callback;
    private Context context;

    public CategoryRepository(Context context)
    {
        this.context = context;
    }

    public void loadCategories(String language, Callback.CategoriesLoaded callback)
    {
        if (hasElements(cache) && callback != null)
        {
            writeNetworkState(context, Result.SUCCESS);
            callback.onCategoriesLoaded(cache);
        }
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

    public static CategoryRepository getInstance(Context context)
    {
        if (instance == null)
            instance = new CategoryRepository(context);
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
            String language = param[0];
            String jsonString = getJsonDataStetho(createCategoriesUri(language));

            int result = new NetworkValidator(context).validate(jsonString);
            if (result == Result.SUCCESS)
                try
                {
                    List<Category> categories = parseCategories(jsonString);
                    writeNetworkState(context, Result.SUCCESS);
                    return categories;
                }
                catch (JSONException e)
                {
                    Log.e(TAG, e.getMessage());
                    writeNetworkState(context, Result.INVALID_DATA);
                    return null;
                }
            writeNetworkState(context, result);
            return null;
        }

        @Override
        protected void onPostExecute(List<Category> categories)
        {
            callback.onCategoriesLoaded(categories);
        }
    }
}
