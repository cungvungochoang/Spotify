package com.example.spotify_app.BaseClass;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class MyRunnable implements Runnable {

    InterfaceBackgroundRun interfaceBackgroundRun;
    Activity context;

    public MyRunnable() {
    }

    public MyRunnable(Fragment fragment, InterfaceBackgroundRun interfaceBackgroundRun) {
        this.context = (Activity) fragment.getActivity();
        this.interfaceBackgroundRun = interfaceBackgroundRun;
    }

    public MyRunnable(Activity context, InterfaceBackgroundRun interfaceBackgroundRun) {
        this.context = context;
        this.interfaceBackgroundRun = interfaceBackgroundRun;
    }

    @Override
    public void run() {
        interfaceBackgroundRun.doInBackground();
    }

    public void excute()
    {
        try
        {
            context.runOnUiThread(this);
        }
        catch (Exception e){
            Log.e("My Runnable", e.getMessage() != null ? e.getMessage() : "");
        }
    }

}
