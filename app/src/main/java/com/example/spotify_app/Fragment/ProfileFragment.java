package com.example.spotify_app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotify_app.Activity.LoginActivity;
import com.example.spotify_app.Adapter.CustomSongAdapter;
import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Model.FavoriteResponse;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.User;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.example.spotify_app.System.AccountPreferences;
import com.example.spotify_app.System.ViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    FrameLayout flContainerListFavoriteSong;
    RoundedImageView imgProfile;
    TextView tvProfileName, tvProfileEmail;
    ImageButton btnProfilePopup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controls(view);

        loadData(view);


        loadEvents();
    }
    private void controls(View view) {
        tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = (TextView) view.findViewById(R.id.tv_profile_email);
        imgProfile = (RoundedImageView) view.findViewById(R.id.img_profile);

        btnProfilePopup = (ImageButton) view.findViewById(R.id.btn_profile_popup);
        flContainerListFavoriteSong = view.findViewById(R.id.flContainerListFavoriteSongSearch);
    }

    void loadFragment(ArrayList<Song> data)
    {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(flContainerListFavoriteSong.getId(), new ListSongFragment(data))
                .commit();
    }

    private void loadEvents() {
        btnProfilePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), btnProfilePopup);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_profile, popupMenu.getMenu());

                // xử lý sự kiện item click
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.item_logout:
                                AccountPreferences.removePreferences(getContext());
                                changeScreen(getContext(), LoginActivity.class);
                                ViewHolder.Ins().getBottomNavigationView().setSelectedItemId(R.id.home_tab);
                                return true;
                            case R.id.item_edit_profile:
                                switchFragment(new EditProfileFragment());
                                return true;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void loadData(View view) {
        loadProfile();

        Call<FavoriteResponse> call = db.getFavoriteListSongResource(AccountManager.Ins().getUser().getUserId());
        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                FavoriteResponse data = response.body();
                // for new release song
                ArrayList<Song> favoriteList = data.getSongs();
                loadFragment(favoriteList);
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProfile() {
        User user = AccountManager.Ins().getUser();

        Picasso.get()
                .load(user.getImage())
                .fit()
                .centerCrop()
                .into(imgProfile);

        tvProfileName.setText(user.getFullName());
        tvProfileEmail.setText(user.getEmail());
    }

    private void changeScreen(Context context, Class clss) {
        Intent intent = new Intent(context, clss);
        startActivity(intent);
    }

    protected void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fm_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}