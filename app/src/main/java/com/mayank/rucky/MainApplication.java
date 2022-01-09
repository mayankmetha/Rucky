package com.mayank.rucky;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this, R.style.Theme_Rucky_Monet);
    }

}
