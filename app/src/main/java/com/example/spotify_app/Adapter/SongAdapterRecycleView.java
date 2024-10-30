package com.example.spotify_app.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.BaseClass.ItemSongClick;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.TypeSong;
import com.example.spotify_app.R;

import java.util.ArrayList;

public class SongAdapterRecycleView extends RecyclerView.Adapter<SongAdapterRecycleView.SongViewHolder> {


    Context context;
    int layout;
    ArrayList<Song> datasource;
    ItemClickListener itemClickListener;
    ItemSongClick openDialogMenu;
    ItemSongClick ckbLikeClick;


    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public ArrayList<Song> getDatasource() {
        return datasource;
    }

    public void setDatasource(ArrayList<Song> datasource) {
        this.datasource = datasource;
    }

    public SongAdapterRecycleView(Context context, int layout, ArrayList<Song> datasource, ItemClickListener itemClickListener) {
        this.context = context;
        this.layout = layout;
        this.datasource = datasource;
        this.itemClickListener = itemClickListener;
    }

    public SongAdapterRecycleView(Context context, int layout, ArrayList<Song> datasource, ItemClickListener itemClickListener
            , ItemSongClick openDialogMenu, ItemSongClick ckbLikeClick) {
        this.context = context;
        this.layout = layout;
        this.datasource = datasource;
        this.itemClickListener = itemClickListener;
        this.openDialogMenu = openDialogMenu;
        this.ckbLikeClick = ckbLikeClick;
    }



    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(layout, parent,false);

        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song item = datasource.get(position);

        holder.setItemClickListener(itemClickListener);

        if(item.getType() == TypeSong.Local)
        {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(item.getSONG_URL());
            byte[] artwork = retriever.getEmbeddedPicture();

            if (artwork != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
//                TypeSong.imgBitmap = bitmap;
                holder.img.setImageBitmap(bitmap);
            } else {
                // There is no artwork embedded in the audio file
            }
        }
        else
        {
            item.loadThumbnailToTarget(context.getContentResolver(), holder.img, new Size(240,240));
        }

        ImageButton imgBtnOpenMenu = holder.imgBtnOpenMenu;
        imgBtnOpenMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogMenu.clicked(item);
            }
        });

        CheckBox ckbLike = holder.ckbLike;
        ckbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox sender = (CheckBox) v;

                ckbLikeClick.clicked((CheckBox) v ,item, sender.isChecked());
            }
        });

        holder.ckbLike.setChecked(item.isLiked());
        holder.tvTitle.setText(item.getTITLE());
        holder.tvArtistNames.setText(item.getARTIST_NAMES());
    }

    @Override
    public int getItemCount() {
        return datasource.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
    {
        ImageView img;
        TextView tvTitle;
        TextView tvArtistNames;
        LinearLayout mainContainer;
        CheckBox ckbLike;

        ImageButton imgBtnOpenMenu;

        private ItemClickListener itemClickListener;


        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);


            img = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtistNames = itemView.findViewById(R.id.tvArtistNames);
            mainContainer = itemView.findViewById(R.id.mainContainer);
            imgBtnOpenMenu = itemView.findViewById(R.id.imgBtnOpenMenu);
            ckbLike = itemView.findViewById(R.id.ckbLike);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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
