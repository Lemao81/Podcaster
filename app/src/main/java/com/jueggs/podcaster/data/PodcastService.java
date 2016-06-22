package com.jueggs.podcaster.data;

import com.jueggs.podcaster.model.CategoryRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PodcastService
{
    @GET("category/{id}.json")
    Call<CategoryRoot> loadCategory(@Path("id") int id, @Query("lang") String language);
}
