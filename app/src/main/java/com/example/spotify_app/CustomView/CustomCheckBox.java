package com.example.spotify_app.CustomView;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.spotify_app.R;


public class CustomCheckBox extends androidx.appcompat.widget.AppCompatImageView {

    static public final int UNKNOW = -1;
    static public final int UNCHECKED = 0;
    static public final int CHECKED = 1;
    private int state = 0;
    private int precentlyState;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    public CustomCheckBox(Context context) {
        super(context);
        init();
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    private void init() {
        state = UNCHECKED;
        updateView();

//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "123", Toast.LENGTH_SHORT).show();
//                updateView();
//                switch (state) {
//                    default:
//                    case UNKNOW:
//                        state = UNCHECKED;
//                        break;
//                    case UNCHECKED:
//                        state = CHECKED;
//                        break;
//                    case CHECKED:
//                        state = UNCHECKED;
//                        break;
//                }
//                runAnimation();
//            }
//        });
    }

    private void updateView() {
        int btnDrawable = R.drawable.ic_checkbox_indeterminate;
        switch (state) {
            default:
            case UNKNOW:
            {
                if(precentlyState == UNCHECKED)
                {
//                    Toast.makeText(getContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                    // Unchecked to unknow
                    btnDrawable = R.drawable.avd_check_tounknow;
                }
                else
                {
//                    Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                    // Checked to unknow
                    btnDrawable = R.drawable.avd_check_tounknow;
                }
                break;
            }
            case UNCHECKED:
                btnDrawable = R.drawable.avd_check_touncheck;
                break;
            case CHECKED:
                btnDrawable = R.drawable.avd_uncheck_tocheck;
                break;
        }
        setImageDrawable(AppCompatResources.getDrawable(getContext(), btnDrawable));
    }

    private void runAnimation() {
        Drawable drawable = getDrawable();

        if(drawable instanceof AnimatedVectorDrawableCompat)
        {
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }
        else if(drawable instanceof AnimatedVectorDrawable)
        {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateView();
        runAnimation();
    }

    public void setState(int state, boolean savePrecentlyState) {

        if(savePrecentlyState)
            this.precentlyState = this.state;
        this.state = state;
        updateView();
        runAnimation();
    }

    public void savePrecentlyState()
    {
        this.precentlyState = this.state;
    }


}
