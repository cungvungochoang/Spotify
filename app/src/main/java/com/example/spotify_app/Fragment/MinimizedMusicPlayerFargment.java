package com.example.spotify_app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.spotify_app.Activity.ListSongMusicPlayerActivity;
import com.example.spotify_app.Activity.MainActivity;
import com.example.spotify_app.BaseClass.MyRunnable;
import com.example.spotify_app.Const.MusicServiceConst;
import com.example.spotify_app.CustomView.CustomCheckBox;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.TypeSong;
import com.example.spotify_app.MusicPlayer;
import com.example.spotify_app.R;
import com.example.spotify_app.Services.MusicService.MusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MinimizedMusicPlayerFargment extends Fragment {

    MainActivity activity;
    Fragment parentFragment;
    Song song;
    CustomCheckBox ckbPlay;
    CircleImageView img;
    ImageButton btnBackSong, btnNextSong;
    TextView tvTitle, tvArtistNames;
    MediaPlayer mMedia;

    // History list song
    ArrayList<Song> songList = new ArrayList<>();

    // Animation
    Animation rotationButtonPlay, rotationImage;



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if(bundle == null)
            {
                return;
            }

            int actionMusic = bundle.getInt(MusicServiceConst.ACTION_MUSIC, 0);
            handleActionMusic(actionMusic);
        }
    };

    public void handleActionMusic(int actionMusic)
    {
        mMedia = MusicPlayer.Ins().getMedia();
        switch (actionMusic)
        {

            case MusicService.LOAD_DATA:
            {
                loadData();
                loadLayoutPlayAudio();
                break;
            }
            case MusicService.ACTION_PLAY:
            {
                songList = new ArrayList<>();
                songList = getSelectedSongsFromSharedPreferences();
                setSong(MusicPlayer.Ins().getCurSong());
                if (!songList.contains(getSong())) {

                    int index = -1;
                    for(Song item: songList)
                    {

                        if(item.checkSimilar(getSong()))
                        {
                            index = 0;
                            break;
                        }
                    }
                    if(index < 0)
                    {
                        songList.add(getSong());
                        saveSelectedSongToSharedPreferences(songList);
                    }

                }

                loadData();
                loadLayoutPlayAudio();
                break;
            }
            case MusicService.ACTION_PAUSE:
            {
                loadLayoutPauseAudio();
                break;
            }
            case MusicService.ACTION_BACK:
            {
                goToAnotherSong();
                break;
            }
            case MusicService.ACTION_NEXT:
            {
                goToAnotherSong();
                break;
            }
            case MusicService.ACTION_CLEAR:
            {
                loadLayoutPauseAudio();
                break;
            }
        }
    }


    public Song getSong() {
        return song;
    }

    private Handler hdlr = new Handler();

    public void setSong(Song song) {
        this.song = song;
    }

    public MinimizedMusicPlayerFargment() {

    }

    public MinimizedMusicPlayerFargment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public MinimizedMusicPlayerFargment(MainActivity activity) {
        this.activity = activity;
    }

    public static MinimizedMusicPlayerFargment newInstance(String param1, String param2) {
        MinimizedMusicPlayerFargment fragment = new MinimizedMusicPlayerFargment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(MusicServiceConst.SEND_DATA_TO_FRAGMENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minimized_music_player_fargment, container, false);
    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadViews(view);
        loadEvents();
        loadAnimations();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    protected void loadViews(View view)
    {
        img = view.findViewById(R.id.img);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtistNames = view.findViewById(R.id.tvArtistNames);

        ckbPlay = view.findViewById(R.id.ckbPlay);
        btnNextSong = view.findViewById(R.id.btnNextSong);
        btnBackSong = view.findViewById(R.id.btnBackSong);

        ckbPlay.setState(CustomCheckBox.UNCHECKED);
    }

    protected void loadEvents()
    {
        ckbPlay.setOnClickListener(v -> {
            if(ckbPlay.getState() == CustomCheckBox.UNCHECKED)
            {
                loadLayoutPlayAudio();
                sendNotificationMedia(MusicService.ACTION_PLAY);
            }
            else
            {
                loadLayoutPauseAudio();
                sendNotificationMedia(MusicService.ACTION_PAUSE);
            }
        });

        btnBackSong.setOnClickListener(v -> sendNotificationMedia(MusicService.ACTION_BACK));
        btnNextSong.setOnClickListener(v -> sendNotificationMedia(MusicService.ACTION_NEXT));

        img.setOnClickListener(v -> showMusicPlayer());

        tvTitle.setOnClickListener(v -> showMusicPlayer());

        tvArtistNames.setOnClickListener(v -> showMusicPlayer());
    }

    public void showMusicPlayer()
    {
        MusicPlayer.Ins().setCurSong(song);

        ArrayList<Song> datasource = MusicPlayer.Ins().getLstSong();
        ArrayList<Song> datasourceTemp = new ArrayList<>();
        for(Song songItem: datasource)
        {
            datasourceTemp.add(new Song(songItem));
        }

        changeScreen(datasourceTemp);
    }

    public void changeScreen(ArrayList<Song> datasource)
    {
        // Mở lên một activity (trình phát nhạc)
        Intent intent = new Intent(getActivity(), ListSongMusicPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MusicServiceConst.DATASOURCE, datasource);
        intent.putExtras(bundle);
        startActivity(intent);
        // Hiệu ứng xuất hiện của Activity
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public void goToAnotherSong()
    {
        ckbPlay.savePrecentlyState();
        loadingImg();
    }

    public void loadLayoutPlayAudio()
    {
        MyRunnable runnable = new MyRunnable(MinimizedMusicPlayerFargment.this, () -> {
            img.startAnimation(rotationImage);
            ckbPlay.clearAnimation();
            ckbPlay.setEnabled(true);
            ckbPlay.setState(CustomCheckBox.CHECKED);
        });
        runnable.excute();
    }

    public void loadLayoutPauseAudio()
    {
        MyRunnable runnable = new MyRunnable(MinimizedMusicPlayerFargment.this, () -> {
            img.clearAnimation();
            ckbPlay.setState(CustomCheckBox.UNCHECKED);
        });
        runnable.excute();
    }

    // Prepare song of current song
    protected void prepareSong()
    {
        mMedia = MusicPlayer.Ins().getMedia();
    }

    // Prepare song of the next song
    protected void prepareSong(Song item)
    {
        mMedia = new MediaPlayer();
        MusicPlayer.Ins().setMedia(mMedia);
        mMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
        String audioUrl = item.getSONG_URL();

        try {
            mMedia.setDataSource(audioUrl);
            mMedia.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadData()
    {
        if(song == null && MusicPlayer.Ins().getCurSong() == null)
            return;
        setSong(MusicPlayer.Ins().getCurSong());

        if(song.getType() == TypeSong.Local)
        {
            img.setImageBitmap(song.getImgBitmap());
        }
        else
        {
            song.loadThumbnailToTarget(getActivity().getContentResolver(), img, new Size(75, 75));
        }

        tvTitle.setText(song.getTITLE());
        tvArtistNames.setText(song.getARTIST_NAMES());


        if(MusicPlayer.Ins().isPlayingMusic())
            loadLayoutPlayAudio();
        else
            loadLayoutPauseAudio();
    }

    public void loadAnimations()
    {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        rotationButtonPlay = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotation_buttonplay);
        rotationImage = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotation_image);

        // Set Easing Function to animation
        rotationButtonPlay.setInterpolator(interpolator);
        rotationImage.setInterpolator(interpolator);
    }

    public void loadingImg()
    {
//        Drawable background = AppCompatResources.getDrawable(getContext(), R.drawable.default_backgroundlogo);
//        img.setBackground(background);
        Drawable icon = AppCompatResources.getDrawable(getContext(), R.drawable.default_logo);
        img.setImageDrawable(icon);

        ckbPlay.setEnabled(false);
        ckbPlay.setState(CustomCheckBox.UNKNOW);
        ckbPlay.startAnimation(rotationButtonPlay);
    }

    public void sendNotificationMedia(int actionMusic) {
        if(song == null)
            return;
        song = MusicPlayer.Ins().getCurSong();
        Song temp = new Song(song);
        if(actionMusic == MusicService.ACTION_BACK || actionMusic == MusicService.ACTION_NEXT)
            temp = null;

        Intent intent = new Intent(getActivity(), MusicService.class);
        Bundle bundle = new Bundle();

        // Put data
        bundle.putSerializable(MusicServiceConst.DATA_SONG, temp);
        bundle.putInt(MusicServiceConst.ACTION_MUSIC, actionMusic);

        intent.putExtras(bundle);

        getActivity().startService(intent);
    }

    public void saveSelectedSongToSharedPreferences(ArrayList<Song> selectedSongs) {
        JSONArray jsonArray = new JSONArray();
        for (Song song : selectedSongs) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("song_id", song.getSONG_ID());
                jsonObject.put("song_name", song.getTITLE());
                jsonObject.put("artist_name", song.getARTIST_NAMES());
                jsonObject.put("song_url", song.getSONG_URL());
                jsonObject.put("image_path", song.getIMAGE());
                jsonObject.put("release_date", song.getRELEASE_DATE());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("song_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("selected_songs", jsonArray.toString());
        editor.apply();
    }
    private ArrayList<Song> getSelectedSongsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("song_info", Context.MODE_PRIVATE);
        String selectedSongsJson = sharedPreferences.getString("selected_songs", "");

        if (!selectedSongsJson.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(selectedSongsJson);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int songId = jsonObject.getInt("song_id");
                    String songName = jsonObject.getString("song_name");
                    String artistName = jsonObject.getString("artist_name");
                    String songUrl = jsonObject.getString("song_url");
                    String imagePath = jsonObject.getString("image_path");
                    String releaseDate = jsonObject.getString("release_date");
                    Song song = new Song(songId, songName, artistName, imagePath, songUrl, releaseDate);
                    songList.add(song);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return songList;
    }


}