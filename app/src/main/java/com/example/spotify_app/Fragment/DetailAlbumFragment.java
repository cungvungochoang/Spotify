package com.example.spotify_app.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotify_app.Adapter.PlaylistAdapter;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Model.Album;
import com.example.spotify_app.Model.AlbumReponse;
import com.example.spotify_app.Model.PlaylistResponse;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailAlbumFragment extends Fragment {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    int albumID;
    AlbumReponse response;
    Album datasource;

    RoundedImageView img;
    ImageButton btnBack;
    TextView tvTitle, tvSubTitle, tvDes;
    FrameLayout flContainerListSongAlbum;

    public DetailAlbumFragment() {

    }

    public DetailAlbumFragment(int albumID) {
        this.albumID = albumID;
    }



    public static DetailAlbumFragment newInstance(String param1, String param2) {
        DetailAlbumFragment fragment = new DetailAlbumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadViews(view);
        getData();
        loadEvents();

    }

    public void getData() {
        Call<AlbumReponse> call = db.getDetailAlbum(albumID);
        call.enqueue(new Callback<AlbumReponse>() {
            @Override
            public void onResponse(Call<AlbumReponse> call, Response<AlbumReponse> response) {
                DetailAlbumFragment.this.response = response.body();
                datasource = response.body().getAlbum();
                loadData();
            }

            @Override
            public void onFailure(Call<AlbumReponse> call, Throwable t) {
            }
        });
    }

    void loadViews(View view)
    {
        img = view.findViewById(R.id.img);
        btnBack = view.findViewById(R.id.btnBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSubTitle = view.findViewById(R.id.tvSubTitle);
        tvDes = view.findViewById(R.id.tvDes);
        flContainerListSongAlbum = view.findViewById(R.id.flContainerListSongAlbum);
    }

    void loadData()
    {
        Picasso.get().load(datasource.getImage()).into(img);
        tvTitle.setText(datasource.getTitle());
        tvSubTitle.setText(datasource.getArtists());
        tvDes.setText(datasource.getDescription());

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