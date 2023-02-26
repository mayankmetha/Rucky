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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import com.mayank.rucky.utils.HID3;
import com.mayank.rucky.utils.Networks;

import org.apache.commons.io.FileUtils;
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
    private static final int PERMISSION_REQUEST_NOTIFICATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cmds = new ArrayList<>();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        config = new Config(this);
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_editor);

        if (config.getInitState() && config.getSec())
            getKey();

        try {
            TrustKit.initializeWithNetworkSecurityConfiguration(this);
        } catch (Exception ignored) {
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::getRoot, 1000);

        getReleaseSigningHash();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_NOTIFICATION);
            }
        }
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
                PackageInfo info;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.PackageInfoFlags.of(PackageManager.	GET_SIGNING_CERTIFICATES));
                } else {
                    info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
                }
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
            PackageInfo pInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.PackageInfoFlags.of(PackageManager.GET_ATTRIBUTIONS));
            } else {
                pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            }
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
                    if (Build.VERSION.SDK_INT >= minAndroidSDK) queue.add(getUpdateSignature());
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

    private void getRoot() {
        RootBeer rootBeer = new RootBeer(this);
        boolean root = rootBeer.isRooted() || rootBeer.isRootedWithoutBusyBoxCheck();
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
        if(!root) {
            new MaterialAlertDialogBuilder(EditorActivity.this)
                    .setTitle(getResources().getString(R.string.root_err))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                    .show();
        }
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
                        try {
                            if (!file.delete() && file.exists()) {
                                if (!file.getCanonicalFile().delete() && file.exists()) {
                                    getApplicationContext().deleteFile(file.getName());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
                        String fileNameString = FileUtils.filename(String.valueOf(fileName.getText()));
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
                    dos.writeBytes("cat /data/local/tmp/rucky.error\n");
                    dos.flush();
                    String disStr = dis.readLine();
                    if (disStr.contains("kernel=2")) {
                        dos.writeBytes("echo 0 > /data/local/tmp/rucky.error\n");
                        dos.flush();
                        new MaterialAlertDialogBuilder(EditorActivity.this)
                                .setTitle(getResources().getString(R.string.kernel_err))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                                .show();
                    }
                    if (disStr.contains("kernel=1")) {
                        dos.writeBytes("echo 0 > /data/local/tmp/rucky.error\n");
                        dos.flush();
                        new MaterialAlertDialogBuilder(EditorActivity.this)
                                .setTitle(getResources().getString(R.string.kernel_err))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()))
                                .show();
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
        HID3 exeScript = new HID3(jsonStr);
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