package com.example.spotify_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotify_app.Const.MusicServiceConst;
import com.example.spotify_app.Fragment.ListSongMusicPlayerFragment;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;

import java.util.ArrayList;

public class ListSongMusicPlayerActivity extends AppCompatActivity {

    FrameLayout flContainerFragment;
    ArrayList<Song> datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song_music_player);
        getSupportActionBar().hide();

        flContainerFragment = findViewById(R.id.flMusicPlayer);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            datasource  = (ArrayList<Song>) bundle.getSerializable(MusicServiceConst.DATASOURCE);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerFragment.getId(), new ListSongMusicPlayerFragment(datasource,null, true))
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_out);
    }

}