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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class NetSocketReceiver extends BroadcastReceiver {

    Config config;
    int status = 0;
    int oldStatus = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        config = new Config(context);
        String action = intent.getAction();
        assert action != null;
        if (action.equals(Constants.NET_SOCKET_CONNECTED)) {
            config.setNetworkStatus(true);
            oldStatus = status;
            status = 1;
        } else if (action.equals(Constants.NET_SOCKET_DISCONNECTED)) {
            config.setNetworkStatus(false);
            oldStatus = status;
            status = 0;
        }
        if (oldStatus != status)
            updateServiceStatus(context);
        if(config.getNetworkStatus() && !EditorActivity.cmds.isEmpty()) {
            new Thread(() -> {
                String ip = config.getNetworkAddress().substring(0,config.getNetworkAddress().indexOf(":"));
                int port = Integer.parseInt(config.getNetworkAddress().substring(config.getNetworkAddress().indexOf(":")+1));
                try {
                    Socket socket = new Socket(ip, port);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    for (int i = 0; i < EditorActivity.cmds.size(); i++) {
                        if (socket.isConnected()) {
                            out.print(EditorActivity.cmds.get(i));
                        }
                    }
                    out.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EditorActivity.cmds.clear();
            }).start();
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
}
