package com.example.spotify_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.Model.Album;
import com.example.spotify_app.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecommedAdapter extends RecyclerView.Adapter<RecommedAdapter.RecommedViewHolder> {

    ArrayList<Album> albumList;
    ItemClickListener itemClickListener;
    public RecommedAdapter(ArrayList<Album> albumList) {
        this.albumList = albumList;
    }

    public RecommedAdapter(ArrayList<Album> albumList, ItemClickListener itemClickListener) {
        this.albumList = albumList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecommedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_album_item, parent, false);
        return new RecommedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommedViewHolder holder, int position) {
        holder.setItem(albumList.get(position));
        holder.setItemClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class RecommedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        RoundedImageView imgAlbum;
        TextView tvTitle, tvArtistName;

        private ItemClickListener itemClickListener;


        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }
        public RecommedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.img_album);
            tvTitle = itemView.findViewById(R.id.tv_title_al);
            tvArtistName = itemView.findViewById(R.id.tv_artist_al);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItem(Album album) {
            Picasso.get()
                    .load(album.getImage())
                    .fit()
                    .centerCrop()
                    .into(imgAlbum);
            tvTitle.setText(album.getTitle());
            tvArtistName.setText(album.getArtists());
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
