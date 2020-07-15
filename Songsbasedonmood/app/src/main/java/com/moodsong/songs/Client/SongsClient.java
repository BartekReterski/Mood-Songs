package com.moodsong.songs.Client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongsClient {

    public  static  final  String BASE_URL= "https://api.discogs.com";

    public static Retrofit retrofit;



    public static Retrofit getRetrofitClient(){

     if(retrofit==null){

         retrofit= new Retrofit.Builder()
                 .baseUrl(BASE_URL)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build();
     }

        return  retrofit;
    }
}
