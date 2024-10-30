package com.example.spotify_app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.BaseClass.InterfaceBackgroundRun;
import com.example.spotify_app.BaseClass.MyRunnable;
import com.example.spotify_app.Const.MusicServiceConst;
import com.example.spotify_app.CustomView.CheckBoxTriStates;
import com.example.spotify_app.CustomView.CustomCheckBox;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.SongResponse;
import com.example.spotify_app.Model.TypeSong;
import com.example.spotify_app.MusicPlayer;
import com.example.spotify_app.R;
import com.example.spotify_app.Services.MusicService.MusicService;
import com.example.spotify_app.System.AccountManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicPlayerFragment extends Fragment {

    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    Fragment parentFragment;
    FrameLayout flMainContainer;
    Song song;
    ImageView imgLogo;
    ImageButton btnBack;
    CheckBox btnBackSong, btnNextSong;
    CheckBox ckbShuffle;
    CustomCheckBox ckbPlay;
    CheckBox ckbLike;
    CheckBoxTriStates ckbRepeat;
    TextView tvTitle, tvArtistNames, tvStartTime, tvEndTime;
    MediaPlayer mMedia;

    boolean isLoading = false;



    // Behaviour play music
    int typeReapeat = MusicPlayer.NONE_REPEAT;

    private Handler hdlr = new Handler();

    SeekBar songPrgs;
    int oTime = 0, sTime = 0, eTime = 0;

    // Drag progress bar song
    int destinationTime = 0;
    boolean isDragging = false;

    public void setSong(Song song) {
        this.song = song;
    }

    // Animation
    Animation zoomOut;
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
            case MusicService.ACTION_UPDATE:
            {
                ckbLike.setChecked(MusicPlayer.Ins().getCurSong().isLiked());
            }
            case MusicService.LOAD_DATA:
            {
                loadDataWithoutPrepare();
                loadLayoutPlayAudio();
                break;
            }
            case MusicService.ACTION_PLAY:
            {
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
                btnBackSong_Clicked();
                break;
            }
            case MusicService.ACTION_NEXT:
            {
                btnNextSong_Clicked();
                break;
            }
            case MusicService.ACTION_CLEAR:
            {
                loadLayoutPauseAudio();
                break;
            }
        }
    }

    public MusicPlayerFragment() {
    }

    public MusicPlayerFragment(Song song) {
        this.song = song;
    }

    public MusicPlayerFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public static MusicPlayerFragment newInstance(String param1, String param2) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
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
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadViews(view);
        loadEvents();
        loadAnimations();
        loadRipples();
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
    }


    @Override
    public void onResume() {
        if(song == null)
            setSong(MusicPlayer.Ins().getPrecentlySong());
        loadData_PlayAudio();
        super.onResume();
    }

    @Override
    public void onPause() {
        oTime = 0;
        hdlr.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hdlr.removeCallbacks(UpdateSongTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    public void loadData_PlayAudio() {

        // Paging ViewPager
        if(ListSongMusicPlayerFragment.isPaging)
        {
            if(!song.checkSimilar(MusicPlayer.Ins().getCurSong()))
            {
                loadDataWithoutPrepare();
            }
            else
            {
                loadTime();
                if(MusicPlayer.Ins().isPlayingMusic())
                    loadLayoutPlayAudio();
                ListSongMusicPlayerFragment.isPaging = false;
            }
            return;
        }

        // Load Data from viewitem recycle
        loadingView();


        // Create service
        ExecutorService executorService = Executors.newFixedThreadPool(1);


        // Create task
        Runnable task = new Runnable() {
            @Override
            public void run() {
                loadData();
                if (song == null)
                    return;
                loadLayoutPlayAudio();
                sendNotificationMedia(MusicService.ACTION_PLAY);

                ListSongMusicPlayerFragment.isPaging = false;

            }
        };

        // Excute task
        executorService.execute(task);

        // Shut down service
        executorService.shutdown();
    }

    public void loadData_WithoutPlayAudio() {
        loadingView();


        // Create service
        ExecutorService executorService = Executors.newFixedThreadPool(1);


        // Create task
        Runnable task = new Runnable() {
            @Override
            public void run() {
                loadDataWithoutPrepare();
                if (song == null)
                    return;
                loadLayoutPlayAudio();
            }
        };

        // Excute task
        executorService.execute(task);

        // Shut down service
        executorService.shutdown();
    }


    public  void prepareSong() {
        Song precentlySong = MusicPlayer.Ins().getPrecentlySong();
        Song curSong = MusicPlayer.Ins().getCurSong();

        if (curSong.checkSimilar(precentlySong)) {
            mMedia = MusicPlayer.Ins().getMedia();
        } else {
            mMedia = new MediaPlayer();
            MusicPlayer.Ins().setMedia(mMedia);
            try {

                String audioUrl = curSong.getSONG_URL();
                mMedia.setDataSource(audioUrl);
                mMedia.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadViews(View view) {
        flMainContainer = view.findViewById(R.id.flMainContainer);

        imgLogo = view.findViewById(R.id.imgLogo);
        songPrgs = view.findViewById(R.id.sBar);
        btnBack = view.findViewById(R.id.btnBack);

        ckbRepeat = view.findViewById(R.id.ckbRepeat);
        btnBackSong = view.findViewById(R.id.btnBackSong);
        ckbPlay = view.findViewById(R.id.ckbPlay);
        btnNextSong = view.findViewById(R.id.btnNextSong);
        ckbShuffle = view.findViewById(R.id.ckbShuffle);

        ckbLike = view.findViewById(R.id.ckbLike);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtistNames = view.findViewById(R.id.tvArtistNames);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);
    }

    protected void loadEvents() {


        songPrgs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                destinationTime = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                songPrgs.setProgress(destinationTime);
                mMedia.seekTo(destinationTime);
                destinationTime = 0;
            }
        });

        ckbLike.setOnClickListener(v -> {
            song.setLiked(ckbLike.isChecked());

            MusicPlayer.Ins().findSongIndex(song).setLiked(ckbLike.isChecked());

            if(!AccountManager.Ins().isLogin())
            {
                Toast.makeText(getActivity(), "Chưa đăng nhập !", Toast.LENGTH_SHORT).show();
                ckbLike.setChecked(false);
                return;
            }

            if(ckbLike.isChecked())
                Toast.makeText(getActivity(), "Đã thêm vào bài hát yêu thích", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Đã xóa khỏi bài hát yêu thích", Toast.LENGTH_SHORT).show();


            int userID = AccountManager.Ins().getUser().getUserId();
            int songID = song.getSONG_ID();
            addSongToPlayListFavortite(songID, userID);
            sendNotificationMedia(MusicService.ACTION_UPDATE);
        });

        ckbRepeat.setOnClickListener(v -> {

            CheckBoxTriStates sender = (CheckBoxTriStates) v;
            typeReapeat = sender.getState();
        });

        btnBackSong.setOnClickListener(v -> {
            sendNotificationMedia(MusicService.ACTION_BACK);
            btnBackSong.setChecked(false);
        });

        ckbPlay.setOnClickListener(v -> {

            if (ckbPlay.getState() == CustomCheckBox.UNCHECKED) {
                loadLayoutPlayAudio();
                sendNotificationMedia(MusicService.ACTION_PLAY);
            } else {
                loadLayoutPauseAudio();
                sendNotificationMedia(MusicService.ACTION_PAUSE);
            }
        });

        btnNextSong.setOnClickListener(v -> {
            sendNotificationMedia(MusicService.ACTION_NEXT);
            btnNextSong.setChecked(false);
        });

        ckbShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicPlayer.Ins().setRandom(ckbShuffle.isChecked());
            }
        });
    }

    public void loadAnimations() {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
        rotationButtonPlay = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_buttonplay);
        rotationImage = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_image);

        // Set Easing Function to animation
        zoomOut.setInterpolator(interpolator);
        rotationButtonPlay.setInterpolator(interpolator);
        rotationImage.setInterpolator(interpolator);
    }

    public void loadRipples() {

        addRippleToTarget(songPrgs);

        addRippleToTarget(ckbRepeat);
        addRippleToTarget(btnBackSong);
        addRippleToTarget(ckbPlay);
        addRippleToTarget(btnNextSong);
        addRippleToTarget(ckbShuffle);
    }

    public void addRippleToTarget(View target) {
        // Create a new RippleDrawable with a color and no mask
        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.foreground_musicplayer_40)), null, null);

        // Create a new StateListDrawable for the checkbox background
        StateListDrawable stateListDrawable = new StateListDrawable();


        // Set the RippleDrawable as the background of the checkbox
        target.setBackground(rippleDrawable);

        // Set the StateListDrawable as the mask for the RippleDrawable
        rippleDrawable.setDrawableByLayerId(android.R.id.mask, stateListDrawable);
    }

    public void goToDesSong() {
        ckbPlay.savePrecentlyState();
        loadLayoutPauseAudio();
        song = MusicPlayer.Ins().getCurSong();
//        loadData_WithoutPlayAudio();
        loadingView();
        oTime = 0;
    }

    public void btnBackSong_Clicked() {
        btnBackSong.setChecked(false);
        if (isLoading) {
//            Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            return;
        }
        isLoading = true;
        goToDesSong();
    }

    public void btnNextSong_Clicked() {
        btnNextSong.setChecked(false);
        if (isLoading) {
//            Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            return;
        }
        isLoading = true;
        goToDesSong();
    }

    protected void loadData() {
        if (MusicPlayer.Ins().getCurSong() == null)
            return;
        setSong(MusicPlayer.Ins().getCurSong());
        prepareSong();
        loadTime();

        MyRunnable runnable = new MyRunnable(this, () -> {

            if(song.getType() == TypeSong.Local)
            {
                imgLogo.setImageBitmap(song.getImgBitmap());
            }
            else
            {
                song.loadThumbnailToTarget(getActivity().getContentResolver(), imgLogo, new Size(777, 777));
            }
            ckbLike.setChecked(song.isLiked());
            tvTitle.setText(song.getTITLE());
            tvArtistNames.setText(song.getARTIST_NAMES());
        });
        runnable.excute();
    }

    protected void loadDataWithoutPrepare() {
        if (MusicPlayer.Ins().getCurSong() == null)
            return;
        setSong(MusicPlayer.Ins().getCurSong());
        mMedia = MusicPlayer.Ins().getMedia();
        loadTime();

        MyRunnable runnable = new MyRunnable(this, () -> {

            if(song.getType() == TypeSong.Local)
            {
                imgLogo.setImageBitmap(song.getImgBitmap());
            }
            else
            {
                song.loadThumbnailToTarget(getActivity().getContentResolver(), imgLogo, new Size(777, 777));
            }
            ckbLike.setChecked(song.isLiked());
            tvTitle.setText(song.getTITLE());
            tvArtistNames.setText(song.getARTIST_NAMES());
        });
        runnable.excute();
    }

    protected void loadTime() {
        MyRunnable runnable = new MyRunnable(this, new InterfaceBackgroundRun() {
            @Override
            public void doInBackground() {

                try {
                    eTime = mMedia.getDuration();

                }
                catch (Exception e){}

                tvEndTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(eTime), TimeUnit.MILLISECONDS.toSeconds(eTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));

                tvStartTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
            }
        });
        runnable.excute();
    }

    public void loadLayoutPlayAudio() {

        MyRunnable runnable = new MyRunnable(this, new InterfaceBackgroundRun() {
            @Override
            public void doInBackground() {

                imgLogo.startAnimation(rotationImage);
                ckbPlay.clearAnimation();
                ckbPlay.setEnabled(true);
                ckbPlay.setState(CustomCheckBox.CHECKED);
                isLoading = false;
//                ckbPlay.setChecked(true);
            }
        });
        runnable.excute();

        loadTime();


        if (oTime == 0) {
            try {
                songPrgs.setMax(mMedia.getDuration());
            }
            catch (Exception e){}
            oTime = 1;
        }

        songPrgs.setProgress(sTime);
        hdlr.postDelayed(UpdateSongTime, 1000);
    }


    public void loadLayoutPauseAudio() {
        MyRunnable runnable = new MyRunnable(this, new InterfaceBackgroundRun() {
            @Override
            public void doInBackground() {
                imgLogo.clearAnimation();
                ckbPlay.setState(CustomCheckBox.UNCHECKED);
            }
        });
        runnable.excute();
    }

    public void cleanSong() {
        if (typeReapeat == MusicPlayer.NONE_REPEAT) {
            loadData();
            loadLayoutPauseAudio();
            sendNotificationMedia(MusicService.ACTION_PAUSE);
        } else if (typeReapeat == MusicPlayer.REPEAT_ONE) {
            loadData();
            loadLayoutPauseAudio();
            loadLayoutPlayAudio();
            sendNotificationMedia(MusicService.ACTION_PLAY);
        } else if (typeReapeat == MusicPlayer.REPEAT_ALL) {
            btnNextSong_Clicked();
            sendNotificationMedia(MusicService.ACTION_NEXT);
        }
    }

    public void loadingView() {
        Drawable icon = AppCompatResources.getDrawable(getContext(), R.drawable.default_logo);
        imgLogo.setImageDrawable(icon);

        ckbPlay.setEnabled(false);
        ckbPlay.setState(CustomCheckBox.UNKNOW);
        ckbPlay.startAnimation(rotationButtonPlay);
    }

    // Add song to favorite playlist
    void addSongToPlayListFavortite(int songID, int userID)
    {
        JsonObject gsonobject = new JsonObject();

        JSONObject jsonObject=new JSONObject();
        try{
            try{

                jsonObject.put("userId", userID);
                jsonObject.put("songId", songID);
            }
            catch (JSONException e)
            {

            }
            JsonParser jsonParser= new JsonParser();
            gsonobject=(JsonObject)jsonParser.parse(jsonObject.toString());
        }
        catch (Exception e){}
        Call<SongResponse> studentAddCall = db.insertSongsFavorite(gsonobject);
        studentAddCall.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {

            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Thao tác thất bại !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if (!isDragging) {

                try {
                    sTime = mMedia.getCurrentPosition();
                }
                catch (Exception e){
                    Log.e("Error Music Player Fragment", "Error");

                }
                tvStartTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));

                songPrgs.setProgress(sTime);

                if (eTime <= sTime)
                    cleanSong();
            }
            hdlr.postDelayed(this, 1000);
        }
    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Từ chối thông báo", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

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

}