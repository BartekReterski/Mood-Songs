package com.moodsong.songs.apiInterface;

import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Pagination;
import com.moodsong.songs.Models.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SongsApi {



    @GET("/database/search")
    Call<Example> getSongsExamplePagination(@Query("per_page") Integer per_page,@Query("year") Integer year, @Query("genre") String genre, @Query("key") String key, @Query("secret") String secret);

    @GET("/database/search")
    Call<Example> getSongsExampleInfo(@Query("per_page") Integer per_page ,@Query("year") Integer year, @Query("page") Integer page, @Query("genre") String genre, @Query("key") String key, @Query("secret") String secret);
}
