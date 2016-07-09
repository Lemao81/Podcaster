package com.jueggs.podcaster.data;

import com.jueggs.podcaster.model.ChannelArrayRoot;
import com.jueggs.podcaster.model.ChannelRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PodcastService
{
    @GET("category/{id}.json")
    Call<ChannelArrayRoot> loadCategory(@Path("id") int id, @Query("lang") String language);

    @GET("channel/{id}.json")
    Call<ChannelRoot> loadChannel(@Path("id") int id, @Query("lang") String language);

    @GET("charts/subscribers.json")
    Call<ChannelArrayRoot> loadCharts(@Query("lang") String language);

    @GET("search.json")
    Call<ChannelArrayRoot> loadSearch(@Query("q") String query);

    @GET("directory.json")
    Call<ChannelArrayRoot> loadNewcomerWithType(@Query("lang") String language, @Query("type") String type);

    @GET("directory.json")
    Call<ChannelArrayRoot> loadNewcomer(@Query("lang") String language);
}
