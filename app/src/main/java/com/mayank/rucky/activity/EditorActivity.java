package com.mayank.rucky.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appmattus.certificatetransparency.CTHostnameVerifierBuilder;
import com.datatheorem.android.trustkit.TrustKit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.kimchangyoun.rootbeerFresh.RootBeer;
import com.mayank.rucky.R;
import com.mayank.rucky.models.HidModel;
import com.mayank.rucky.service.SocketHeartbeatService;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.HID;
import com.mayank.rucky.utils.HID2;
import com.mayank.rucky.utils.Networks;

import org.apache.commons.io.IOUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class EditorActivity extends AppCompatActivity {

    private boolean root = false;
    private boolean hidPresent = true;

    public static Config config;
    public NotificationCompat.Builder updateNotify;
    private static MasterKey keyAlias;
    Process p;
    private static DataOutputStream dos;
    private static BufferedReader dis;
    public static ArrayList<String> cmds;
    public static NotificationManager serviceNotificationManager;
    public static NotificationManager updateNotificationManager;
    private static boolean noHidFile = false;
    public static ArrayList<HidModel> keymap;

    public static int distro = 0;
    public static String getSHA512;
    public static boolean nightly;
    RequestQueue queue;
    static double currentVersion;
    static double newVersion;
    static long currentNightly;
    static long newNightly;
    public static int minAndroidSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cmds = new ArrayList<>();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        config = new Config(this);
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_editor);

        if (config.getInitState() && config.getSec())
            getKey();

        if (savedInstanceState == null) {
            requestPermissions();
        }

        try {
            TrustKit.initializeWithNetworkSecurityConfiguration(this);
        } catch (Exception ignored) {
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> supportedFiles(true), 1000);

        getReleaseSigningHash();
        setNotificationChannel();

        if (config.getUpdateFlag())
            updateInit();

        openSettings();

        keymap = new ArrayList<>();
        keymapListRefresh(this);

        ide();
    }

    @Override
    public void onBackPressed() {
        if(config.getConfigFSOption())
            disableConfigFSHID();
        new MaterialAlertDialogBuilder(EditorActivity.this)
                .setTitle(getResources().getString(R.string.exit_dialog))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btn_exit), (dialog, which) -> {
                    if(config.getHIDMode() == 1)
                        stopNetworkSocketService();
                    finishAndRemoveTask();
                    System.exit(0);
                })
                .setNeutralButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                .setNegativeButton(getResources().getString(R.string.btn_back), (dialog, which) -> super.onBackPressed())
                .show();
    }

    void getKey() {
        try {
            keyAlias = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private void getReleaseSigningHash() {
        String NetHunter = "x2j+O+TND/jjH0ryjO/2ROPpjCvHoHK/XnjrgdAHJfM=";
        String GitHubRelease = "0Xv/I6xP6Q1wKbIqCgXi4CafhKZtOZLOR575TiqN93s=";
        String GitHubNightly = "eEk+yGxeE5dXukQ4HiGYS4eEyTAcoC6Mfm1OX/1l12c=";
        String debug = "6E9Beocz6JC8xJXYO7Wu3nZQwZW1bKb1M004n/xR2BE=";
        ArrayList<String> hashList = new ArrayList<>();
        nightly = false;
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                if (info.signingInfo.hasMultipleSigners()) {
                    for (Signature signature: info.signingInfo.getApkContentsSigners()) {
                        MessageDigest md = MessageDigest.getInstance("SHA256");
                        md.update(signature.toByteArray());
                        hashList.add(new String(Base64.encode(md.digest(), 0)));
                    }
                } else {
                    for (Signature signature: info.signingInfo.getSigningCertificateHistory()) {
                        MessageDigest md = MessageDigest.getInstance("SHA256");
                        md.update(signature.toByteArray());
                        hashList.add(new String(Base64.encode(md.digest(), 0)));
                    }
                }
            } else {
                for (Signature signature : PackageInfoCompat.getSignatures(getPackageManager(),getPackageName())) {
                    MessageDigest md = MessageDigest.getInstance("SHA256");
                    md.update(signature.toByteArray());
                    hashList.add(new String(Base64.encode(md.digest(), 0)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < hashList.size(); i++) {
            if(hashList.get(i).trim().equals(NetHunter)) {
                distro = R.string.releaseNetHunter;
                nightly = false;
                config.setUpdateFlag(false);
            } else if(hashList.get(i).trim().equals(GitHubRelease)) {
                distro = R.string.releaseGitHub;
                nightly = false;
                config.setUpdateFlag(true);
            } else if(hashList.get(i).trim().equals(GitHubNightly)) {
                distro = R.string.releaseGitHubNightly;
                nightly = true;
                config.setUpdateFlag(true);
            } else if(hashList.get(i).trim().equals(debug)) {
                distro = R.string.releaseTest;
                nightly = true;
                config.setUpdateFlag(true);
            } else {
                distro = R.string.releaseOthers;
                nightly = false;
                config.setUpdateFlag(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        updateScreen();
    }

    private void setNotificationChannel() {
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Constants.SCHANNEL_ID, Constants.SCHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null,null);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            serviceNotificationManager = getSystemService(NotificationManager.class);
            assert serviceNotificationManager != null;
            serviceNotificationManager.createNotificationChannel(notificationChannel);
        } else {
            serviceNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        updateServiceStatus();
    }

    private void startNetworkSocketService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
        } else {
            getApplicationContext().startService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
        }
        updateNotification();
    }

    private void stopNetworkSocketService() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
    }

    private void updateConfigActivityUSBStatus() {
        if (config.getUSBStatus()) {
            config.setStatusTextRes(R.string.config_status_usb_on);
            config.setStatusImageRes(R.drawable.ic_usb);
        } else {
            config.setStatusTextRes(R.string.config_status_usb_off);
            config.setStatusImageRes(R.drawable.ic_usb_off);
        }
    }

    private void updateConfigActivityNetSocketStatus() {
        if (config.getNetworkStatus()) {
            config.setStatusTextRes(R.string.config_status_net_on);
            config.setStatusImageRes(R.drawable.ic_net);
        } else {
            config.setStatusTextRes(R.string.config_status_net_off);
            config.setStatusImageRes(R.drawable.ic_net_off);
        }
    }

    private void updateServiceStatus() {
        if (config.getHIDMode() == 0) {
            stopNetworkSocketService();
            updateConfigActivityUSBStatus();
        } else if (config.getHIDMode() == 1) {
            startNetworkSocketService();
            updateConfigActivityNetSocketStatus();
            updateNotification();
        }
    }

    private void updateNotification() {
        if (config.getHIDMode() == 1) {
            serviceNotificationManager.notify(1, new NotificationCompat.Builder(getApplicationContext(), Constants.SCHANNEL_ID)
                    .setContentTitle(getApplicationContext().getString(config.getStatusTextRes()))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setOngoing(true)
                    .setGroup(Constants.PACKAGE_NAME)
                    .setGroupSummary(true)
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                            PendingIntent.FLAG_IMMUTABLE))
                    .setAutoCancel(false).build());
        }
    }

    private void updateInit() {
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null,null);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            updateNotificationManager = getSystemService(NotificationManager.class);
            assert updateNotificationManager != null;
            updateNotificationManager.createNotificationChannel(notificationChannel);
        } else {
            updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        queue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                if (connection instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                    CTHostnameVerifierBuilder builder = new CTHostnameVerifierBuilder(httpsConnection.getHostnameVerifier());
                    for (String host : Constants.hostnames) {
                        builder.includeHost(host);
                    }
                    httpsConnection.setHostnameVerifier(builder.build());
                    httpsConnection.setSSLSocketFactory(TrustKit.getInstance().getSSLSocketFactory(url.getHost()));
                }
                return connection;
            }
        });

        if (config.getUpdateFlag()) {
            preCheckAppUpdate();
        }
    }

    private void preCheckAppUpdate() {
        getCurrentAppVersion();
        getNewAppVersion();
    }

    private void getCurrentAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
            currentNightly = PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getNewAppVersion() {
        Networks n = new Networks();
        if (n.isNetworkPresent(this)) {
            Runnable runnable = () -> {
                try {
                    URL url = new URL(Constants.GITHUB_RELEASE_LATEST);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.getInputStream();
                    String str = conn.getHeaderField( "Location" );
                    if (str.isEmpty()) {
                        newVersion = 0;
                    } else {
                        newVersion = Double.parseDouble(str.substring(str.lastIndexOf('/') + 1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nightly || newVersion > currentVersion) {
                    queue.add(getUpdateMetadata());
                    runOnUiThread(this::updateScreen);
                }
            };
            new Thread(runnable).start();
        }
    }

    private StringRequest getUpdateMetadata() {
        String url = nightly ? Constants.CFG_NIGHTLY : Constants.CFG_BASE_RELEASE + newVersion + Constants.CFG_FILE_RELEASE;
        return new StringRequest(Request.Method.GET, url,
                response -> {
                    String[] lines = response.split("\\r?\\n");
                    try {
                        minAndroidSDK = lines[0] != null ? Integer.parseInt(lines[0]) : 0;
                        newNightly = lines[1] != null ? Long.parseLong(lines[1]) : currentNightly;
                    } catch (Exception e) {
                        minAndroidSDK = 0;
                        newNightly = currentNightly;
                    }
                    if (Build.VERSION.SDK_INT != 0 && Build.VERSION.SDK_INT >= minAndroidSDK) queue.add(getUpdateSignature());
                }, error -> {
            minAndroidSDK = 0;
            newNightly = currentNightly;
        }
        );
    }

    private StringRequest getUpdateSignature() {
        String url = nightly ? Constants.SIG_NIGHTLY : Constants.SIG_BASE_RELEASE + newVersion + Constants.SIG_FILE_RELEASE;
        return new StringRequest(Request.Method.GET, url, response -> getSHA512 = response.trim(), error -> getSHA512 = "");
    }

    private void updateScreen() {
        Button updateBtn = findViewById(R.id.update_button);
        updateBtn.setFilterTouchesWhenObscured(true);
        if (config.getUpdateFlag()) {
            if (nightly && newNightly > currentNightly) {
                Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
                updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_IMMUTABLE);
                updateNotify = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle(String.format("%s%s (%s)",getString(R.string.update_new),newVersion,newNightly))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(notifyPendingIntent)
                        .setGroup(Constants.PACKAGE_NAME)
                        .setGroupSummary(true)
                        .setAutoCancel(false);
                updateNotificationManager.notify(0, updateNotify.build());
                updateBtn.setEnabled(true);
            } else if(newVersion > currentVersion) {
                Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
                updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_IMMUTABLE);
                updateNotify = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle(String.format("%s%s",getString(R.string.update_new),newVersion))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(notifyPendingIntent)
                        .setGroup(Constants.PACKAGE_NAME)
                        .setGroupSummary(true)
                        .setAutoCancel(false);
                updateNotificationManager.notify(0, updateNotify.build());
                updateBtn.setEnabled(true);
            } else {
                updateBtn.setEnabled(false);
            }
        } else {
            updateBtn.setEnabled(false);
        }

        updateBtn.setOnClickListener(view -> {
            Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
            startActivity(updateIntent);
        });
    }

    private void openSettings() {
        Button settingBtn = findViewById(R.id.setting_button);
        settingBtn.setFilterTouchesWhenObscured(true);
        settingBtn.setOnClickListener(view -> {
            Intent settingIntent = new Intent(EditorActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        });
    }

    private void requestPermissions() {
        ArrayList<String> permission = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!permission.isEmpty()) {
            String[] per = new String[permission.size()];
            ActivityCompat.requestPermissions(this, permission.toArray(per), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length > 0 && permissions.length == grantResults.length) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
            }
        }
    }

    private void getRoot() {
        RootBeer rootBeer = new RootBeer(this);
        root = rootBeer.isRooted() || rootBeer.isRootedWithoutBusyBoxCheck();
        String disStr;
        ArrayList<String> killList = new ArrayList<>();
        try {
            Process ps = Runtime.getRuntime().exec("ps -u $(whoami)");
            BufferedReader psDis = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            while((disStr = psDis.readLine()) != null) {
                disStr = disStr.replaceAll(" +"," ");
                if (disStr.endsWith("su"))
                    killList.add(disStr.split(" ")[1]);
            }
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if (dos != null) {
                dos.writeBytes("id\n");
                dos.flush();
                disStr = dis.readLine();
                if (disStr.contains("uid=0")) {
                    root = true;
                }
            }
            for (int i=0; i< killList.size(); i++) {
                assert dos != null;
                dos.writeBytes("kill -9 "+killList.get(0)+"\n");
                dos.flush();
            }
        } catch (Exception ignored) {
        }
    }

    private boolean checkKernel() {
        boolean canKernelSupportConfigFS = false;
        try {
            dos.writeBytes("if [ \"$(printf '%s\\n%s\\n' \"3.19\" \"$(uname -r)\" | sort -V | head -n1)\" = \"3.19\" ]; then\n");
            dos.writeBytes("    echo 0\n");
            dos.writeBytes("fi\n");
            dos.flush();
            if (Integer.parseInt(dis.readLine()) == 0)
                canKernelSupportConfigFS = true;
        } catch (Exception ignored) {
        }
        return canKernelSupportConfigFS;
    }

    private boolean checkConfigFS() {
        boolean canSupportConfigFS = false;
        try {
            dos.writeBytes("if [ -d /config/usb_gadget -o -f /sys/devices/virtual/android_usb/android0/enable ]; then\n");
            dos.writeBytes("    echo 0\n");
            dos.writeBytes("fi\n");
            dos.flush();
            if (Integer.parseInt(dis.readLine()) == 0)
                canSupportConfigFS = true;
        } catch (Exception ignored) {
        }
        return canSupportConfigFS;
    }

    private int createConfigFSKeyboard() {
        int status = 1;
        try {
            dos.writeBytes("if [ ! -d /config/usb_gadget/g1/functions/hid.0 ]; then\n");
            dos.writeBytes("    if ! mkdir /config/usb_gadget/g1/functions/hid.0 > /dev/null 2>&1; then\n");
            dos.writeBytes("        echo 1\n");
            dos.writeBytes("    else\n");
            dos.writeBytes("        echo \"1\" > /config/usb_gadget/g1/functions/hid.0/protocol\n");
            dos.writeBytes("        echo \"1\" > /config/usb_gadget/g1/functions/hid.0/subclass\n");
            dos.writeBytes("        echo \"8\" > /config/usb_gadget/g1/functions/hid.0/report_length\n");
            dos.writeBytes("        echo -n -e '\\x05\\x01\\x09\\x06\\xa1\\x01\\x05\\x07\\x19\\xe0\\x29\\xe7\\x15\\x00\\x25\\x01\\x75\\x01\\x95\\x08\\x81\\x02\\x95\\x01\\x75\\x08\\x81\\x03\\x95\\x05\\x75\\x01\\x05\\x08\\x19\\x01\\x29\\x05\\x91\\x02\\x95\\x01\\x75\\x03\\x91\\x03\\x95\\x06\\x75\\x08\\x15\\x00\\x25\\x65\\x05\\x07\\x19\\x00\\x29\\x65\\x81\\x00\\xc0' > config/usb_gadget/g1/functions/hid.0/report_desc\n");
            dos.writeBytes("        echo 0\n");
            dos.writeBytes("    fi\n");
            dos.writeBytes("else\n");
            dos.writeBytes("    echo 0\n");
            dos.writeBytes("fi\n");
            dos.flush();
            status = Integer.parseInt(dis.readLine());
        } catch(Exception ignored) {
        }
        return status;
    }

    private int createConfigFSMouse() {
        int status = 1;
        try {
            dos.writeBytes("if [ ! -d /config/usb_gadget/g1/functions/hid.1 ]; then\n");
            dos.writeBytes("    if ! mkdir /config/usb_gadget/g1/functions/hid.1 > /dev/null 2>&1; then\n");
            dos.writeBytes("        echo 1\n");
            dos.writeBytes("    else\n");
            dos.writeBytes("        echo \"1\" > /config/usb_gadget/g1/functions/hid.1/protocol\n");
            dos.writeBytes("        echo \"2\" > /config/usb_gadget/g1/functions/hid.1/subclass\n");
            dos.writeBytes("        echo \"4\" > /config/usb_gadget/g1/functions/hid.1/report_length\n");
            dos.writeBytes("            echo -n -e '\\x05\\x01\\x09\\x02\\xa1\\x01\\x09\\x01\\xa1\\x00\\x05\\x09\\x19\\x01\\x29\\x05\\x15\\x00\\x25\\x01\\x95\\x05\\x75\\x01\\x81\\x02\\x95\\x01\\x75\\x03\\x81\\x01\\x05\\x01\\x09\\x30\\x09\\x31\\x09\\x38\\x15\\x81\\x25\\x7f\\x75\\x08\\x95\\x03\\x81\\x06\\xc0\\xc0' > config/usb_gadget/g1/functions/hid.1/report_desc\n");
            dos.writeBytes("        echo 0\n");
            dos.writeBytes("    fi\n");
            dos.writeBytes("else\n");
            dos.writeBytes("    echo 0\n");
            dos.writeBytes("fi\n");
            dos.flush();
            status = Integer.parseInt(dis.readLine());
        } catch(Exception ignored) {
        }
        return status;
    }

    private boolean enableConfigFSHID() {
        ArrayList<String> f = new ArrayList<>();
        try {
            dos.writeBytes("getprop sys.usb.config\n");
            dos.flush();
            String usbConfig = dis.readLine();
            dos.writeBytes("find /config/usb_gadget/g1/configs/b.1/ -type l | wc -l\n");
            dos.flush();
            int symlinkCount = Integer.parseInt(dis.readLine());
            dos.writeBytes("readlink -f $(find /config/usb_gadget/g1/configs/b.1/ -type l)\n");
            dos.flush();
            boolean hid0 = false, hid1 = false;
            for(int i = 0; i < symlinkCount; i++) {
                String line = dis.readLine();
                if(line.contains("hid.0")) hid0 = true;
                if(line.contains("hid.1")) hid1 = true;
                f.add(line);
            }
            if (!hid0 || !hid1) hidPresent = false;
            if (!hid0) f.add("/config/usb_gadget/g1/functions/hid.0");
            if (!hid1) f.add("/config/usb_gadget/g1/functions/hid.1");
            dos.writeBytes("echo \"none\" > /config/usb_gadget/g1/UDC\n");
            dos.writeBytes("stop adbd\n");
            dos.writeBytes("setprop sys.usb.ffs.ready 0\n");
            dos.writeBytes("rm $(find /config/usb_gadget/g1/configs/b.1 -type l) 2>/dev/null\n");
            dos.flush();
            if(!usbConfig.contains("hid")) {
                hidPresent = false;
                usbConfig += ",hid";
                dos.writeBytes("setprop sys.usb.config " + usbConfig + "\n");
                dos.flush();
            }
            for(int i = 0; i < f.size(); i++) {
                dos.writeBytes("ln -s "+f.get(i)+" /config/usb_gadget/g1/configs/b.1/f"+i+"\n");
            }
            if(usbConfig.contains("adb")) {
                dos.writeBytes("start adbd\n");
                dos.writeBytes("setprop sys.usb.ffs.ready 1\n");
            }
            dos.writeBytes("echo $(getprop sys.usb.controller) > /config/usb_gadget/g1/UDC\n");
            dos.writeBytes("sleep 1\n");
            dos.writeBytes("echo 1\n");
            dos.flush();
            if(dis.readLine().contains("1"))
                return (checkHIDFiles());
        } catch(Exception ignored) {
        }
        return false;
    }

    private void disableConfigFSHID() {
        if (!hidPresent) {
            ArrayList<String> f = new ArrayList<>();
            try {
                dos.writeBytes("find /config/usb_gadget/g1/configs/b.1/ -type l | wc -l\n");
                dos.flush();
                int symlinkCount = Integer.parseInt(dis.readLine());
                dos.writeBytes("readlink -f $(find /config/usb_gadget/g1/configs/b.1/ -type l)\n");
                dos.flush();
                for (int i = 0; i < symlinkCount; i++) f.add(dis.readLine());
                dos.writeBytes("getprop sys.usb.config\n");
                dos.flush();
                String usbConfig = dis.readLine();
                dos.writeBytes("echo \"none\" > /config/usb_gadget/g1/UDC\n");
                dos.writeBytes("stop adbd\n");
                dos.writeBytes("setprop sys.usb.ffs.ready 0\n");
                dos.writeBytes("rm $(find /config/usb_gadget/g1/configs/b.1 -type l) 2>/dev/null\n");
                dos.flush();
                if (!usbConfig.contains("hid")) {
                    hidPresent = false;
                    usbConfig += ",hid";
                    dos.writeBytes("setprop sys.usb.config " + usbConfig + "\n");
                    dos.flush();
                }
                for (int i = 0; i < f.size(); i++) {
                    String line = f.get(i);
                    if (line.contains("hid.0") || line.contains("hid.1")) continue;
                    dos.writeBytes("ln -s " + line + " /config/usb_gadget/g1/configs/b.1/f" + i + "\n");
                }
                if (usbConfig.contains("adb")) {
                    dos.writeBytes("start adbd\n");
                    dos.writeBytes("setprop sys.usb.ffs.ready 1\n");
                }
                if (usbConfig.contains("hid")) {
                    usbConfig = usbConfig.replace("hid", "").replace(",,", ",");
                    if (usbConfig.endsWith(","))
                        usbConfig = usbConfig.substring(0, usbConfig.length() - 1);
                    if (usbConfig.startsWith(","))
                        usbConfig = usbConfig.substring(1);
                    dos.writeBytes("setprop sys.usb.config " + usbConfig + "\n");
                    dos.flush();
                }
                dos.writeBytes("echo $(getprop sys.usb.controller) > /config/usb_gadget/g1/UDC\n");
                dos.writeBytes("sleep 1\n");
                dos.writeBytes("echo 1\n");
                dos.flush();
            } catch (Exception ignored) {
            }
        }
    }

    private boolean checkHIDFiles() {
        String pathDev = "/dev";
        File file1 = new File(pathDev,"hidg0");
        File file2 = new File(pathDev,"hidg1");
        if(!file1.exists() && !file2.exists()) {
            return false;
        } else {
            try {
                dos.writeBytes("chmod 666 /dev/hidg0\n");
                dos.writeBytes("chmod 666 /dev/hidg1\n");
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean supportedFiles(boolean quiet) {
        boolean canExe = false;
        getRoot();
        if (root) {
            if(checkHIDFiles())
                canExe = true;
            else if(config.getConfigFSOption()) {
                if(checkKernel() && checkConfigFS()) {
                    if(createConfigFSKeyboard() == 0 && createConfigFSMouse() == 0) {
                        canExe = enableConfigFSHID();
                    } else {
                        if(!quiet) {
                            new MaterialAlertDialogBuilder(EditorActivity.this)
                                    .setTitle(getResources().getString(R.string.kernel_err))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                                    .show();
                        }
                    }
                }
            } else {
                if(!quiet) {
                    new MaterialAlertDialogBuilder(EditorActivity.this)
                            .setTitle(getResources().getString(R.string.kernel_err))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                            .show();
                }
            }
        } else {
            if(!quiet) {
                new MaterialAlertDialogBuilder(EditorActivity.this)
                        .setTitle(getResources().getString(R.string.root_err))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                        .show();
            }
        }
        return canExe;
    }

    private void ide() {
        Button DelBtn = findViewById(R.id.delBtn);
        DelBtn.setFilterTouchesWhenObscured(true);
        Button SaveBtn = findViewById(R.id.svBtb);
        SaveBtn.setFilterTouchesWhenObscured(true);
        Button LoadBtn = findViewById(R.id.ldBtn);
        LoadBtn.setFilterTouchesWhenObscured(true);
        Button ExeBtn = findViewById(R.id.exBtn);
        ExeBtn.setFilterTouchesWhenObscured(true);
        Button CfgBtn = findViewById(R.id.cfgBtn);
        CfgBtn.setFilterTouchesWhenObscured(true);

        DelBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!config.getSec()) {
                for (File file : tmp) {
                    if (file.getPath().endsWith(".txt")) {
                        files.add(file);
                    }
                }
            } else {
                files.addAll(Arrays.asList(tmp));
            }
            CharSequence[] fileName = new CharSequence[files.size()];
            for (int i = 0; i < files.size(); i++) {
                fileName[i] = files.get(i).getName();
            }
            new MaterialAlertDialogBuilder(EditorActivity.this)
                    .setTitle(getResources().getString(R.string.title_delete_file))
                    .setCancelable(false)
                    .setItems(fileName, (dialog, i) -> {
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                        file.delete();
                        if(file.exists()){
                            try {
                                file.getCanonicalFile().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(file.exists()){
                                getApplicationContext().deleteFile(file.getName());
                            }
                        }
                        Snackbar.make(findViewById(android.R.id.content), fileName[i]+" "+getResources().getString(R.string.file_deleted), Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show();
        });

        SaveBtn.setOnClickListener(view -> {
            LayoutInflater saveLI = LayoutInflater.from(this);
            final View saveView = saveLI.inflate(R.layout.editor_save, null);
            final EditText fileName = saveView.findViewById(R.id.save_filename);
            new MaterialAlertDialogBuilder(EditorActivity.this)
                    .setTitle(getResources().getString(R.string.title_save_file))
                    .setView(saveView)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
                        EditText scripts = findViewById(R.id.code);
                        File file;
                        String fileNameString = fileName.getText().toString().replaceAll("[\\\\/.]+","");
                        if (fileNameString.isEmpty()) {
                            fileNameString = String.valueOf(new Date().getTime());
                        }
                        if (config.getSec()) {
                            file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileNameString+".enc");
                        } else {
                            file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileNameString+".txt");
                        }
                        String content = scripts.getText().toString();
                        FileOutputStream fOutputStream;
                        OutputStream outputStream;
                        try {
                            if (config.getSec()) {
                                EncryptedFile encryptedFile = new EncryptedFile.Builder(this, file,
                                        keyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();
                                fOutputStream = encryptedFile.openFileOutput();
                            } else {
                                fOutputStream = new FileOutputStream(file);
                            }
                            outputStream = new BufferedOutputStream(fOutputStream);
                            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                            outputStream.close();
                            fOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Snackbar.make(findViewById(android.R.id.content), file.getName() + " "+getResources().getString(R.string.file_saved),Snackbar.LENGTH_SHORT)
                                .show();
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show();
        });

        LoadBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!config.getSec()) {
                for (File file : tmp) {
                    if (file.getPath().endsWith(".txt")) {
                        files.add(file);
                    }
                }
            } else {
                files.addAll(Arrays.asList(tmp));
            }
            CharSequence[] fileName = new CharSequence[files.size()];
            for (int i = 0; i < files.size(); i++) {
                fileName[i] = files.get(i).getName();
            }
            new MaterialAlertDialogBuilder(EditorActivity.this)
                    .setTitle(getResources().getString(R.string.title_open_file))
                    .setCancelable(false)
                    .setItems(fileName, (dialog, i) -> {
                        EditText scripts = findViewById(R.id.code);
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                        FileInputStream fInputStream;
                        InputStream inputStream;
                        StringWriter writer;
                        try {
                            if (config.getSec() && file.getPath().endsWith(".enc")) {
                                EncryptedFile encryptedFile = new EncryptedFile.Builder(this, file,
                                        keyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();
                                fInputStream = encryptedFile.openFileInput();
                            } else {
                                fInputStream = new FileInputStream(file);
                            }
                            inputStream = new BufferedInputStream(fInputStream);
                            writer = new StringWriter();
                            IOUtil.copy(inputStream, writer, "UTF-8");
                            scripts.setText(writer.toString());
                            inputStream.close();
                            fInputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show();
        });

        ExeBtn.setOnClickListener(view -> {
            EditText scripts = findViewById(R.id.code);
            launchAttack(config.getHIDMode(),config.getHIDLanguage(),scripts.getText().toString());
        });

        CfgBtn.setOnClickListener(view -> startActivity(new Intent(this, ConfigActivity.class)));
    }

    void launchAttack(int mode, int language, String scripts) {
        // usb mode
        if(mode == 0) {
            if(!supportedFiles(false))
                return;
            if(config.getUSB() && !config.getUSBStatus()) {
                if(config.getHIDCustomise())
                    fetchCommands(jsonRead(new File(this.getExternalFilesDir("keymap"),config.getHIDFileSelected())),scripts);
                else
                    fetchCommands(language, scripts);
            } else {
                try {
                    if(config.getHIDCustomise())
                        fetchCommands(jsonRead(new File(this.getExternalFilesDir("keymap"),config.getHIDFileSelected())),scripts);
                    else
                        fetchCommands(language, scripts);
                    for(int i = 0; i < cmds.size(); i++) {
                        dos.writeBytes(cmds.get(i));
                        dos.flush();
                    }
                    cmds.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // network socket mode
        if (mode == 1) {
            if (config.getNet() && !config.getNetworkStatus()) {
                if(config.getHIDCustomise())
                    fetchCommands(jsonRead(new File(this.getExternalFilesDir("keymap"),config.getHIDFileSelected())),scripts);
                else
                    fetchCommands(language, scripts);
            } else {
                if(config.getHIDCustomise())
                    fetchCommands(jsonRead(new File(this.getExternalFilesDir("keymap"),config.getHIDFileSelected())),scripts);
                else
                    fetchCommands(language, scripts);
                new Thread(() -> {
                    String ip = config.getNetworkAddress().substring(0,config.getNetworkAddress().indexOf(":"));
                    int port = Integer.parseInt(config.getNetworkAddress().substring(config.getNetworkAddress().indexOf(":")+1));
                    try {
                        Socket socket = new Socket(ip, port);
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        for (int i = 0; i < cmds.size(); i++) {
                            if (socket.isConnected()) {
                                out.print(cmds.get(i));
                            }
                        }
                        out.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cmds.clear();
                }).start();
            }
        }
    }

    void fetchCommands(int language, String scripts) {
        HID exeScript = new HID(language);
        exeScript.parse(scripts);
        cmds.clear();
        cmds.addAll(exeScript.getCmd());
    }

    void fetchCommands(String jsonStr, String scripts) {
        HID2 exeScript = new HID2(jsonStr);
        exeScript.parse(scripts);
        cmds.clear();
        cmds.addAll(exeScript.getCmd());
    }

    public static void keymapListRefresh(Context context) {
        keymap.clear();
        getOnDeviceRepo(context);
        getKeymapRepo(context);
    }

    static String jsonRead(File file) {
        FileInputStream fInputStream;
        InputStream inputStream;
        StringWriter writer = new StringWriter();
        try {
            fInputStream = new FileInputStream(file);
            inputStream = new BufferedInputStream(fInputStream);
            IOUtil.copy(inputStream, writer, "UTF-8");
            inputStream.close();
            fInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    static int getFileRevision(String jsonStr) {
        int revision = 0;
        if (!jsonStr.isEmpty()) {
            try {
                JSONObject tmp = new JSONObject(jsonStr);
                revision = tmp.getInt("version");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return revision;
    }

    static void getOnDeviceRepo(Context context) {
        final File[] tmp = Objects.requireNonNull(context.getExternalFilesDir("keymap")).listFiles();
        if (tmp == null || tmp.length == 0) {
            noHidFile = true;
        } else {
            noHidFile = false;
            for (File file : tmp) {
                if (file.getPath().endsWith(".json")) {
                    try {
                        keymap.add(new HidModel(
                                file.getName().replace(".json", "").replace("_"," ").toUpperCase(),
                                getFileRevision(jsonRead(file)),
                                file.getName(),
                                "",
                                Constants.HID_OFFLINE
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static void getKeymapRepo(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, Constants.KEYBOARDMAP_URL, null,
            response -> {
                for(int i=0; i < response.length(); i++) {
                    boolean added = false;
                    try {
                        HidModel tmpHidModel = new HidModel(
                            response.getJSONObject(i).getString("name"),
                            Integer.parseInt(response.getJSONObject(i).getString("revision")),
                            response.getJSONObject(i).getString("filename"),
                            response.getJSONObject(i).getString("url"),
                            Constants.HID_DOWNLOAD
                        );
                        for(int j = 0; j < keymap.size(); j++) {
                            if(keymap.get(j).modelExist(tmpHidModel)) {
                                added = true;
                                HidModel updHidModel = keymap.get(j);
                                if(updHidModel.getHidModelRevision() < tmpHidModel.getHidModelRevision()) {
                                    updHidModel.setHidModelState(Constants.HID_UPDATE);
                                }
                                updHidModel.setHidModelUrl(tmpHidModel.getHidModelUrl());
                                keymap.set(j, updHidModel);
                            }
                        }
                        if (!added)
                            keymap.add(tmpHidModel);
                        if(noHidFile) {
                            noHidFile = false;
                            JsonObjectRequest reqDl = new JsonObjectRequest(Request.Method.GET, keymap.get(0).getHidModelUrl(), null,
                                responseDl -> {
                                    try {
                                        File file = new File(context.getExternalFilesDir("keymap"),keymap.get(0).getHidModelFilename());
                                        FileOutputStream fOutputStream = new FileOutputStream(file);
                                        OutputStream outputStream = new BufferedOutputStream(fOutputStream);
                                        outputStream.write(responseDl.toString().getBytes(StandardCharsets.UTF_8));
                                        outputStream.close();
                                        fOutputStream.close();
                                        keymap.get(0).setHidModelState(Constants.HID_OFFLINE);
                                        keymap.get(0).setHidModelRevision(responseDl.getInt("version"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, Throwable::printStackTrace);
                            requestQueue.add(reqDl);
                            config.setHIDFileSelected(keymap.get(0).getHidModelFilename());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, Throwable::printStackTrace);
        requestQueue.add(req);
    }

}