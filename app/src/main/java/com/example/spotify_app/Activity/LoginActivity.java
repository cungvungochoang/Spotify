package com.example.spotify_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotify_app.Api.APIInterface;
import com.example.spotify_app.Api.RetrofitClient;
import com.example.spotify_app.Helper.Validate;
import com.example.spotify_app.Model.AuthResponse;
import com.example.spotify_app.R;
import com.example.spotify_app.System.AccountManager;
import com.example.spotify_app.System.AccountPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);


    TextView tvRegister;
    EditText edtEmail, edtPassword;

    ImageButton btnBack;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        controls();

        loadEvents();
    }

    private void controls() {
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);

        tvRegister = (TextView) findViewById(R.id.tv_register);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void loadEvents() {
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(view.getContext(), RegisterActivity.class);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate.checkIsEmtyList(new EditText[] {edtEmail, edtPassword})) {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ các trường !!", Toast.LENGTH_LONG).show();
                    return;
                }

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                Call<AuthResponse> call = db.loginResource(email, password);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        AuthResponse data = response.body();
                        if (data == null) {
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ !!", Toast.LENGTH_LONG).show();
                        } else {
                            AccountPreferences.setPreferences(view.getContext(), data.getUser());
                            AccountPreferences.getPreferences(view.getContext());

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công !!", Toast.LENGTH_LONG).show();
                            changeScreen(LoginActivity.this, MainActivity.class);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Đã có lỗi vui lòng thử lại !!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void changeScreen(Context context, Class clss) {
        Intent intent = new Intent(context, clss);
        startActivity(intent);
    }
}