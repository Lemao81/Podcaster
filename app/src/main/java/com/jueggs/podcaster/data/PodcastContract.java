package com.jueggs.podcaster.data;

import android.net.Uri;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PodcastContract
{
    public static final String BASE_URI = "https://api.podcast.de";
    public static final String PATH_CATEGORIES = "categories.json";

    public static final String PROP_CATEGORIES = "categories";
    public static final String PROP_ID = "id";
    public static final String PROP_TITLE = "title";
    public static final String PROP_SUBCATEGORIES = "subcategories";

    public static Uri createCategoriesUri()
    {
        return Uri.parse(BASE_URI).buildUpon().appendPath(PATH_CATEGORIES).build();
    }

    public static PodcastService createPodcastService()
    {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URI)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
        return retrofit.create(PodcastService.class);
    }
}
