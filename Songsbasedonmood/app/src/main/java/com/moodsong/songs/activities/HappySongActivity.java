package com.moodsong.songs.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.moodsong.songs.Client.SongsClient;
import com.moodsong.songs.MainActivity;
import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Pagination;
import com.moodsong.songs.R;
import com.moodsong.songs.apiInterface.SongsApi;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappySongActivity extends AppCompatActivity {


    int randomResult;

    //intent z MainActivity-> przesłanie danych z formularza -> a nastepnie przeslanie ich do GetHappySongInfo wraz z randomResult zamiast per_pages

    int per_pages=3;
    int page=100;
    String genre="pop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_song_layout);


        GetHappySongsPagination();

    }


//pobranie ilosci stron z ktorych przedstawiana jest dana playlista
    private void GetHappySongsPagination(){



        try {

            SongsApi songsApi= SongsClient.getRetrofitClient().create(SongsApi.class);

            Call<Example> call = songsApi.getSongsExamplePagination(3,100,"pop","pSKPIAPwGJmfkjDXBTAF","xmcybONgQdFMkUAjZwCPmBxPiQOMVuYz");

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {

                    if(response.isSuccessful()){
                        //Toast.makeText(getBaseContext(), "OK"+response.message(), Toast.LENGTH_LONG).show();
                        if(response.body()!=null){

                            Example example= response.body();

                            TextView sampleText=findViewById(R.id.sampleText);
                            //String perPages= String.valueOf(example.getPagination().getPages());

                            //generowanie losowej strony w pagination
                            Random r = new Random();
                            int low = 1;
                            int high = example.getPagination().getPages();
                            randomResult = r.nextInt(high-low) + low;
                            sampleText.setText(String.valueOf(randomResult));
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





    }
}
