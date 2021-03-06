package com.jueggs.podcaster.data;

import android.net.Uri;
import android.util.SparseArray;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PodcastContract
{
    public static final String BASE_URI = "https://api.podcast.de";
    public static final String PATH_CATEGORIES = "categories.json";

    public static final String QRY_LANG = "lang";

    public static final String PROP_CATEGORIES = "categories";
    public static final String PROP_ID = "id";
    public static final String PROP_TITLE = "title";
    public static final String PROP_SUBCATEGORIES = "subcategories";

    public static final String LANG_DE = "de";
    public static final String LANG_EN = "en";

    public static final String CHANNEL_TYPE_AUDIO_STRING = "1";
    public static final String CHANNEL_TYPE_VIDEO_STRING = "2";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";
    public static final int CHANNEL_TYPE_AUDIO = 1;
    public static final int CHANNEL_TYPE_VIDEO = 2;
    public static final SparseArray<String> CHANNEL_TYPES = new SparseArray<>(2);

    public static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";

    static
    {
        CHANNEL_TYPES.put(CHANNEL_TYPE_AUDIO, TYPE_AUDIO);
        CHANNEL_TYPES.put(CHANNEL_TYPE_VIDEO, TYPE_VIDEO);
    }

    public static Uri createCategoriesUri(String language)
    {
        return Uri.parse(BASE_URI).buildUpon().appendPath(PATH_CATEGORIES).appendQueryParameter(QRY_LANG, language).build();
    }

    public static PodcastService createPodcastService()
    {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URI).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
        return retrofit.create(PodcastService.class);
    }
}
