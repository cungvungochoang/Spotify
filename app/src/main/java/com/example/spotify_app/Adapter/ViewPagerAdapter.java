package com.example.spotify_app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.spotify_app.Fragment.ListSongFragment;
import com.example.spotify_app.Fragment.MusicPlayerFragment;
import com.example.spotify_app.Model.Song;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {


    ArrayList<Song> datasource;
    boolean isSpecial;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, ArrayList<Song> datasource, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.datasource = datasource;
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, ArrayList<Song> datasource, boolean isSpecial, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.datasource = datasource;
        this.isSpecial = isSpecial;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
            {
                return  new MusicPlayerFragment();
            }
            case 1:
            {
                if(datasource != null && datasource.size() != 0)
                    return new ListSongFragment(datasource, isSpecial);
                else
                    return new ListSongFragment(true);

            }

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
