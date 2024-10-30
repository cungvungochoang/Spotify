package com.example.spotify_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayListAdapterV2 extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Playlist> datasource;

    public ArrayList<Playlist> getDatasource() {
        return datasource;
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public PlayListAdapterV2(Context context, int layout, ArrayList<Playlist> datasource) {
        this.context = context;
        this.layout = layout;
        this.datasource = datasource;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Playlist item =datasource.get(position);

        if(view == null)
            view = LayoutInflater.from(context).inflate(layout, parent, false);

        ImageView img = view.findViewById(R.id.img);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSubTitle = view.findViewById(R.id.tvSubTitle);


        Picasso.get().load(item.getImage()).into(img);
        tvTitle.setText(item.getTitle());
        tvSubTitle.setText(AccountManager.Ins().getUser().getFullName());
        return view;
    }
}
