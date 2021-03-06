package com.moodsong.songs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.moodsong.songs.Client.SongsClient;
import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Result;
import com.moodsong.songs.R;
import com.moodsong.songs.adapters.SongAdapter;
import com.moodsong.songs.apiInterface.SongsApi;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongActivity extends AppCompatActivity {


    public  static  final  String KEY= "pSKPIAPwGJmfkjDXBTAF";
    public  static  final  String SECRET= "xmcybONgQdFMkUAjZwCPmBxPiQOMVuYz";

    List<Result> listSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_layout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Songs by your mood");

        InternetConnectionCheck();
        GetSongsPagination();


        //inicjalizacja reklamy
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdView adView= findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }

//sprawdzanie polaczenia z internetem
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
    private void GetSongsPagination(){


        try {


            final int nrTracks = getIntent().getIntExtra("SELECTED_NR_TRACKS",0);
            final String genre = getIntent().getStringExtra("SELECTED_GENRE");
            final int year = getIntent().getIntExtra("SELECTED_YEAR",0);



            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);

            Call<Example> call = songsApi.getSongsExamplePagination(nrTracks,year,genre,KEY,SECRET);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {

                    if (response.isSuccessful()) {

                        if (response.body() != null) {

                            Example example = response.body();

                            //String perPages= String.valueOf(example.getPagination().getPages());

                            //obostrzenie zeby nie wywalało jak nic nie ma
                            try{

                                //generowanie losowej strony w pagination
                                Random r = new Random();
                                int low = 1;
                                int high = example.getPagination().getPages();
                                int randomResult = r.nextInt(high - low) + low;
                               /* TextView textViewHelper = findViewById(R.id.textViewHelper);
                                textViewHelper.setText(String.valueOf(randomResult));*/

                                GetSongInfo(randomResult);

                            }catch (Exception ex){


                                ImageView imageNoSearch= findViewById(R.id.imageNoSearch);
                                TextView textNoSearch=findViewById(R.id.textViewNoSearch);

                                imageNoSearch.setVisibility(View.VISIBLE);
                                textNoSearch.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(),"No search result",Toast.LENGTH_LONG).show();
                                System.out.println(ex.getMessage());
                            }



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


    private void  GetSongInfo(int randomResult){


        try{



            final int nrTracks = getIntent().getIntExtra("SELECTED_NR_TRACKS",0);
            final String genre = getIntent().getStringExtra("SELECTED_GENRE");
            final int year = getIntent().getIntExtra("SELECTED_YEAR",0);



        //    Toast.makeText(getApplicationContext(),String.valueOf(randomResult),Toast.LENGTH_LONG).show();


            final AlertDialog alertDialog = new SpotsDialog.Builder()
                    .setContext(SongActivity.this)
                    .build();
            alertDialog.setMessage("Loading suggestion list");
            alertDialog.setCancelable(false);
            alertDialog.show();




            final RecyclerView recyclerView= findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            final SongAdapter songAdapter= new SongAdapter(SongActivity.this);

            recyclerView.setAdapter(songAdapter);

            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);
            Call<Example> call =songsApi.getSongsExampleInfo(nrTracks,year,randomResult,genre,KEY,SECRET);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {


                    if(response.isSuccessful()& response.body()!=null){

                        listSongs= response.body().getResults();



                        songAdapter.addResult(listSongs);
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

            Toast.makeText(this,"Something was wrong"+ex.getMessage(),Toast.LENGTH_LONG).show();
        }



    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.aboutApp:

                Intent intent= new Intent(SongActivity.this,AboutApp.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    }


