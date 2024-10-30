package com.example.spotify_app.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.example.spotify_app.R;

public class CheckBoxTriStates extends androidx.appcompat.widget.AppCompatCheckBox {
    static public final int UNKNOW = -1;
    static public final int UNCHECKED = 0;
    static public final int CHECKED = 1;
    private int state;

    public CheckBoxTriStates(Context context) {
        super(context);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        state = UNKNOW;
        this.setAlpha(0.6f);
        updateBtn();

        setOnCheckedChangeListener(new OnCheckedChangeListener() {

            // checkbox status is changed from uncheck to checked.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (state) {
                    default:
                    case UNKNOW:
                        state = UNCHECKED;
                        break;
                    case UNCHECKED:
                        state = CHECKED;
                        break;
                    case CHECKED:
                        state = UNKNOW;
                        break;
                }
                updateBtn();
            }
        });
    }

    private void updateBtn() {
        int btnDrawable = R.drawable.ic_checkbox_indeterminate;
        switch (state) {
            default:
            case UNKNOW:
                btnDrawable = R.drawable.ic_checkbox_indeterminate;
                break;
            case UNCHECKED:
                btnDrawable = R.drawable.ic_checkbox_unchecked;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_checkbox_checked;
                break;
        }

        setButtonDrawable(btnDrawable);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateBtn();
    }
}