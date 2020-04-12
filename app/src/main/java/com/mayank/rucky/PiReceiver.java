package com.mayank.rucky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (action.equals("piConnected"))
            MainActivity.piConnected = true;
        if (action.equals("piDisconnected"))
            MainActivity.piConnected = false;
        MainActivity.connectionNotify(context);
        if(MainActivity.piConnected && !MainActivity.cmds.isEmpty()) {
            Thread piThread = new Thread(new wifiSocket(MainActivity.cmds, MainActivity.piIp));
            piThread.start();
            MainActivity.cmds.clear();
        }
    }
}
