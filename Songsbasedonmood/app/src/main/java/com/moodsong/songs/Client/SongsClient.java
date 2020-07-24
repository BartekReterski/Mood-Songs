package com.moodsong.songs.Client;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongsClient {

    public static final String BASE_URL = "https://api.discogs.com";
    public  static  final  String KEY= "pSKPIAPwGJmfkjDXBTAF";
    public  static  final  String SECRET= "xmcybONgQdFMkUAjZwCPmBxPiQOMVuYz";

    public static Retrofit retrofit;


    public static Retrofit getRetrofitClient() {

        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
