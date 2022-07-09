package com.mayank.rucky.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mayank.rucky.R;
import com.mayank.rucky.activity.WelcomeActivity;

public class AppOwnerReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.e("Device Admin: ", "Enabled");
    }
    @Override
    public String onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        return "Admin disable requested";
    }
    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        Log.e("Device Admin: ", "Disabled");
    }
    @Override
    public void onProfileProvisioningComplete(Context context, @NonNull Intent intent) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context.getApplicationContext(), AppOwnerReceiver.class);
        manager.setProfileName(componentName, context.getString(R.string.app_name));
        manager.setProfileEnabled(componentName);
        manager.enableSystemApp(componentName, context.getPackageName());
        Intent launch = new Intent(context, WelcomeActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }
}
