package com.example.spotify_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    ArrayList<Playlist> playlists;
    ItemClickListener itemClickListener;

    public PlaylistAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public PlaylistAdapter(ArrayList<Playlist> playlists, ItemClickListener itemClickListener) {
        this.playlists = playlists;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_playlist_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.setItem(playlists.get(position));
        holder.setItemClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        RoundedImageView imgAlbum;
        TextView tvTitle, tvSongCount;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAlbum = itemView.findViewById(R.id.img_album);
            tvTitle = itemView.findViewById(R.id.tv_title_pl);
            tvSongCount = itemView.findViewById(R.id.tv_total_pl);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItem(Playlist playlist) {
            Picasso.get()
                    .load(playlist.getImage())
                    .fit()
                    .centerCrop()
                    .into(imgAlbum);

            tvTitle.setText(playlist.getTitle());
            tvSongCount.setText(playlist.getTotalSong() + " bài hát");
        }

        @Override
        public void onClick(View v) {


            itemClickListener.onClick(v,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }
}
