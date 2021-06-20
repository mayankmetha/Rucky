package com.mayank.rucky.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mayank.rucky.activity.EditorActivity;
import com.mayank.rucky.utils.Config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


public class USBReceiver extends BroadcastReceiver {

    Process p;
    private static DataOutputStream dos;
    Config config;

    @Override
    public void onReceive(final Context context, Intent intent) {
        config = new Config(context);
        String action = intent.getAction();
        assert action != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (action.equals("android.hardware.usb.action.USB_STATE")) {
                config.setUSBStatus(Objects.requireNonNull(intent.getExtras()).getBoolean("connected"));
                EditorActivity.updateServiceStatus(context);
            }
        } else {
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                config.setUSBStatus(true);
                EditorActivity.updateServiceStatus(context);
            }
            if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                config.setUSBStatus(false);
                EditorActivity.updateServiceStatus(context);
            }
        }
        if(config.getUSBStatus() && getRoot() && !EditorActivity.cmds.isEmpty()) {
            try {
                for (int i = 0; i < EditorActivity.cmds.size(); i++) {
                    dos.writeBytes(EditorActivity.cmds.get(i));
                    dos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            EditorActivity.cmds.clear();
        }
    }

    private boolean getRoot() {
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            BufferedReader dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if(dos != null) {
                dos.writeBytes("id\n");
                dos.flush();
                String rootCheck = dis.readLine();
                if(rootCheck.contains("uid=0")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
