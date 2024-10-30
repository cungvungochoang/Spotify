package com.example.spotify_app.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spotify_app.Adapter.CustomSongAdapter;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Model.SearchSongResponse;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    // Search song
    SearchView svSong;
    FrameLayout flContainerListSongSearch;
    ListSongFragment listSongFragment;

    ArrayList<Song> songList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controls(view);
        loadFragment();
        makeCss();

        loadEvents(view);
    }

    public void controls(View view) {

        svSong = (SearchView) view.findViewById(R.id.sv_song);
        flContainerListSongSearch = view.findViewById(R.id.flContainerListSongSearch);
    }

    public void makeCss() {
        // Change text, icon color to WHITE

        int whiteColor = ContextCompat.getColor(requireContext(), R.color.white);
        EditText searchEditText = svSong.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(whiteColor);

        ImageView searchIcon = svSong.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(whiteColor);

        ImageView deleteIcon = svSong.findViewById(androidx.appcompat.R.id.search_close_btn);
        deleteIcon.setColorFilter(whiteColor);
    }

    void loadFragment()
    {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerListSongSearch.getId(), listSongFragment = new ListSongFragment(songList))
                .commit();
    }

    public void loadEvents(View view) {
        svSong.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.trim().isEmpty()) {
                    return false;
                }

                Call<SearchSongResponse> call = db.searchSongResource(query);
                call.enqueue(new Callback<SearchSongResponse>() {
                    @Override
                    public void onResponse(Call<SearchSongResponse> call, Response<SearchSongResponse> response) {
                        SearchSongResponse data = response.body();
                        if(data.getSongs() == null) {
                            songList = new ArrayList<>();
                        }
                        else {

                            songList.clear();
                            for(Song item: data.getSongs())
                            {
                                songList.add(item);
                            }

                            if(listSongFragment != null)
                                listSongFragment.getAdapter().notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchSongResponse> call, Throwable t) {
                        Toast.makeText(view.getContext(), "Đã có lỗi vui lòng thử lại !", Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}