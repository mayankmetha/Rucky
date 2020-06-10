package com.mayank.rucky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


public class USBReceiver extends BroadcastReceiver {

    Process p;
    private static DataOutputStream dos;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (action.equals("android.hardware.usb.action.USB_STATE"))
                MainActivity.usbConnected = Objects.requireNonNull(intent.getExtras()).getBoolean("connected");
        } else {
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                MainActivity.usbConnected = true;
            }
            if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                MainActivity.usbConnected = false;
            }
        }
        MainActivity.connectionNotify(context);
        if(MainActivity.usbConnected && getRoot() && !MainActivity.cmds.isEmpty()) {
            try {
                for (int i = 0; i < MainActivity.cmds.size(); i++) {
                    dos.writeBytes(MainActivity.cmds.get(i));
                    dos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainActivity.cmds.clear();
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
