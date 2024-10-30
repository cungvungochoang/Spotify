package com.example.spotify_app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.spotify_app.Adapter.CustomSongAdapter;
import com.example.spotify_app.Adapter.RecommedAdapter;
import com.example.spotify_app.Adapter.SliderAdapter;
import com.example.spotify_app.Adapter.SongAdapterRecycleView;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.Model.Album;
import com.example.spotify_app.Model.Banner;
import com.example.spotify_app.Model.HomeResponse;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    // Slider
    ViewPager2 vpSlider;
    Handler sliderHandler = new Handler();
    ArrayList<Banner> sliderItems = new ArrayList<>();

    NestedScrollView scContainer;
    FrameLayout flContainerAlbum;

    // Albums recommend
    RecyclerView rvRecommend;

    // New release Song
    RecyclerView rvNewRelease;

    // Search
    ImageButton btnSearch;

    // Fragment
    FrameLayout flContainerListSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controls(view);

        loadData(view);

        loadEvents();

        sliderConfig();
    }

    public void controls(View view) {
        vpSlider = (ViewPager2) view.findViewById(R.id.vp_slider);
        rvRecommend = (RecyclerView) view.findViewById(R.id.rv_recommend);
        btnSearch = (ImageButton) view.findViewById(R.id.btn_search);

        scContainer = view.findViewById(R.id.scContainer);
        flContainerAlbum = view.findViewById(R.id.flContainerAlbum);
        flContainerListSong = view.findViewById(R.id.flContainerListSong);
    }

    public void loadEvents() {
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

    public void loadData(View view) {

        Call<HomeResponse> call = db.getStudentResource();
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                HomeResponse data = response.body();

                // for banner
                for(Banner item : data.getData().getBanners()) {
                    sliderItems.add(new Banner(item.getImageUrl()));
                }

                vpSlider.setAdapter(new SliderAdapter(sliderItems, vpSlider));

                // for albums
                ArrayList<Album> albumList = data.getData().getAlbums();
                RecommedAdapter recommedAdapter = new RecommedAdapter(albumList, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Album selectedItem = albumList.get(position);
                        int albumID = selectedItem.getAlbumId();

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(flContainerAlbum.getId(), new DetailAlbumFragment(albumID))
                                .addToBackStack(DetailAlbumFragment.class.getSimpleName())
                                .commit();
                    }
                });
                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                rvRecommend.setLayoutManager(layoutManager);
                rvRecommend.setAdapter(recommedAdapter);


                // for new release song
                ArrayList<Song> songList = data.getData().getSongs();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(flContainerListSong.getId(), new ListSongFragment(songList))
                        .commit();

            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    public void sliderConfig() {

        vpSlider.setClipToPadding(false);
        vpSlider.setClipChildren(false);
        vpSlider.setOffscreenPageLimit(3);
        vpSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(20));

        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
            }
        });

        vpSlider.setPageTransformer(compositePageTransformer);

        vpSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = vpSlider.getCurrentItem();
            if(currentItem == sliderItems.size()-1) {
                vpSlider.setCurrentItem(0);
            }
            else {
                vpSlider.setCurrentItem(vpSlider.getCurrentItem() + 1);
            }
        }
    };
}