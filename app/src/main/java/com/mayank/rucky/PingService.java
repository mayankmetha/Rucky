package com.mayank.rucky;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class PingService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkPiStatus();
        startForeground(99,MainActivity.cNotify(this));
    }

    public boolean ping() {
        if (MainActivity.piIp.equals(""))
            return false;
        String cmd = "ping -c 1 "+MainActivity.piIp;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void checkPiStatus() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean piConnected;
                piConnected = ping();
                Intent i = new Intent();
                if (MainActivity.piConnected != piConnected) {
                    if(piConnected)
                        i.setAction("piConnected");
                    else
                        i.setAction("piDisconnected");
                    sendBroadcast(i);
                }
            }
        };
        timer.schedule(task, 0, 30000);
    }

}
