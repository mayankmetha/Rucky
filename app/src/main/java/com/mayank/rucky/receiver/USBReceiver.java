package com.mayank.rucky.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mayank.rucky.R;
import com.mayank.rucky.activity.EditorActivity;
import com.mayank.rucky.activity.WelcomeActivity;
import com.mayank.rucky.service.SocketHeartbeatService;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;

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
            if (action.equals(Constants.USB_ACTION)) {
                config.setUSBStatus(Objects.requireNonNull(intent.getExtras()).getBoolean(Constants.USB_ACTION_STATE));
                updateServiceStatus(context);
            }
        } else {
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                config.setUSBStatus(true);
                updateServiceStatus(context);
            }
            if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                config.setUSBStatus(false);
                updateServiceStatus(context);
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

    private void updateServiceStatus(Context context) {
        if (config.getHIDMode() == 0) {
            stopNetworkSocketService(context);
            if (config.getNetworkStatus()) {
                config.setStatusTextRes(R.string.config_status_net_on);
                config.setStatusImageRes(R.drawable.ic_net);
            } else {
                config.setStatusTextRes(R.string.config_status_net_off);
                config.setStatusImageRes(R.drawable.ic_net_off);
            }
        } else if (config.getHIDMode() == 1) {
            startNetworkSocketService(context);
            if (config.getUSBStatus()) {
                config.setStatusTextRes(R.string.config_status_usb_on);
                config.setStatusImageRes(R.drawable.ic_usb);
            } else {
                config.setStatusTextRes(R.string.config_status_usb_off);
                config.setStatusImageRes(R.drawable.ic_usb_off);
            }
            updateNotification(context);
        }
    }

    private void startNetworkSocketService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, SocketHeartbeatService.class));
        } else {
            context.startService(new Intent(context, SocketHeartbeatService.class));
        }
        updateNotification(context);
    }

    private void stopNetworkSocketService(Context context) {
        context.stopService(new Intent(context, SocketHeartbeatService.class));
    }

    private void updateNotification(Context context) {
        EditorActivity.serviceNotificationManager.notify(1, new NotificationCompat.Builder(context, Constants.SCHANNEL_ID)
                .setContentTitle(context.getString(config.getStatusTextRes()))
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        PendingIntent.FLAG_IMMUTABLE))
                .setAutoCancel(false).build());
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
