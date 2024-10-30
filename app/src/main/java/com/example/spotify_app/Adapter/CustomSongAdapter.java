package com.example.spotify_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomSongAdapter extends RecyclerView.Adapter<CustomSongAdapter.CustomSongViewHolder>{

    ArrayList<Song> songList;
    int layout;

    public CustomSongAdapter(ArrayList<Song> songList, int layout) {
        this.songList = songList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public CustomSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new CustomSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomSongViewHolder holder, int position) {
        holder.setItem(songList.get(position));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class CustomSongViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgSong;
        TextView tvTitleSong, tvArtistSong;

        public CustomSongViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = (RoundedImageView) itemView.findViewById(R.id.img_song);
            tvTitleSong = (TextView) itemView.findViewById(R.id.tv_title_song);
            tvArtistSong = (TextView) itemView.findViewById(R.id.tv_artist_song);
        }


        public void setItem(Song song) {
            Picasso.get()
                    .load(song.getIMAGE())
                    .fit()
                    .centerCrop()
                    .into(imgSong);

            tvTitleSong.setText(song.getTITLE());
            tvArtistSong.setText(song.getARTIST_NAMES());
        }
    }
}
