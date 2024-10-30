package com.example.spotify_app.Fragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Helper.RealPathUtil;
import com.example.spotify_app.Model.AuthResponse;
import com.example.spotify_app.Model.User;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.example.spotify_app.System.AccountPreferences;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);

    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE_PICK_IMAGE = 123;
    ImageButton btnBack;
    Button btnEditProfile, btnChooseImage;
    EditText edtFullname, edtEmail, edtRole;

    RoundedImageView imgProfile;

    Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controls(view);

        loadData();

        loadEvents();
    }

    private void controls(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        btnChooseImage = (Button) view.findViewById(R.id.btn_choose_image);
        btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);

        edtFullname = (EditText) view.findViewById(R.id.edt_fullname);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtRole = (EditText) view.findViewById(R.id.edt_role);

        imgProfile = (RoundedImageView) view.findViewById(R.id.img_profile);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang cập nhật...");
        progressDialog.setCancelable(false);
    }

    private void loadData() {
        User user = AccountManager.Ins().getUser();
        edtFullname.setText(user.getFullName());
        edtEmail.setText(user.getEmail());
        edtRole.setText(user.getRole());
    }

    private void loadEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new ProfileFragment());
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = edtFullname.getText().toString().trim();

                uploadImageToServer( AccountManager.Ins().getUser().getUserId(),fullname, mUri);
            }
        });
    }

    private void uploadImageToServer(int userId, String fullname ,Uri imageUri) {
        RequestBody requestBodyUsername = RequestBody.create(MediaType.parse("multipart/form-data"), fullname);

        if(imageUri == null) {
            progressDialog.show();

            Call<AuthResponse> call = db.editUserWithoutImageResource(userId, requestBodyUsername);
            call.enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    progressDialog.dismiss();
                    AuthResponse data = response.body();

                    AccountPreferences.removePreferences(getContext());
                    AccountPreferences.setPreferences(getContext(), data.getUser());
                    AccountPreferences.getPreferences(getContext());

                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Đã có lỗi vui lòng thử lại sau !!", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        String strRealPath = RealPathUtil.getRealPath(getContext(), imageUri);
        File file = new File(strRealPath);

        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multiPartBodyFile = MultipartBody.Part.createFormData("file", file.getName(), requestBodyFile);

        progressDialog.show();

        Call<AuthResponse> call = db.editUserResource(userId, requestBodyUsername, multiPartBodyFile);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();
                AuthResponse data = response.body();

                AccountPreferences.removePreferences(getContext());
                AccountPreferences.setPreferences(getContext(), data.getUser());
                AccountPreferences.getPreferences(getContext());

                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Đã có lỗi vui lòng thử lại sau !!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickRequestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            mUri = selectedImage;

            Picasso.get()
                    .load(selectedImage)
                    .fit()
                    .centerCrop()
                    .into(imgProfile);
        }
    }

    protected void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
            .replace(R.id.fm_container, fragment)
            .addToBackStack(fragment.getClass().getSimpleName())
            .commit();
    }
}