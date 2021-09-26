package com.mayank.rucky.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mayank.rucky.activity.EditorActivity;
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
            EditorActivity.updateServiceStatus(context);
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
}
