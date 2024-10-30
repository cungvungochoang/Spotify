package com.example.spotify_app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.spotify_app.Fragment.HistoryFragment;
import com.example.spotify_app.Fragment.HomeFragment;
import com.example.spotify_app.Fragment.LibraryMusicFragment;
import com.example.spotify_app.Fragment.MinimizedMusicPlayerFargment;
import com.example.spotify_app.Fragment.PlaylistFragment;
import com.example.spotify_app.Fragment.ProfileFragment;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.example.spotify_app.System.AccountPreferences;
import com.example.spotify_app.System.ViewHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {


    FrameLayout flContainer, flContainerMusicPlayer;
    LinearLayout lContainerSmallMusicPlayer;

    public MinimizedMusicPlayerFargment minimizedMusicPlayerFargment = new MinimizedMusicPlayerFargment(this);

    BottomNavigationView navigation;
    FrameLayout flContainerSmallMusicPlayer;
    HistoryFragment historyFragment= new HistoryFragment();
    // Animations
    Animation zoomIn;

    BottomNavigationView bottomNavigationView;

    Boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SplashScreen
        SplashScreen.installSplashScreen(this);

        // SplashScreen animation
        startSplashScreenAnimation();

        setContentView(R.layout.activity_main);

        // lấy thông tin User từ Prefers lưu vào AccountManager
        AccountPreferences.getPreferences(MainActivity.this);

        if(!checkOS())
            return;

        // Request permissions
        requestStoragePermission();
        requestReadAudioPermission();

        getSupportActionBar().hide();

        controls();

        loadEvents();
        // Load default Fragment
        switchFragment(new HomeFragment(), true);
    }

    @Override
    public void onBackPressed() {
        showMusicPlayer(false);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            isBack = true;
            getSupportFragmentManager().popBackStack();
            int index = getSupportFragmentManager().getBackStackEntryCount() - 2;
            String fragmentName;

            if(index <= 0)
                fragmentName = "HomeFragment";
            else
                fragmentName = getSupportFragmentManager().getBackStackEntryAt(index).getName();

            switch (fragmentName) {
                case "LibraryMusicFragment":
                    bottomNavigationView.setSelectedItemId(R.id.library_tab);
                    break;
                case "PlaylistFragment":
                    bottomNavigationView.setSelectedItemId(R.id.playlist_tab);
                    break;
                case "HistoryFragment":
                    bottomNavigationView.setSelectedItemId(R.id.history_tab);
                    break;
                case "ProfileFragment":
                    bottomNavigationView.setSelectedItemId(R.id.profile_tab);
                    break;
                default:
                    bottomNavigationView.setSelectedItemId(R.id.home_tab);
                    break;
            }
            isBack = false;
        } else {
            super.onBackPressed();
        }
    }

    public void startSplashScreenAnimation()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {

                final ObjectAnimator opacityAnimation = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.ALPHA,
                        1,
                        0
                );
                opacityAnimation.setInterpolator(new BounceInterpolator());
                opacityAnimation.setDuration(200L);


                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        0f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(200L);


                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            splashScreenView.remove();
                        }
                    }
                });

                // Run your animation.
                slideUp.start();
                opacityAnimation.start();
            });
        }
    }

    public boolean checkOS()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            return false;
        }
        return true;
    }

    public void controls() {
        bottomNavigationView = findViewById(R.id.navigation_view);
        ViewHolder.Ins().setBottomNavigationView(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home_tab);
        flContainer = findViewById(R.id.fm_container);


        flContainerSmallMusicPlayer = findViewById(R.id.flContainerSmallMusicPlayer);
        ViewHolder.Ins().setFlContainerSmallMusicPlayer(flContainerSmallMusicPlayer);
        ViewHolder.Ins().hideFlContainerSmallMusicPlayer();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainerSmallMusicPlayer, minimizedMusicPlayerFargment)
                .commit();


    }

    public void loadEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_tab:
                        if(!isBack) {
                            switchFragment(ViewHolder.Ins().getHomeFragment(), false);
                        }
                        return  true;
                    case R.id.library_tab:
                        {
                            switchFragment(ViewHolder.Ins().getLibraryMusicFragment(), false);
                            return true;
                        }
                    case R.id.playlist_tab:
                        if(!isBack && AccountManager.Ins().isLogin()) {
                            switchFragment(ViewHolder.Ins().getPlaylistFragment(), false);
                            return true;
                        }
                        else {
                            changeScreen(MainActivity.this, LoginActivity.class);
                            return false;
                        }
                    case R.id.history_tab:
                        if(!isBack && AccountManager.Ins().isLogin()) {
                            switchFragment(ViewHolder.Ins().getHistoryFragment(), false);
                            return true;
                        }
                        else {
                            changeScreen(MainActivity.this, LoginActivity.class);
                            return false;
                        }

                    case R.id.profile_tab:
                        if(!isBack && AccountManager.Ins().isLogin()) {
                            switchFragment(ViewHolder.Ins().getProfileFragment(), false);
                            return true;
                        }
                        else {
                            changeScreen(MainActivity.this, LoginActivity.class);
                            return false;
                        }
                }

                return false;
            }
        });

    }

    protected void switchFragment(Fragment fragment, Boolean isFistLoad) {
        if(isFistLoad == true) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fm_container, fragment)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fm_container, fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private void changeScreen(Context context, Class clss) {
        if(clss.getSimpleName().equals(LoginActivity.class.getSimpleName()))
        {
            if(AccountManager.Ins().isLogin())
                return;
        }
        Intent intent = new Intent(context, clss);
        startActivity(intent);
    }

    public void showMusicPlayer(boolean isShow)
    {
        if(flContainerMusicPlayer == null || lContainerSmallMusicPlayer == null)
            return;
        if(isShow)
        {
            flContainerMusicPlayer.setVisibility(View.VISIBLE);
            lContainerSmallMusicPlayer.setVisibility(View.GONE);
            zoomIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in);
            flContainerMusicPlayer.startAnimation(zoomIn);

        }
        else
        {
            flContainerMusicPlayer.setVisibility(View.GONE);
            lContainerSmallMusicPlayer.setVisibility(View.VISIBLE);
            minimizedMusicPlayerFargment.loadData();
        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionsGranted = true;
                for (boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    // Thực hiện công việc cần thiết sau khi đã được cấp quyền
                } else {
                    // Xử lý khi người dùng từ chối cấp quyền
                }
            });

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Quyền đã được cấp, thực hiện công việc cần thiết
        } else {
            // Yêu cầu quyền từ người dùng

            requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_AUDIO});
        }
    }

    public void requestReadAudioPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    123);
            return;
        }
    }
}