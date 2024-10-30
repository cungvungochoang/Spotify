package com.example.spotify_app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.widget.ViewPager2;


import com.example.spotify_app.Adapter.ViewPagerAdapter;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;
import com.example.spotify_app.System.ViewHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


// Compine ListSongFragment and MusicPlayerFragment
public class ListSongMusicPlayerFragment extends Fragment {

    public static boolean isPaging = false;
    ArrayList<Song> datasource;
    Fragment parentFragment;
    FrameLayout flMainContainer;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView tvNotification;
    ImageButton btnBack;

    boolean isSpecial = false;

    Animation zoomIn, zoomOut;



    public ListSongMusicPlayerFragment() {

    }

    public ListSongMusicPlayerFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public ListSongMusicPlayerFragment(ArrayList<Song> datasource, Fragment parentFragment) {
        this.datasource = datasource;
        this.parentFragment = parentFragment;
    }

    public ListSongMusicPlayerFragment(ArrayList<Song> datasource, Fragment parentFragment, boolean isSpecial) {
        this.datasource = datasource;
        this.parentFragment = parentFragment;
        this.isSpecial = isSpecial;
    }



    public static ListSongMusicPlayerFragment newInstance(String param1, String param2) {
        ListSongMusicPlayerFragment fragment = new ListSongMusicPlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_song_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewHolder.Ins().hideFlContainerSmallMusicPlayer();
        loadViews(view);
        loadEvents();
        loadAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        flMainContainer.startAnimation(zoomIn);
    }

    @Override
    public void onPause() {
        super.onPause();
        ViewHolder.Ins().showFlContainerSmallMusicPlayer();
    }

    protected void loadViews(View view)
    {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        tvNotification = view.findViewById(R.id.tvNotification);
        btnBack = view.findViewById(R.id.btnBack);
        flMainContainer = view.findViewById(R.id.flMainContainer);
    }

    protected void loadEvents()
    {
        ViewPagerAdapter adapter = createViewPagerAdapter();

        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("")
        ).attach();



        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                isPaging = true;
                if(position == 0)
                {
                    tvNotification.setText("Phát nhạc");
                }
                else
                {
                    tvNotification.setText("Danh sách nhạc");
                }
            }
        });


        btnBack.setOnClickListener(v -> {
            ListSongMusicPlayerFragment.isPaging = false;

            Activity parentActivity = getActivity();
            parentActivity.onBackPressed();
        });

    }


    public ViewPagerAdapter createViewPagerAdapter()
    {

        if(datasource == null || datasource.size() == 0)
        {
            return new ViewPagerAdapter(getActivity().getSupportFragmentManager(), new Lifecycle() {
                @Override
                public void addObserver(@NonNull LifecycleObserver observer) {

                }

                @Override
                public void removeObserver(@NonNull LifecycleObserver observer) {

                }

                @NonNull
                @Override
                public State getCurrentState() {
                    return null;
                }
            });
        }
        else
        {
            return new ViewPagerAdapter(getActivity().getSupportFragmentManager()
                    , datasource
                    , isSpecial
                    , new Lifecycle() {
                @Override
                public void addObserver(@NonNull LifecycleObserver observer) {

                }

                @Override
                public void removeObserver(@NonNull LifecycleObserver observer) {

                }

                @NonNull
                @Override
                public State getCurrentState() {
                    return null;
                }
            });
        }


    }

    public void loadAnimation()
    {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

        zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
        zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);

        // Set Easing Function to animation
        zoomOut.setInterpolator(interpolator);
        zoomIn.setInterpolator(decelerateInterpolator);
    }


}