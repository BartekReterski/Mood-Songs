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

    @GET("/database/search?per_page=3&page=2&genre=pop&key=pSKPIAPwGJmfkjDXBTAF&secret=xmcybONgQdFMkUAjZwCPmBxPiQOMVuYz")
    Call<Example> getSongsExample();
}
