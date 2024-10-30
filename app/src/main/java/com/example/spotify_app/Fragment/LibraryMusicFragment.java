package com.example.spotify_app.Fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.TypeSong;
import com.example.spotify_app.MusicPlayer;
import com.example.spotify_app.R;

import java.util.ArrayList;

public class LibraryMusicFragment extends Fragment {

    String pattern = ".mp3";
    ArrayList<Song> datasource;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    FrameLayout flContainerLibrarySong;


    public LibraryMusicFragment() {

    }


    public static LibraryMusicFragment newInstance(String param1, String param2) {
        LibraryMusicFragment fragment = new LibraryMusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkPermisstions();
        loadViews(view);
        loadFragment();
    }


    protected void loadViews(View view)
    {
        flContainerLibrarySong = view.findViewById(R.id.flContainerLibrarySong);
    }

    private void loadFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerLibrarySong.getId(), new ListSongFragment(datasource))
                .commit();
    }

    public void getData()
    {
        datasource = new ArrayList<>();
        getListSongFromExternalMemory();
        if(MusicPlayer.Ins().getLstSong().size() == 0)
            MusicPlayer.Ins().setLstSong(datasource);
    }

    private void checkPermisstions() {

        String permission = "";

        // Android 10
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        }
        else
        {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED ) {
            getData();
        } else {
            // Open dialog request permission
            requestPermissions(new String[] {permission}, REQUEST_CODE_ASK_PERMISSIONS);
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public void addSongToDatasource(Song item)
    {
        if(!item.getSONG_URL().contains(pattern))
            return;
        datasource.add(item);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();

                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Từ chối cấp quyền. Một số tính năng có thể bị hạn chế"
                                    , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Method to retrieve song infos from external memory device
    public void getListSongFromExternalMemory() {
        // Query external audio resources
        ContentResolver musicResolver = getActivity().getContentResolver();

        Uri musicUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        // Iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {

            // Get columns
            int songPathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
//            int imageColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            do {
                String songPath = musicCursor.getString(songPathColumn);
                Long ID = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artistNames = musicCursor.getString(artistColumn);
                int albumID = musicCursor.getInt(albumIDColumn);
                String imagePath = "";
                Uri uri = null;
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                {
                    Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID+ "=?",
                            new String[] {String.valueOf(albumID)},
                            null);



                    if (cursor.moveToFirst()) {
                        int artColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                        imagePath = cursor.getString(artColumn);
                    }
                    cursor.close();
                }
                else
                {
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ID);
                }
                Song item = new Song(songPath, ID, title, artistNames, imagePath, TypeSong.Local);
                item.setSONG_URI(uri);
                addSongToDatasource(item);
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }





}