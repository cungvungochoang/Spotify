package com.example.spotify_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // Call api
    RetrofitClient retrofitClient = new RetrofitClient();
    APIInterface db = retrofitClient.getRetrofit().create(APIInterface.class);


    TextView tvLogin;

    ImageButton btnBack;
    Button btnRegister;

    EditText edtFullname, edtEmail, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        controls();

        loadEvents();
    }
    private void controls() {
        tvLogin = (TextView) findViewById(R.id.tv_login);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnRegister = (Button) findViewById(R.id.btn_register);

        edtFullname = (EditText) findViewById(R.id.edt_fullname);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm_password);
    }

    private void loadEvents() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(view.getContext(), LoginActivity.class);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate
                if(Validate.checkIsEmtyList(new EditText[] {edtFullname, edtEmail, edtPassword, edtConfirmPassword})) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ các trường !!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Validate.checkConfirmPassword(edtPassword.getText().toString(), edtConfirmPassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Nhập lại mật khẩu không đúng !!", Toast.LENGTH_LONG).show();
                    return;
                }

                String fullname = edtFullname.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                Call<AuthResponse> call = db.registerResource(fullname, email, password);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công !!", Toast.LENGTH_LONG).show();
                        changeScreen(RegisterActivity.this, LoginActivity.class);
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại !!", Toast.LENGTH_LONG).show();
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