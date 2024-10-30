package com.example.spotify_app.Fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotify_app.Activity.ListSongMusicPlayerActivity;
import com.example.spotify_app.Adapter.PlayListAdapterV2;
import com.example.spotify_app.Adapter.PlaylistAdapter;
import com.example.spotify_app.Adapter.SongAdapterRecycleView;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.BaseClass.ItemSongClick;
import com.example.spotify_app.Const.MusicServiceConst;
import com.example.spotify_app.Model.AlbumReponse;
import com.example.spotify_app.Model.FavoriteSong;
import com.example.spotify_app.Model.FavoriteSongResponse;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.Model.PlaylistResponse;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.SongResponse;
import com.example.spotify_app.MusicPlayer;
import com.example.spotify_app.R;
import com.example.spotify_app.Services.MusicService.MusicService;
import com.example.spotify_app.System.AccountManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSongFragment extends Fragment {

    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);
    RecyclerView rcvContainer;
    FrameLayout flContainerSmallMusicPlayer, flContainerMusicPlayer;
    LinearLayout lContainerSmallMusicPlayer;
    ArrayList<Song> datasource;
    SongAdapterRecycleView adapter;

    ItemClickListener clickListener;

    Drawable selectedColorViewItem;
    Drawable normalColorViewItem;

    int selectedIndex = -1;
    boolean isSpecial = false;
    Song song;

    Animation zoomIn;

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
        switch (actionMusic)
        {
            case MusicService.ACTION_UPDATE:
            {
                adapter.notifyDataSetChanged();
            }
        }
    }


    public SongAdapterRecycleView getAdapter() {
        return adapter;
    }

    public void setAdapter(SongAdapterRecycleView adapter) {
        this.adapter = adapter;
    }

    public void removeAllItemSong()
    {
        datasource.clear();
        adapter.notifyDataSetChanged();
    }

    public void removeItem(Song item)
    {
        datasource.remove(item);
        adapter.notifyDataSetChanged();
    }

    public void removeItem(int index)
    {
        datasource.remove(index);
        adapter.notifyDataSetChanged();
    }

    public ListSongFragment() {
    }


    public ListSongFragment(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public ListSongFragment(ArrayList<Song> datasource) {
        this.datasource = datasource;
    }

    public ListSongFragment(ArrayList<Song> datasource, ItemClickListener clickListener) {
        this.datasource = datasource;
        this.clickListener = clickListener;
    }

    public ListSongFragment(ArrayList<Song> datasource, boolean isSpecial) {
        this.datasource = datasource;
        this.isSpecial = isSpecial;
    }




    public static ListSongFragment newInstance(String param1, String param2) {
        ListSongFragment fragment = new ListSongFragment();
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
        return inflater.inflate(R.layout.fragment_list_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loadViews(view);
        loadEvents();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        song = MusicPlayer.Ins().getCurSong();
        if(MusicPlayer.Ins().getLstSong() != null)
        {
            if(MusicPlayer.Ins().getCurSong() != null)
            {
                selectedIndex = MusicPlayer.Ins().getCurSong().findIndexItem(MusicPlayer.Ins().getLstSong());
                setSelectedColorViewItem(selectedIndex);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    protected void loadViews(View view)
    {
        rcvContainer = view.findViewById(R.id.rcvContainer);
        flContainerMusicPlayer = view.findViewById(R.id.flContainerMusicPlayer);
        flContainerSmallMusicPlayer = view.findViewById(R.id.flContainerSmallMusicPlayer);
        lContainerSmallMusicPlayer = view.findViewById(R.id.lContainerSmallMusicPlayer);

        selectedColorViewItem  = getContext().getDrawable(R.color.foreground_musicplayer_10);
        normalColorViewItem  = getContext().getDrawable(R.color.transparent);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            datasource = MusicPlayer.Ins().getLstSong();
            int fromPossision = viewHolder.getAdapterPosition();
            int toPossision = target.getAdapterPosition();

            Collections.swap(datasource, fromPossision, toPossision);
            recyclerView.getAdapter().notifyItemMoved(fromPossision, toPossision);



            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


        }


        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if(actionState == ItemTouchHelper.ACTION_STATE_IDLE)
            {
                synchronized (datasource) {
                    // perform some operations on myObject
                    datasource.notify(); // notify a waiting thread
                }


                Song curSong = MusicPlayer.Ins().getCurSong();
                if(curSong == null)
                    return;
                ArrayList<Song> lstSong = MusicPlayer.Ins().getLstSong();
                int indexCurSong = curSong.findIndexItem(lstSong);
                MusicPlayer.Ins().setIndexCurSong(indexCurSong);
            }
        }
    };



    protected void loadEvents()
    {
        // Change color item when rcvContainer scrolling
        rcvContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.canScrollVertically(1)) {
                    if(selectedIndex != -1)
                        setSelectedColorViewItem(selectedIndex);
                }
            }
        });

        // Drag item
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcvContainer);
    }

    public void itemClick(int position)
    {
        if(datasource == null) {
            Toast.makeText(getContext(), "Có lỗi khi chuyển màn hình !", Toast.LENGTH_SHORT).show();
            return;
        }

        MusicPlayer.Ins().setLstSong(datasource);
        if(isSpecial)
        {
            Song item = datasource.get(position);
            song = item;
            MusicPlayer.Ins().setCurSong(item);


            String audioUrl = song.getSONG_URL();

            MediaPlayer newMedia = new MediaPlayer();
            MusicPlayer.Ins().setMedia(newMedia);
            try {
               newMedia.setDataSource(audioUrl);
               newMedia.prepareAsync();
               newMedia.setOnPreparedListener(mp -> sendNotificationMedia(MusicService.ACTION_PLAY));
            }
            catch (Exception e){}
        }
        else
        {
            Song item = datasource.get(position); // Bài nhạc cần phát
            ArrayList<Song> datasourceTemp = new ArrayList<>(); // Tạo ra một playlist tạm.
            for(Song songItem: datasource)                      // Đảm bảo các thuộc tính đặc biệt như BitMap,
            {                                                   // ImageBitmap = null
                datasourceTemp.add(new Song(songItem));
            }

            MusicPlayer.Ins().setCurSong(item); // Gán bài nhạc cần phát
            changeScreen(datasourceTemp);
        }
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

    protected void loadData()
    {
        if(!(datasource != null && datasource.size() != 0))
            Toast.makeText(getActivity(), "Không có dữ liệu !!!", Toast.LENGTH_SHORT).show();

        // Get list song from local memory or list song from available datasouce
        getData();

        if(MusicPlayer.Ins().getCurSong() != null)
        {
            if(MusicPlayer.Ins().getLstSong() != null && MusicPlayer.Ins().getLstSong().size() > 0)
            {
                Song temp = MusicPlayer.Ins().getCurSong();
                selectedIndex = temp.findIndexItem(MusicPlayer.Ins().getLstSong());
                setSelectedColorViewItem(selectedIndex);
            }
        }
    }


    public void callDataFavoriteSongs()
    {
        Call<FavoriteSongResponse> songCall = db.getListAllFavoriteSongs(AccountManager.Ins().getUser().getUserId());
        songCall.enqueue(new Callback<FavoriteSongResponse>() {
            @Override
            public void onResponse(Call<FavoriteSongResponse> call, Response<FavoriteSongResponse> response) {

                if(response.body() == null)
                {
                    Toast.makeText(getContext(), "Không thể lấy dữ liệu. Null !!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<FavoriteSong> dataFavoriteSongs = response.body().getData();
                Song.optimalData(datasource, dataFavoriteSongs);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FavoriteSongResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Không thể lấy dữ liệu. Không thể gọi api !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData()
    {
        setAdapter();
    }

    public void setAdapter()
    {
        if(MusicPlayer.Ins().getLstSong().size() == 0)
            MusicPlayer.Ins().setLstSong(datasource);
        adapter = new SongAdapterRecycleView(getActivity(), R.layout.itemsong, datasource
                // Open music player
                , (view, position, isLongClick) -> {
                    if(isLongClick)
                    {
                        // History fragment
                        if(clickListener != null)
                        {
                            clickListener.onClick(view, position, isLongClick);
                        }
                        return;
                    }
                    selectedIndex = position;
                    setSelectedColorViewItem(position);

                    itemClick(position);

                }
                ,
                // Open dialog menu
                new ItemSongClick(){
                    @Override
                    public void clicked(Object... data) {

                        // Show dialog menu
                        // Type of var data is Song
                        showDialog(data);
                    }
                }
                // Checkbox like
                , new ItemSongClick(){
                    @Override
                    public void clicked(Object... data) {


                        CheckBox sender = (CheckBox) data[0];
                        Song itemSong = (Song) data[1];
                        Boolean checked = (Boolean) data[2];

                        if(!AccountManager.Ins().isLogin())
                        {
                            Toast.makeText(getActivity(), "Chưa đăng nhập !", Toast.LENGTH_SHORT).show();
                            sender.setChecked(false);
                            return;
                        }
                        if(checked)
                            Toast.makeText(getActivity(), "Đã thêm vào bài hát yêu thích", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Đã xóa khỏi bài hát yêu thích", Toast.LENGTH_SHORT).show();
                        itemSong.setLiked(checked);

                        MusicPlayer.Ins().findSongIndex(itemSong).setLiked(checked);

                        int userID = AccountManager.Ins().getUser().getUserId();
                        int songID = itemSong.getSONG_ID();
                        addSongToPlayListFavortite(songID, userID);
                        sendNotificationMedia(MusicService.ACTION_UPDATE);
                    }
                });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rcvContainer.setLayoutManager(layoutManager);
        rcvContainer.setAdapter(adapter);

        // Optimal datasource if account is logining
        if(AccountManager.Ins().isLogin())
        {
            callDataFavoriteSongs();
        }

    }

    // Bottom sheet
    public void showDialog(Object... data)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogmenu);

        LinearLayout insertToLayout = dialog.findViewById(R.id.insertToPlaylist);
        insertToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!AccountManager.Ins().isLogin())
                {
                    Toast.makeText(getActivity(), "Chưa đăng nhập !", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialogInsertToPlaylist(data);
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void showDialogInsertToPlaylist(Object... data)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogmenu_inserttoplaylist);

        ListView lvContainerPlaylist = dialog.findViewById(R.id.lvContainerPlaylist);
        loadDataPlaylist(lvContainerPlaylist);
        lvContainerPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<Playlist> dataPlaylist = ((PlayListAdapterV2) lvContainerPlaylist.getAdapter()).getDatasource();

                int playlistID = dataPlaylist.get(position).getPlaylistId();
                int songID = ((Song) data[0]).getSONG_ID();

                // Add song to playlist
                addSongToPlayList(songID, playlistID);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void loadDataPlaylist(ListView target) {
        Call<PlaylistResponse> call = db.getPlaylistResource(AccountManager.Ins().getUser().getUserId());
        call.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                PlaylistResponse data = response.body();
                ArrayList<Playlist> playlists = data.getPlaylists();
                PlayListAdapterV2 adapterV2 = new PlayListAdapterV2(getActivity(), R.layout.itemplaylist, playlists);
                target.setAdapter(adapterV2);
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Đã có lỗi vui lòng thử lại sau", Toast.LENGTH_LONG).show();
            }
        });
    }

    void addSongToPlayList(int songID, int playListID)
    {
        JsonObject gsonobject = new JsonObject();

        JSONObject jsonObject=new JSONObject();
        try{
            try{

                jsonObject.put("playlistId", playListID);
                jsonObject.put("songId", songID);
            }
            catch (JSONException e)
            {

            }
            JsonParser jsonParser= new JsonParser();
            gsonobject=(JsonObject)jsonParser.parse(jsonObject.toString());
        }
        catch (Exception e){}
        Call<SongResponse> studentAddCall = db.insertSongsPlaylist(gsonobject);
        studentAddCall.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                Toast.makeText(getActivity(), "Đã thêm bài hát vào playlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Thao tác thất bại !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add song to playlist favorite
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

    public void setSelectedColorViewItem(int selectedIndex)
    {
        for (int i = 0; i < rcvContainer.getChildCount(); i++) {
            View viewItem = rcvContainer.getChildAt(i);

            int adapterPosision = rcvContainer.getChildAdapterPosition(viewItem);
            if(adapterPosision == selectedIndex)
            {
                viewItem.setBackground(selectedColorViewItem);
            }
            else
            {
                viewItem.setBackground(normalColorViewItem);
            }
        }
    }


    // Layout
    public void addFragmentMusicPlayer()
    {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(flContainerMusicPlayer.getId(),  new ListSongMusicPlayerFragment(datasource,this, true))
                .addToBackStack(ListSongMusicPlayerFragment.class.getSimpleName())
                .commit();
    }


    // Notification
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