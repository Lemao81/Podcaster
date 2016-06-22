package com.jueggs.podcaster.utils;

import android.util.Log;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.podcaster.model.Category;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.jueggs.podcaster.data.PodcastContract.*;

public class ParseUtils
{
    public static final String TAG = ParseUtils.class.getSimpleName();

    public static List<Category> parseCategories(String jsonString)
    {
        try
        {
            JSONObject root = new JSONObject(jsonString);
            JSONObject categories = root.getJSONObject(PROP_CATEGORIES);
            return parseCategories(categories);
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private static List<Category> parseCategories(JSONObject categories) throws JSONException
    {
        List<Category> list = new ArrayList<>();

        while (categories.keys().hasNext())
        {
            String index = categories.keys().next();
            Category category = new Category();

            JSONObject json = categories.getJSONObject(index);
            category.setId(json.getString(PROP_ID));
            category.setTitle(json.getString(PROP_TITLE));

            Object subCategories = json.get(PROP_SUBCATEGORIES);
            if (subCategories instanceof JSONObject)
                category.setSubCategories(parseCategories((JSONObject) subCategories));

            list.add(category);
        }
        return list;
    }
}
