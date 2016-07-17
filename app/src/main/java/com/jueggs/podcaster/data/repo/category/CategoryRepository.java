package com.jueggs.podcaster.data.repo.category;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.jueggs.podcaster.data.repo.Callback;
import com.jueggs.podcaster.data.repo.DataResultReceiver;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Category;

import java.io.Serializable;
import java.util.List;

import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.Utils.*;

public class CategoryRepository implements Receiver
{
    public static final String TAG = CategoryRepository.class.getSimpleName();

    private static CategoryRepository instance;

    private List<Category> cache;
    private Callback.CategoriesLoaded callback;
    private Context context;
    private DataResultReceiver resultReceiver = new DataResultReceiver(new Handler(), this);

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
            context.startService(new Intent(context, CategoryFetchService.class)
                    .putExtra(EXTRA_LANGUAGE, language)
                    .putExtra(EXTRA_RECEIVER, resultReceiver));
        }
    }

    @Override
    public void onReceiveResult(Serializable result)
    {
        List<Category> categories = (List<Category>) result;
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
}
