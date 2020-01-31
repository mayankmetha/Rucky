package com.mayank.rucky;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static Boolean advSecurity = false;
    public static double currentVersion;
    public static double newVersion;
    public static long downloadRef;
    public DownloadManager downloadManager;
    public static int dlStatus;
    final static private int PERM = 0;
    public static final String CHANNEL_ID = "com.mayank.rucky";
    public static final String CHANNEL_NAME = "Update";
    private NotificationManager notificationManager;
    Process p;
    private static DataOutputStream dos;
    public static String getSHA512;
    public static String genSHA512;
    private Boolean root = false;
    public static int distro = 0;
    public static boolean updateEnable = false;
    private static AlertDialog waitDialog;
    public static SecretKey key;
    ArrayList<String> languages = new ArrayList<>();
    ArrayList<String> modes = new ArrayList<>();
    private static String piIp = null;
    public static boolean piConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        advSecurity = settings.getBoolean(SettingsActivity.PREF_SETTING_ADV_SECURITY, false);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            permission();
            getRoot();
            if(!root) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Root Access Required For USB Cable Attack!");
                builder.setCancelable(false);
                builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                AlertDialog rootMissing = builder.create();
                rootMissing.show();
            }
        }
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null,null);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
        cleanup();
        Spinner language = findViewById(R.id.langMenu);
        languages.add("American English");
        languages.add("Turkish");
        languages.add("Swedish");
        languages.add("Slovenian");
        languages.add("Russian");
        languages.add("Portuguese");
        languages.add("Norwegian");
        languages.add("Italian");
        languages.add("Croatian");
        languages.add("United Kingdom English");
        languages.add("French");
        languages.add("Finnish");
        languages.add("Spanish");
        languages.add("Danish");
        languages.add("German");
        languages.add("Canadian French");
        languages.add("Brazilian Portuguese");
        languages.add("Belarusian");
        languages.add("Hungarian");
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(langAdapter);
        Spinner mode = findViewById(R.id.modeMenu);
        modes.add("USB Cable (Root)");
        modes.add("Raspberry Pi (Wi-Fi)");
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode.setAdapter(modeAdapter);
        Button DelBtn = findViewById(R.id.delBtn);
        Button SaveBtn = findViewById(R.id.svBtb);
        Button LoadBtn = findViewById(R.id.ldBtn);
        Button ExeBtn = findViewById(R.id.exBtn);
        DelBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!advSecurity) {
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
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select File");
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
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
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        SaveBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("File Name");
            final EditText fileName = new EditText(MainActivity.this);
            builder.setView(fileName);
            builder.setCancelable(false);
            builder.setPositiveButton("Save", (dialog, which) -> {
                EditText scripts = findViewById(R.id.code);
                File file;
                if (advSecurity) {
                    file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName.getText().toString()+".enc");
                } else {
                    file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName.getText().toString()+".txt");
                }
                String content = scripts.getText().toString();
                FileOutputStream fOutputStream;
                OutputStream outputStream;
                try {
                    if (advSecurity) {
                        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        c.init(Cipher.ENCRYPT_MODE, key);
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(new CipherOutputStream(fOutputStream, c));
                        outputStream.write(content.getBytes(Charset.forName("UTF-8")));
                        outputStream.close();
                        fOutputStream.close();
                    } else {
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(fOutputStream);
                        outputStream.write(content.getBytes(Charset.forName("UTF-8")));
                        outputStream.close();
                        fOutputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        LoadBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!advSecurity) {
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
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select File");
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                EditText scripts = findViewById(R.id.code);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                FileInputStream fInputStream;
                InputStream inputStream;
                StringWriter writer;
                try {
                    if (advSecurity) {
                        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        c.init(Cipher.DECRYPT_MODE, key);
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(new CipherInputStream(fInputStream,c));
                        writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        scripts.setText(writer.toString());
                        inputStream.close();
                        fInputStream.close();
                    } else {
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(fInputStream);
                        writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        scripts.setText(writer.toString());
                        inputStream.close();
                        fInputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        ExeBtn.setOnClickListener(view -> {
            EditText scripts = findViewById(R.id.code);
            launchAttack(modes.indexOf(mode.getSelectedItem().toString()),languages.indexOf(language.getSelectedItem().toString()),scripts.getText().toString());
        });
        if (updateEnable)
            updater(0);
    }

    void launchAttack(int mode, int language, String scripts) {
        if(mode == 0) {
            getRoot();
            if(root) {
                supportedFiles();
                try {
                    hid exeScript = new hid(language);
                    exeScript.parse(scripts);
                    ArrayList<String> cmds = exeScript.getCmd();
                    for(int i = 0; i < cmds.size(); i++) {
                        dos.writeBytes(cmds.get(i));
                        dos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Root Access Required For USB Cable Attack!");
                builder.setCancelable(false);
                builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                builder.setNegativeButton("Exit", ((dialog, which) -> {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }));
                AlertDialog rootMissing = builder.create();
                rootMissing.show();
            }
        }
        if(mode == 1) {
            if(getPi()) {
                if(!piConnected) {
                    if(!piIp.equals("")) {
                        hid exeScript = new hid(language);
                        exeScript.parse(scripts);
                        ArrayList<String> cmds = exeScript.getCmd();
                        Thread piThread = new Thread(new wifiSocket(cmds));
                        piThread.start();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Raspberry Pi connection failed!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                    AlertDialog pi = builder.create();
                    pi.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Raspberry Pi Not Found!");
                builder.setCancelable(false);
                builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                AlertDialog pi = builder.create();
                pi.show();
            }
        }
    }

    private boolean getPi() {
        boolean support = false;
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifi != null;
        if(wifi.isWifiEnabled()) {
            WifiInfo info = wifi.getConnectionInfo();
            if(info.getSSID().equals("\"RUCKY\"")) {
                try {
                    piIp = InetAddress.getByAddress(
                            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(info.getIpAddress()).array())
                            .getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                support = true;
            }
        }
        return support;
    }

    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(updateEnable)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        else
            getMenuInflater().inflate(R.menu.menu_noupdate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        } else if (item.getItemId() == R.id.Update && updateEnable) {
            updater(1);
        }
        return true;
    }

    @Override
    public void onResume()throws NullPointerException {
        super.onResume();
        if (didThemeChange) {
            didThemeChange = false;
            finish();
            startActivity(getIntent());
        }
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadBR, filter);
        if(updateEnable)
            updater(0);
        permission();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadBR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void checkUpdate()throws NullPointerException {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                Notification.Builder updateNotify;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    updateNotify = new Notification.Builder(this, CHANNEL_ID);
                } else {
                    updateNotify = new Notification.Builder(this);
                }
                updateNotify.setContentTitle("Checking for update")
                        .setContentText("Please Wait...")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);
                getManager().notify(3,updateNotify.build());
                URL url = new URL("https://github.com/mayankmetha/Rucky/releases/latest");
                new fetchVersion().execute(url);
                getManager().cancel(3);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Please check the network connection")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    private static class fetchVersion extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(URL... urls) {
            String str = "";
            try {
                HttpsURLConnection conn = (HttpsURLConnection) urls[0].openConnection();
                conn.getInputStream();
                str = ""+conn.getURL()+"";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str.substring(str.lastIndexOf('/')+1);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.isEmpty()){
                newVersion = currentVersion;
            } else {
                newVersion = Double.parseDouble(result);
            }
        }
    }

    void updater(int mode) {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            checkUpdate();
            if (currentVersion < newVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("New update available. Want do update now?")
                        .setCancelable(false)
                        .setPositiveButton("Download & Install", (dialog, id) -> {
                            getDownloadHash();
                            Uri dl = Uri.parse("https://github.com/mayankmetha/Rucky/releases/download/"+newVersion+"/rucky.apk");
                            download(dl);
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> {
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                if (mode == 1) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("No update found")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }
        } else {
            if (mode == 1) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage("Please check the network connection")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        }
    }

    void download(Uri uri) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Please leave the app open till install screen starts")
                .setCancelable(false);
        waitDialog = alertBuilder.create();
        waitDialog.show();
        File fDel = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky.apk");
        if (fDel.exists()) {
            fDel.delete();
            if(fDel.exists()){
                try {
                    fDel.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(fDel.exists()){
                    getApplicationContext().deleteFile(fDel.getName());
                }
            }
        }
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        req.setTitle("rucky.apk");
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/rucky.apk");
        req.setVisibleInDownloadsUi(true);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_SUCCESSFUL | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING);
        Cursor c = downloadManager.query(q);
        while (c.moveToNext()) {
            downloadManager.remove(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
        }
        downloadRef = downloadManager.enqueue(req);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadBR, filter);
    }

    private final BroadcastReceiver downloadBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadRef);
            Cursor c = downloadManager.query(q);
            if (c != null && c.moveToFirst()) {
                dlStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refId == downloadRef && dlStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    SystemClock.sleep(5000);
                    waitDialog.dismiss();
                    generateHash();
                }
            }
        }
    };

    void getDownloadHash() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                Notification.Builder updateNotify;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    updateNotify = new Notification.Builder(this, CHANNEL_ID);
                } else {
                    updateNotify = new Notification.Builder(this);
                }
                updateNotify.setContentTitle("Verifying update")
                        .setContentText("Please Wait...")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);
                getManager().notify(3,updateNotify.build());
                URL url = new URL("https://github.com/mayankmetha/Rucky/releases/download/"+newVersion+"/rucky.sha512");
                new fetchHash().execute(url);
                getManager().cancel(3);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Please check the network connection")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    private static class fetchHash extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(URL... urls) {
            String str = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                str = in.readLine();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSHA512 = str;
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    void generateHash() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky.apk");
        try {
            genSHA512 = Files.asByteSource(file).hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(getSHA512.equals(genSHA512)) {
            installUpdate();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Update file corrupted!")
                    .setCancelable(false)
                    .setNegativeButton("TRY AGAIN LATER", (dialog, which) -> {
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    void installUpdate() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky.apk");
        Intent installer;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            installer = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            installer.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri apkUri = Uri.fromFile(file);
            installer = new Intent(Intent.ACTION_VIEW);
            installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            installer.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        startActivity(installer);
    }

    private void permission() {
        //Android permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERM);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supportedFiles() {
        String pathDev = "/dev";
        File file1 = new File(pathDev,"hidg0");
        File file2 = new File(pathDev,"hidg1");
        if(!file1.exists() && !file2.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Kernel Not Supported For USB Cable Attack!");
            builder.setCancelable(false);
            builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
            AlertDialog kernelExit = builder.create();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERM) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                permission();
            }
        }
    }

    void cleanup() {
        if(root) {
            try {
                dos.writeBytes("rm -rf /data/local/tmp/rucky-hid\n");
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}