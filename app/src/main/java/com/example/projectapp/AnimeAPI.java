package com.example.projectapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimeAPI {
    @GET("v3/anime/{id}")
    Call<Restanimereponse> getanimeresponse(@Path("id") int id);

    @GET("v3/search/anime")
    Call<AnimeList> getanimelist(@Query("q") String name);
}
