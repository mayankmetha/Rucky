package com.mayank.rucky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;

import java.util.Objects;

public class USBReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert action != null;
            if (action.equals("android.hardware.usb.action.USB_STATE"))
                MainActivity.usbConnected = Objects.requireNonNull(intent.getExtras()).getBoolean("connected");
        } else {
            assert action != null;
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                MainActivity.usbConnected = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            }
        }
    }
}
