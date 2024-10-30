package com.example.spotify_app.Helper;

import android.widget.EditText;

import java.util.ArrayList;

public class Validate {
    public static Boolean checkIsEmty(EditText edt) {
        return edt.getText().toString().trim().isEmpty();
    }

    public static Boolean checkIsEmtyList(EditText[] editTextList) {
        for(EditText edt : editTextList) {
            if(checkIsEmty(edt)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean checkConfirmPassword(String password, String confirmPassword) {
        return password.trim().equals(confirmPassword.trim());
    }
}
