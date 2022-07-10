package com.mayank.rucky.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.mayank.rucky.utils.Config;

public class AppOwnerReceiver extends DeviceAdminReceiver {
    Config config;

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        config = new Config(context);
        config.setDeviceAdmin(true);
    }
    @Override
    public String onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        return "Admin disable requested";
    }
    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        config = new Config(context);
        config.setDeviceAdmin(false);
    }
}
