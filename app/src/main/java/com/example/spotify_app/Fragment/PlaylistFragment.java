package com.example.spotify_app.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.spotify_app.Adapter.PlaylistAdapter;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.Dialog.AddPlayListDialog;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.Model.PlaylistResponse;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistFragment extends Fragment {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    // Playlist
    RecyclerView rvPlaylist;
    ArrayList<Playlist> playlists = new ArrayList<>();

    FrameLayout flContainerDetailPlayList;

    ImageButton btnOpenPlaylistDialog;
    ImageButton btnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controls(view);

        loadData(view);

        loadEvents();
    }

    public void controls(View view) {
        rvPlaylist = (RecyclerView) view.findViewById(R.id.rv_playlist);
        btnOpenPlaylistDialog = (ImageButton) view.findViewById(R.id.btn_open_playlist_dialog);
        btnSearch = (ImageButton) view.findViewById(R.id.btn_search);

        flContainerDetailPlayList = view.findViewById(R.id.flContainerDetailPlaylist);
    }

    public void loadData(View view) {
        Call<PlaylistResponse> call = db.getPlaylistResource(AccountManager.Ins().getUser().getUserId());
        call.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                PlaylistResponse data = response.body();

                playlists = data.getPlaylists();
                PlaylistAdapter adapter = new PlaylistAdapter(playlists, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Playlist selectedItem = playlists.get(position);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(flContainerDetailPlayList.getId(), new DetailPlayListFragment(selectedItem))
                                .addToBackStack(DetailPlayListFragment.class.getSimpleName())
                                .commit();
                    }
                });

                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
                rvPlaylist.setLayoutManager(gridLayoutManager);
                rvPlaylist.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Toast.makeText(view.getContext(), "Đã có lỗi vui lòng thử lại sau", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadEvents() {
        btnOpenPlaylistDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPlayListDialog dialogFragment = new AddPlayListDialog(playlists);
                dialogFragment.show(getParentFragmentManager(), "playlistDialog");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new SearchFragment());
            }
        });
    }

    protected void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fm_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}