package com.example.spotify_app.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Model.Album;
import com.example.spotify_app.Model.AlbumReponse;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.Model.PlaylistResponse;
import com.example.spotify_app.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlayListFragment extends Fragment {

    Playlist datasource;

    RoundedImageView img;
    ImageButton btnBack;
    TextView tvTitle, tvSubTitle;
    FrameLayout flContainerListSongAlbum;

    public DetailPlayListFragment() {

    }
    public DetailPlayListFragment(Playlist datasource) {
        this.datasource = datasource;
    }

    public static DetailPlayListFragment newInstance(String param1, String param2) {
        DetailPlayListFragment fragment = new DetailPlayListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_play_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadViews(view);
        loadData();
        loadEvents();

    }


    void loadViews(View view)
    {
        img = view.findViewById(R.id.img);
        btnBack = view.findViewById(R.id.btnBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSubTitle = view.findViewById(R.id.tvSubTitle);
        flContainerListSongAlbum = view.findViewById(R.id.flContainerListSongAlbum);
    }

    void loadData()
    {
        Picasso.get().load(datasource.getImage()).into(img);
        tvTitle.setText(datasource.getTitle());
        tvSubTitle.setText("Có " + String.valueOf(datasource.getSongs().size() + " bài hát"));

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerListSongAlbum.getId(), new ListSongFragment(datasource.getSongs()))
                .commit();
    }


    void loadEvents()
    {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}