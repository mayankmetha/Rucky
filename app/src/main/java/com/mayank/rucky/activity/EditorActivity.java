package com.mayank.rucky.activity;

import static android.util.Base64.decode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.mayank.rucky.R;
import com.mayank.rucky.models.HidModel;
import com.mayank.rucky.service.SocketHeartbeatService;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.HID;
import com.mayank.rucky.utils.HID2;
import com.mayank.rucky.utils.Networks;

import org.apache.commons.io.IOUtils;
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
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class EditorActivity extends AppCompatActivity {

    private Boolean root = false;

    public static Config config;
    public NotificationCompat.Builder updateNotify;
    static SecretKey key;
    static AlgorithmParameterSpec iv;
    Process p;
    private static DataOutputStream dos;
    public static ArrayList<String> cmds;
    public static NotificationManager serviceNotificationManager;
    public static NotificationManager updateNotificationManager;
    @SuppressLint("StaticFieldLeak")
    public static NotificationCompat.Builder sNotify;
    private static boolean noHidFile = false;
    public static ArrayList<HidModel> keymap;

    public static int distro = 0;
    public static String getSHA512;
    public static boolean nightly;
    RequestQueue queue;
    static double currentVersion;
    static double newVersion;
    static int currentNightly;
    static int newNightly;
    public static int minAndroidSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cmds = new ArrayList<>();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        config = new Config(this);
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(Constants.themeList[config.getAccentTheme()]);
        setContentView(R.layout.activity_editor);

        if (config.getInitState() && config.getSec())
            biometric();

        if (savedInstanceState == null) {
            requestPermissions();
            getRoot();
        }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle(getResources().getString(R.string.exit_dialog));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.btn_exit), (dialog, which) -> {
            if(config.getHIDMode() == 1)
                stopNetworkSocketService(this);
            finishAndRemoveTask();
        });
        builder.setNeutralButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
        builder.setNegativeButton(getResources().getString(R.string.btn_back), (dialog, which) -> super.onBackPressed());
        AlertDialog exitDialog = builder.create();
        Objects.requireNonNull(exitDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        exitDialog.show();
    }

    public void biometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finishAffinity();
                System.exit(0);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                getKey();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                finishAffinity();
                System.exit(0);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getResources().getString(R.string.unlock))
                .setSubtitle(getResources().getString(R.string.auth))
                .setNegativeButtonText(getResources().getString(R.string.btn_cancel))
                .setConfirmationRequired(false)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    void getKey() {
        try{
            KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(Constants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE,null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String k64 = config.getKeyStore1();
            byte[] tmp = decode(k64, Base64.DEFAULT);
            key = new SecretKeySpec(cipher.doFinal(tmp),"AES");
            String k642 = config.getKeyStore2();
            byte[] tmp2 = decode(k642,Base64.DEFAULT);
            iv = new IvParameterSpec(cipher.doFinal(tmp2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReleaseSigningHash() {
        String NetHunter = "x2j+O+TND/jjH0ryjO/2ROPpjCvHoHK/XnjrgdAHJfM=";
        String GitHubRelease = "0Xv/I6xP6Q1wKbIqCgXi4CafhKZtOZLOR575TiqN93s=";
        String GitHubNightly = "eEk+yGxeE5dXukQ4HiGYS4eEyTAcoC6Mfm1OX/1l12c=";
        String debug = "im5KgLli2rx4iEvMVXotXGpfiR1/eqXEwBO2YQ6uP70=";
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
                @SuppressLint("PackageManagerGetSignatures")
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
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
                config.setUpdateFlag(false);
            } else if(hashList.get(i).trim().equals(GitHubRelease)) {
                distro = R.string.releaseGitHub;
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
                config.setUpdateFlag(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(Constants.themeList[config.getAccentTheme()]);
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

        updateServiceStatus(this);
    }

    public static void startNetworkSocketService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, SocketHeartbeatService.class));
        } else {
            context.startService(new Intent(context, SocketHeartbeatService.class));
        }
        updateNotification(context);
    }

    public static void stopNetworkSocketService(Context context) {
        context.stopService(new Intent(context, SocketHeartbeatService.class));
    }

    public static boolean configActivityCreated() {
        return ConfigActivity.statusText != null && ConfigActivity.statusImage != null;
    }

    public static void updateConfigActivityUSBStatus(Config config, Context context) {
        if (configActivityCreated()) {
            if (config.getUSBStatus()) {
                ConfigActivity.statusText.setText(R.string.config_status_usb_on);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_usb));
            } else {
                ConfigActivity.statusText.setText(R.string.config_status_usb_off);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_usb_off));
            }
        }
    }

    public static void updateConfigActivityNetSocketStatus(Config config, Context context) {
        if (configActivityCreated()) {
            if (config.getNetworkStatus()) {
                ConfigActivity.statusText.setText(R.string.config_status_net_on);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_net));
            } else {
                ConfigActivity.statusText.setText(R.string.config_status_net_off);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_net_off));
            }
        }
    }

    public static void updateServiceStatus(Context context) {
        Config config = new Config(context);
        if (config.getHIDMode() == 0) {
            stopNetworkSocketService(context);
            updateConfigActivityUSBStatus(config, context);
        } else if (config.getHIDMode() == 1) {
            startNetworkSocketService(context);
            updateConfigActivityNetSocketStatus(config, context);
            updateNotification(context);
        }
    }

    public static void buildNotification(Context context, String text) {
        Intent sIntent = new Intent(context, SplashActivity.class);
        sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent sPendingIntent;
        sPendingIntent = PendingIntent.getActivity(context, 0, sIntent, PendingIntent.FLAG_IMMUTABLE);
        sNotify = new NotificationCompat.Builder(context, Constants.SCHANNEL_ID)
                .setContentTitle(text)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setContentIntent(sPendingIntent)
                .setAutoCancel(false);
        serviceNotificationManager.notify(1, sNotify.build());
    }

    public static void updateNotification(Context context) {
        Config c = new Config(context);
        if (c.getHIDMode() == 1) {
            if (c.getNetworkStatus())
                buildNotification(context,context.getString(R.string.config_status_net_on));
            else
                buildNotification(context,context.getString(R.string.config_status_net_off));
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
        queue = Volley.newRequestQueue(this);
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
            currentNightly = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getNewAppVersion() {
        Networks n = new Networks();
        if (n.isNetworkPresent(this)) {
            Runnable runnable = () -> {
                try {
                    URL url = new URL("https://github.com/mayankmetha/Rucky/releases/latest");
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
                    updateScreen();
                }
            };
            new Thread(runnable).start();
        }
    }

    private StringRequest getUpdateMetadata() {
        String url = nightly ? "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.cfg" : "https://github.com/mayankmetha/Rucky/releases/download/" + newVersion + "/rucky.cfg";
        return new StringRequest(Request.Method.GET, url,
                response -> {
                    String[] lines = response.split("\\r?\\n");
                    try {
                        minAndroidSDK = lines[0] != null ? Integer.parseInt(lines[0]) : 0;
                        newNightly = lines[1] != null ? Integer.parseInt(lines[1]) : currentNightly;
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
        String url = nightly ? "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.sha512" : "https://github.com/mayankmetha/Rucky/releases/download/" + newVersion + "/rucky.sha512";
        return new StringRequest(Request.Method.GET, url, response -> getSHA512 = response.trim(), error -> getSHA512 = "");
    }

    private void updateScreen() {
        Button updateBtn = findViewById(R.id.update_button);
        if (config.getUpdateFlag()) {
            if (nightly && newNightly > currentNightly) {
                Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
                updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                updateNotify = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle(getResources().getString(R.string.update_new)+" Nightly Version: "+ newVersion+" ("+newNightly+")")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(false);
                updateNotificationManager.notify(0, updateNotify.build());
                updateBtn.setVisibility(View.VISIBLE);
            } else if(newVersion > currentVersion) {
                Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
                updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                updateNotify = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle(getResources().getString(R.string.update_new) + " Version: " + newVersion)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(false);
                updateNotificationManager.notify(0, updateNotify.build());
                updateBtn.setVisibility(View.VISIBLE);
            } else {
                updateBtn.setVisibility(View.GONE);
            }
        } else {
            updateBtn.setVisibility(View.GONE);
        }

        updateBtn.setOnClickListener(view -> {
            Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
            startActivity(updateIntent);
        });
    }

    private void openSettings() {
        Button settingBtn = findViewById(R.id.setting_button);
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

    private void getRoot() {
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            BufferedReader dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if(dos != null) {
                dos.writeBytes("id\n");
                dos.flush();
                String rootCheck = dis.readLine();
                if(rootCheck.contains("uid=0")) {
                    root = true;
                }
            }
        } catch (Exception ignored) {
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

    private void supportedFiles() {
        String pathDev = "/dev";
        File file1 = new File(pathDev,"hidg0");
        File file2 = new File(pathDev,"hidg1");
        if(!file1.exists() && !file2.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle(getResources().getString(R.string.kernel_err));
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()));
            AlertDialog kernelExit = builder.create();
            Objects.requireNonNull(kernelExit.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            kernelExit.show();
        } else {
            try {
                dos.writeBytes("chmod 666 /dev/hidg0\n");
                dos.writeBytes("chmod 666 /dev/hidg1\n");
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ide() {
        int[] colors = getResources().getIntArray(R.array.colors);
        Button DelBtn = findViewById(R.id.delBtn);
        Button SaveBtn = findViewById(R.id.svBtb);
        Button LoadBtn = findViewById(R.id.ldBtn);
        Button ExeBtn = findViewById(R.id.exBtn);
        Button CfgBtn = findViewById(R.id.cfgBtn);

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
            AlertDialog.Builder builder= new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle(getResources().getString(R.string.title_delete_file));
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                if(file.exists()){
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file.exists()){
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
                Snackbar.make(view, fileName[i] + " "+getResources().getString(R.string.file_deleted),Snackbar.LENGTH_SHORT).setBackgroundTint(colors[config.getAccentTheme()]).show();
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog delDialog = builder.create();
            Objects.requireNonNull(delDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            delDialog.show();
        });

        SaveBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle(getResources().getString(R.string.title_save_file));
            LayoutInflater saveLI = LayoutInflater.from(this);
            final View saveView = saveLI.inflate(R.layout.editor_save, null);
            builder.setView(saveView);
            final EditText fileName = saveView.findViewById(R.id.save_filename);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
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
                        Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
                        c.init(Cipher.ENCRYPT_MODE, key, iv);
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(new CipherOutputStream(fOutputStream, c));
                    } else {
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(fOutputStream);
                    }
                    outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    fOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Snackbar.make(view, file.getName() + " "+getResources().getString(R.string.file_saved),Snackbar.LENGTH_SHORT).setBackgroundTint(colors[config.getAccentTheme()]).show();
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog saveDialog = builder.create();
            Objects.requireNonNull(saveDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            saveDialog.show();
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
            AlertDialog.Builder builder= new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle(getResources().getString(R.string.title_open_file));
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                EditText scripts = findViewById(R.id.code);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                FileInputStream fInputStream;
                InputStream inputStream;
                StringWriter writer;
                try {
                    if (config.getSec() && file.getPath().endsWith(".enc")) {
                        Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
                        c.init(Cipher.DECRYPT_MODE, key, iv);
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(new CipherInputStream(fInputStream,c));
                    } else {
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(fInputStream);
                    }
                    writer = new StringWriter();
                    IOUtils.copy(inputStream, writer, "UTF-8");
                    scripts.setText(writer.toString());
                    inputStream.close();
                    fInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog loadDialog = builder.create();
            Objects.requireNonNull(loadDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            loadDialog.show();
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
            getRoot();
            if(root) {
                supportedFiles();
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
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                builder.setTitle(getResources().getString(R.string.root_err));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> dialog.cancel()));
                AlertDialog rootMissing = builder.create();
                Objects.requireNonNull(rootMissing.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                rootMissing.show();
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
            IOUtils.copy(inputStream, writer, "UTF-8");
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
        String url = "https://raw.githubusercontent.com/mayankmetha/Rucky-KeyMap/main/keymap.json";
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
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