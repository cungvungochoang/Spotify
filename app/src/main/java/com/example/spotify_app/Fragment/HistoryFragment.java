package com.example.spotify_app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.spotify_app.BaseClass.ItemClickListener;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    ImageButton imgBtnDeleteAll;
    ArrayList<Song> selectedSongs;
    FrameLayout flContainerListSong;
    ListSongFragment listSongFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        addControls(view);
        addEvents();

        selectedSongs = getSelectedSongsFromSharedPreferences();


        loadFragment();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedSongs = getSelectedSongsFromSharedPreferences();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedSongs = getSelectedSongsFromSharedPreferences();
    }

    private void addControls(View view){
        imgBtnDeleteAll = view.findViewById(R.id.imgBtnDeleteAll);
        flContainerListSong = view.findViewById(R.id.flContainerListSong);
    }

    private void addEvents()
    {
        imgBtnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), imgBtnDeleteAll);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.deleteAllMenuItem) {
                            listSongFragment.removeAllItemSong();

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("song_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void loadFragment()
    {
        reserveList();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerListSong.getId(), listSongFragment = new ListSongFragment(selectedSongs, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        new AlertDialog.Builder(getContext()).setTitle("Bạn có muốn xóa?").setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listSongFragment.removeItem(position);
                                selectedSongs.remove(position);

                                saveSelectedSongToSharedPreferences(selectedSongs);
                            }
                        }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                }))
                .commit();
    }

    public void reserveList()
    {
        ArrayList<Song> temp = new ArrayList<>();
        for (int i = selectedSongs.size() - 1; i >= 0; i--)
        {
            temp.add(selectedSongs.get(i));
        }
        selectedSongs = temp;
    }


    private ArrayList<Song> getSelectedSongsFromSharedPreferences() {
        ArrayList<Song> selectedSongs = new ArrayList<>();
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
                    selectedSongs.add(song);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return selectedSongs;
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
}