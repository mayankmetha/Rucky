package com.mayank.rucky.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.mayank.rucky.R;
import com.mayank.rucky.service.SocketHeartbeatService;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.HID;

import org.apache.commons.io.IOUtils;

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
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

public class EditorActivity extends AppCompatActivity {

    private Boolean root = false;

    private Config config;
    public NotificationCompat.Builder updateNotify;
    static SecretKey key;
    static AlgorithmParameterSpec iv;
    Process p;
    private static DataOutputStream dos;
    public static ArrayList<String> cmds;
    public static NotificationManager serviceNotificationManager;
    @SuppressLint("StaticFieldLeak")
    public static NotificationCompat.Builder sNotify;

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
        if (savedInstanceState == null) {
            requestPermissions();
            getRoot();
        }
        setNotificationChannel();
        if (config.getUpdateFlag())
            updateInit();
        openSettings();
        ide();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SocketHeartbeatService.class));
        } else {
            startService(new Intent(this, SocketHeartbeatService.class));
        }
        updateNotification(this);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void buildNotification(Context context, String text) {
        Intent sIntent = new Intent(context, SplashActivity.class);
        sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent sPendingIntent;
        sPendingIntent = PendingIntent.getActivity(context, 0, sIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        if (c.getHIDMode() == 0)
            buildNotification(context,context.getString(R.string.config_status_net_disabled));
        else if (c.getHIDMode() == 1) {
            if (c.getNetworkStatus())
                buildNotification(context,context.getString(R.string.config_status_net_on));
            else
                buildNotification(context,context.getString(R.string.config_status_net_off));
        }
    }

    public static void updateServiceStatus(Context context) {
        Config config = new Config(context);
        updateNotification(context);
        if (ConfigActivity.statusText == null)
            return;
        if (ConfigActivity.statusImage == null)
            return;
        if (config.getHIDMode() == 0) {
            if (config.getUSBStatus()) {
                ConfigActivity.statusText.setText(R.string.config_status_usb_on);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_usb));
            } else {
                ConfigActivity.statusText.setText(R.string.config_status_usb_off);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_usb_off));
            }
        } else if (config.getHIDMode() == 1) {
            if (config.getNetworkStatus()) {
                ConfigActivity.statusText.setText(R.string.config_status_net_on);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_net));
            } else {
                ConfigActivity.statusText.setText(R.string.config_status_net_off);
                ConfigActivity.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_net_off));
            }
        }
    }

    private void updateInit() {
        Button updateBtn = findViewById(R.id.update_button);
        if (config.getUpdateFlag() && SplashActivity.newVersion > SplashActivity.currentVersion) {
            Intent updateIntent = new Intent(EditorActivity.this, UpdateActivity.class);
            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            updateNotify = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                    .setContentTitle(getResources().getString(R.string.update_new)+" Version: "+ SplashActivity.newVersion)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(notifyPendingIntent)
                    .setAutoCancel(false);
            SplashActivity.notificationManager.notify(0, updateNotify.build());
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
        } catch (Exception e) {
            e.printStackTrace();
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
            builder.setTitle(getResources().getString(R.string.file_select));
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
            builder.setTitle(getResources().getString(R.string.file_name));
            final EditText fileName = new EditText(EditorActivity.this);
            builder.setView(fileName);
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
            builder.setTitle(getResources().getString(R.string.file_select));
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
                    fetchCommands(language, scripts);
                } else {
                    try {
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
                fetchCommands(language, scripts);
            } else {
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

}