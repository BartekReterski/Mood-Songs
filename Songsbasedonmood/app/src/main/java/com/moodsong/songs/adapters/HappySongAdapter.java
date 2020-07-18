package com.moodsong.songs.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Result;
import com.moodsong.songs.R;
import com.moodsong.songs.activities.HappySongActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HappySongAdapter extends RecyclerView.Adapter<HappySongAdapter.CustomViewHolder> {

    private List<Result> dataList;
    private Context context;

    public HappySongAdapter(Context context){

        dataList= new ArrayList<>();
        this.context=context;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

//Get a reference to the Views in our layout//

        public final View myView;

        TextView songTite, songYear, songGenreandStyle;
        ImageView youtubeIcon,spotifyIcon, songThumb;

        CustomViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            songTite= myView.findViewById(R.id.songTitle);
            songYear= myView.findViewById(R.id.songYear);
            songGenreandStyle= myView.findViewById(R.id.songGenreandStyle);
            youtubeIcon=myView.findViewById(R.id.youtubeIcon);
            spotifyIcon=myView.findViewById(R.id.spotifyIcon);
            songThumb=myView.findViewById(R.id.songThumb);



        }
    }

    @Override

//Construct a RecyclerView.ViewHolder//

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.songsinfolayout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override

//Set the data//

    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.songTite.setText(dataList.get(position).getTitle());

//sprawdzanie czy jest null i ukrywanie go
        if(holder.songYear.getText().toString().equals("null") || holder.songYear.getText().toString().isEmpty()){

            holder.songYear.setVisibility(View.GONE);
        }else {
            holder.songYear.setText(String.valueOf(dataList.get(position).getYear()));

        }

// przekonwertowanie listy stringow na obiekty

        StringBuilder allStringGenres= new StringBuilder();
        List<String> genres= dataList.get(position).getGenre();
        for(int i=0; i<genres.size(); i++){

            allStringGenres.append(genres.get(i));
            if(i<genres.size() -1) allStringGenres.append(",");
        }

        StringBuilder allStringStyles= new StringBuilder();
        List<String> styles= dataList.get(position).getStyle();
        for(int i=0; i<styles.size(); i++){

            allStringStyles.append(styles.get(i));
            if(i<styles.size() -1) allStringStyles.append(",");
        }

        holder.songGenreandStyle.setText(allStringGenres.toString()+"/"+allStringStyles.toString());

        Picasso.get().load(dataList.get(position).getThumb()).into(holder.songThumb);


        holder.youtubeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(IsAppisInstalled("com.google.android.youtube")){

                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.google.android.youtube");
                    intent.putExtra("query", dataList.get(position).getTitle());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {


                    Toast.makeText(context.getApplicationContext(),"Youtube app is not installed",Toast.LENGTH_LONG).show();
                }




            }
        });


        holder.spotifyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(IsAppisInstalled("com.spotify.music")){

                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.spotify.music");
                    intent.putExtra("query", dataList.get(position).getTitle());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {


                    Toast.makeText(context.getApplicationContext(),"Spotify app is not installed",Toast.LENGTH_LONG).show();
                }



            }
        });

    }

    //sprawdzanie czy aplikacja jest zainstalowana
    private boolean IsAppisInstalled(String name){

        boolean available=true;

        try{

            context.getPackageManager().getPackageInfo(name,0);
        }
        catch (PackageManager.NameNotFoundException e){

            available=false;
        }
        return available;
        }




//Calculate the item count for the RecylerView//

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //pobranie z listy Result
    public  void addResult(List<Result> resultsData){

        dataList.addAll(resultsData);
        notifyDataSetChanged();
    }


}


