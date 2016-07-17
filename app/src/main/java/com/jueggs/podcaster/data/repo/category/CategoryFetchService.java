package com.jueggs.podcaster.data.repo.category;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.jueggs.podcaster.helper.NetworkValidator;
import com.jueggs.podcaster.helper.Result;
import com.jueggs.podcaster.model.Category;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.createCategoriesUri;
import static com.jueggs.podcaster.data.repo.DataResultReceiver.*;
import static com.jueggs.podcaster.utils.ParseUtils.parseCategories;
import static com.jueggs.podcaster.utils.Util.writeNetworkState;
import static com.jueggs.utils.NetUtils.getJsonDataStetho;

public class CategoryFetchService extends IntentService
{
    public static final String TAG = CategoryFetchService.class.getSimpleName();

    public CategoryFetchService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        List<Category> categories = null;
        String language = intent.getStringExtra(EXTRA_LANGUAGE);
        ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        String jsonString = getJsonDataStetho(createCategoriesUri(language));

        int result = new NetworkValidator(this).validate(jsonString);
        if (result == Result.SUCCESS)
            try
            {
                categories = parseCategories(jsonString);
                writeNetworkState(this, Result.SUCCESS);
            }
            catch (JSONException e)
            {
                Log.e(TAG, e.getMessage());
                writeNetworkState(this, Result.INVALID_DATA);
            }
        else
            writeNetworkState(this, result);

        Bundle data = new Bundle();
        data.putSerializable(EXTRA_DATA, (ArrayList) categories);
        resultReceiver.send(RESULT_CODE, data);
    }
}