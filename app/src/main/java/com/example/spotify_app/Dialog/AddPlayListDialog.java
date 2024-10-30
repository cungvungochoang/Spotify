package com.example.spotify_app.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.Model.PlaylistItemResponse;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlayListDialog extends DialogFragment {
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);


    Button btnCancelPlaylist, btnAddPlaylist;
    EditText edtPlaylistName;

    ArrayList<Playlist> playlists;

    public AddPlayListDialog(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_add_playlist, container, false);

        controls(view);
        loadEvents();

        return view;
    }

    private void controls(View view) {
        btnCancelPlaylist = view.findViewById(R.id.btn_cancel_playlist);
        btnAddPlaylist = view.findViewById(R.id.btn_add_playlist);
        edtPlaylistName = view.findViewById(R.id.edt_playlist_name);
    }

    private void loadEvents() {
        btnCancelPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playListName = edtPlaylistName.getText().toString();
                if(playListName.isEmpty()) {
                    Toast.makeText(view.getContext(), "Vui lòng nhập tên Playlist muốn tạo", Toast.LENGTH_LONG).show();
                }
                else {
                    Call<PlaylistItemResponse> call = db.createPlaylistResource(playListName, AccountManager.Ins().getUser().getUserId());
                    call.enqueue(new Callback<PlaylistItemResponse>() {
                        @Override
                        public void onResponse(Call<PlaylistItemResponse> call, Response<PlaylistItemResponse> response) {
                            playlists.add(response.body().getPlaylist());
                            Toast.makeText(view.getContext(), "Tạo mới playlist thành công", Toast.LENGTH_LONG).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<PlaylistItemResponse> call, Throwable t) {
                            Toast.makeText(view.getContext(), "Đã có lỗi vui lòng thử lại sau", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
