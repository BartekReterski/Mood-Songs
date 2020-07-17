package com.moodsong.songs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import dmax.dialog.SpotsDialog;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_song_layout);


        InternetConnectionCheck();
        GetHappySongsPagination();
        GetHappySongInfo();

    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

// wlasciwe sprawdzanie polacznie z internetem
    public void InternetConnectionCheck() {
        if (haveNetworkConnection()) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();

        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("No internet connection");
            alertDialogBuilder.setMessage("Please check your internet connection");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }


    //pobranie ilosci stron z ktorych przedstawiana jest dana playlista
    private void GetHappySongsPagination(){



        try {

            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);

            Call<Example> call = songsApi.getSongsExamplePagination(3,100,"pop",KEY,SECRET);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {

                    if (response.isSuccessful()) {
                        //Toast.makeText(getBaseContext(), "OK"+response.message(), Toast.LENGTH_LONG).show();
                        if (response.body() != null) {

                            Example example = response.body();

                            //String perPages= String.valueOf(example.getPagination().getPages());

                            //generowanie losowej strony w pagination
                            Random r = new Random();
                            int low = 1;
                            int high = example.getPagination().getPages();
                            randomResultPages = r.nextInt(high - low) + low;





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
            final AlertDialog alertDialog = new SpotsDialog.Builder()
                    .setContext(HappySongActivity.this)
                    .build();
            alertDialog.setMessage("Loading suggestion list");
            alertDialog.setCancelable(false);
            alertDialog.show();

            RecyclerView recyclerView= findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            final HappySongAdapter happySongAdapter= new HappySongAdapter(HappySongActivity.this);

            recyclerView.setAdapter(happySongAdapter);

            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);
            Call<Example> call =songsApi.getSongsExampleInfo(3,101,"pop",KEY,SECRET);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {


                    if(response.isSuccessful()& response.body()!=null){

                        List<Result> list= response.body().getResults();
                        happySongAdapter.addResult(list);
                        alertDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {

                    alertDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Server Error- Couldn't load data, Please try again"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });



        }catch (Exception ex){

            Toast.makeText(this,"Something was wrong"+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }



    }


}
