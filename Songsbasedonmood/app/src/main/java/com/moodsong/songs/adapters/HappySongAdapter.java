package com.moodsong.songs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moodsong.songs.Models.Example;
import com.moodsong.songs.Models.Result;
import com.moodsong.songs.R;
import com.moodsong.songs.activities.HappySongActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HappySongAdapter extends RecyclerView.Adapter<HappySongAdapter.CustomViewHolder> {

    private List<Example> dataList;

    public HappySongAdapter(List<Example> dataList){

        this.dataList = dataList;
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

    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.songTite.setText(dataList.get(position).getResults().get(position).getTitle());
        holder.songYear.setText(String.valueOf(dataList.get(position).getResults().get(position).getYear()));
     //   holder.songGenreandStyle.setText(dataList.get(position).getGenre()+"/"+dataList.get(position).getStyle());

        Picasso.get().load(dataList.get(position).getResults().get(position).getThumb()).into(holder.songThumb);

    }

//Calculate the item count for the RecylerView//

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
