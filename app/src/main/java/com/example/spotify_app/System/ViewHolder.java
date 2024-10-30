package com.example.spotify_app.System;

import android.view.View;
import android.widget.FrameLayout;

import com.example.spotify_app.Fragment.HistoryFragment;
import com.example.spotify_app.Fragment.HomeFragment;
import com.example.spotify_app.Fragment.LibraryMusicFragment;
import com.example.spotify_app.Fragment.PlaylistFragment;
import com.example.spotify_app.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ViewHolder {
    static ViewHolder _Ins;

    public static ViewHolder Ins()
    {
        if(_Ins == null)
            _Ins = new ViewHolder();
        return _Ins;
    }

    FrameLayout flContainerSmallMusicPlayer;

    LibraryMusicFragment libraryMusicFragment;
    HomeFragment homeFragment;
    PlaylistFragment playlistFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;

    BottomNavigationView bottomNavigationView;

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    public FrameLayout getFlContainerSmallMusicPlayer() {
        return flContainerSmallMusicPlayer;
    }

    public void setFlContainerSmallMusicPlayer(FrameLayout flContainerSmallMusicPlayer) {
        this.flContainerSmallMusicPlayer = flContainerSmallMusicPlayer;
    }

    public void showFlContainerSmallMusicPlayer()
    {
        flContainerSmallMusicPlayer.setVisibility(View.VISIBLE);
    }

    public void hideFlContainerSmallMusicPlayer()
    {
        flContainerSmallMusicPlayer.setVisibility(View.GONE);
    }

    public LibraryMusicFragment getLibraryMusicFragment() {
        if(libraryMusicFragment == null)
            libraryMusicFragment = new LibraryMusicFragment();
        return libraryMusicFragment;
    }

    public void setLibraryMusicFragment(LibraryMusicFragment libraryMusicFragment) {
        this.libraryMusicFragment = libraryMusicFragment;
    }

    public HomeFragment getHomeFragment() {
        if(homeFragment == null)
            homeFragment = new HomeFragment();
        return homeFragment;
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public PlaylistFragment getPlaylistFragment() {
        if(playlistFragment == null)
            playlistFragment = new PlaylistFragment();
        return playlistFragment;
    }

    public void setPlaylistFragment(PlaylistFragment playlistFragment) {
        this.playlistFragment = playlistFragment;
    }

    public HistoryFragment getHistoryFragment() {
        if(historyFragment == null)
            historyFragment = new HistoryFragment();
        return historyFragment;
    }

    public void setHistoryFragment(HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
    }

    public ProfileFragment getProfileFragment() {
        if(profileFragment == null)
            profileFragment = new ProfileFragment();
        return profileFragment;
    }

    public void setProfileFragment(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }
}
