package com.moodsong.songs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.moodsong.songs.Client.SongsClient;
import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Result;
import com.moodsong.songs.R;
import com.moodsong.songs.adapters.HappySongAdapter;
import com.moodsong.songs.apiInterface.SongsApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappySongActivity extends AppCompatActivity {


    int randomResultPages;

    //intent z MainActivity-> przesłanie danych z formularza -> a nastepnie przeslanie ich do GetHappySongInfo wraz z randomResult zamiast per_pages
    int per_pages=3;
    int page=100;
    String genre="pop";
    public  static  final  String KEY= "pSKPIAPwGJmfkjDXBTAF";
    public  static  final  String SECRET= "xmcybONgQdFMkUAjZwCPmBxPiQOMVuYz";

    private HappySongAdapter happySongAdapter;
    private RecyclerView recyclerView;
   private ArrayList<Result> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_song_layout);


        GetHappySongsPagination();
        GetHappySongInfo();

    }


//pobranie ilosci stron z ktorych przedstawiana jest dana playlista
    private void GetHappySongsPagination(){



        try {

            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);

            Call<Example> call = songsApi.getSongsExamplePagination(3,100,"pop",KEY,SECRET);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {

                    if(response.isSuccessful()){
                        //Toast.makeText(getBaseContext(), "OK"+response.message(), Toast.LENGTH_LONG).show();
                        if(response.body()!=null){

                            Example example= response.body();

                            //String perPages= String.valueOf(example.getPagination().getPages());

                            //generowanie losowej strony w pagination
                            Random r = new Random();
                            int low = 1;
                            int high = example.getPagination().getPages();
                            randomResultPages = r.nextInt(high-low) + low;





                        }
                    }


                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {

                    Toast.makeText(getBaseContext(), "Server Error- Couldn't load data, Please try again"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception ex){

            Toast.makeText(this,"Something was wrong"+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }


    private void  GetHappySongInfo(){


        try{


            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);
            Call<Example> call= songsApi.getSongsExampleInfo(3,randomResultPages,"pop",KEY,SECRET);

          call.enqueue(new Callback<Example>() {
              @Override
              public void onResponse(Call<Example> call, Response<Example> response) {

                  Example example= response.body();


                  songList= new ArrayList<>(response.body());
                  recyclerView = findViewById(R.id.recyclerView);
                  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HappySongActivity.this);
                  recyclerView.setLayoutManager(layoutManager);
                  happySongAdapter= new HappySongAdapter(example)
              }

              @Override
              public void onFailure(Call<Example> call, Throwable t) {

                  Toast.makeText(getBaseContext(), "Server Error- Couldn't load data, Please try again"+t.getMessage(), Toast.LENGTH_LONG).show();
              }
          });


        }catch (Exception ex){

            Toast.makeText(this,"Something was wrong"+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }



    }

    private void loadDataList(List<Result> songList) {

//Get a reference to the RecyclerView//

        recyclerView = findViewById(R.id.recyclerView);
        happySongAdapter = new HappySongAdapter(songList);

//Use a LinearLayoutManager with default vertical orientation//

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HappySongActivity.this);
        recyclerView.setLayoutManager(layoutManager);

//Set the Adapter to the RecyclerView//

        recyclerView.setAdapter(happySongAdapter);
    }

}
