package com.mayank.rucky;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static double currentVersion;
    public static double newVersion;
    public static long downloadRef;
    public DownloadManager downloadManager;
    public static int dlStatus;
    final static private int STORAGE_PERM = 0;
    public static final String CHANNEL_ID = "com.mayank.rucky";
    public static final String CHANNEL_NAME = "Update";
    private NotificationManager notificationManager;
    Process p;
    DataOutputStream dos;
    BufferedReader dis;
    public static String getSHA512;
    public static String genSHA512;
    private Boolean root = false;
    static private int downloadId = 0;
    public static int distro = 0;
    public static boolean updateEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            permission();
            if(root) {
                supportedFiles();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Root Access Required!");
                builder.setCancelable(false);
                builder.setNegativeButton("Exit", ((dialog, which) -> {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }));
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
        ImageButton SaveBtn = findViewById(R.id.svBtb);
        ImageButton LoadBtn = findViewById(R.id.ldBtn);
        ImageButton ExeBtn = findViewById(R.id.exBtn);
        SaveBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("File Name");
            final EditText fileName = new EditText(MainActivity.this);
            builder.setView(fileName);
            builder.setCancelable(false);
            builder.setPositiveButton("Save", (dialog, which) -> {
                EditText scripts = findViewById(R.id.code);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName.getText().toString());
                String content = scripts.getText().toString();
                FileOutputStream outputStream;
                OutputStreamWriter outputStreamWriter;
                try {
                    outputStream = new FileOutputStream(file);
                    outputStreamWriter = new OutputStreamWriter(outputStream);
                    outputStreamWriter.write(content);
                    outputStreamWriter.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        LoadBtn.setOnClickListener(view -> {
            final File[] files = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            CharSequence[] fileName = new CharSequence[files.length];
            for (int i = 0; i < files.length; i++) {
                fileName[i] = files[i].getName();
            }
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select File");
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                EditText scripts = findViewById(R.id.code);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files[i].getName());
                FileInputStream inputStream;
                InputStreamReader inputStreamReader;
                BufferedReader bufferedReader;
                StringBuilder stringBuilder;
                String str;
                try {
                    inputStream = new FileInputStream(file);
                    inputStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    stringBuilder = new StringBuilder();
                    while((str = bufferedReader.readLine()) != null) {
                        stringBuilder.append(str).append("\n");
                    }
                    str = stringBuilder.toString();
                    scripts.setText(str);
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        ExeBtn.setOnClickListener(view -> {
            EditText scripts = findViewById(R.id.code);
            try {
                genScript(scripts.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (updateEnable)
            updater(0);
    }



    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    void genScript(String str)throws Exception {
        String[] lines = str.split("\\r?\\n");
        String con;
        float defdelay = 0;
        for (int a = 0; a < lines.length; a++) {
            //DEFAULTDELAY or DEFAULT_DELAY
            if (a == 0 && (lines[a].startsWith("DEFAULTDELAY") || lines[a].startsWith("DEFAULT_DELAY"))) {
                con = lines[a];
                con = con.replace("DEFAULTDELAY ", "");
                con = con.replace("DEFAULT_DELAY ", "");
                defdelay = Float.parseFloat(con)/1000;
            }
            //DELAY
            else if (lines[a].startsWith("DELAY")) {
                con = lines[a].replace("DELAY ", "");
                float delay = Float.parseFloat(con)/1000;
                dos.writeBytes("sleep "+delay+"\n");
                dos.flush();
            }
            //REM
            else if (lines[a].startsWith("REM")) {
                continue;
            }
            //GUI or WINDOWS
            else if (lines[a].startsWith("GUI") || lines[a].startsWith("WINDOWS")) {
                con = lines[a];
                con = con.replace("WINDOWS ", "");
                con = con.replace("GUI ", "");
                char ch = con.charAt(0);
                dos.writeBytes("echo left-meta " + ch + " | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //MENU or APP
            else if (lines[a].equals("APP") || lines[a].equals("MENU")) {
                dos.writeBytes("echo left-shift f10 | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //SHIFT
            else if (lines[a].startsWith("SHIFT")) {
                con = lines[a].replace("SHIFT ","");
                String shseq = "";
                switch (con) {
                    case "DELETE":
                        shseq = "delete";
                        break;
                    case "HOME":
                        shseq = "home";
                        break;
                    case "INSERT":
                        shseq = "insert";
                        break;
                    case "PAGEUP":
                        shseq = "pgup";
                        break;
                    case "PAGEDOWN":
                        shseq = "pgdown";
                        break;
                    case "WINDOWS":
                    case "GUI":
                        shseq = "left-meta";
                        break;
                    case "DOWNARROW":
                    case "DOWN":
                        shseq = "down";
                        break;
                    case "UPARROW":
                    case "UP":
                        shseq = "up";
                        break;
                    case "LEFTARROW":
                    case "LEFT":
                        shseq = "left";
                        break;
                    case "RIGHTARROW":
                    case "RIGHT":
                        shseq = "right";
                        break;
                    case "TAB":
                        shseq = "tab";
                        break;
                }
                dos.writeBytes("echo left-shift "+shseq+" | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //ALT
            else if(lines[a].startsWith("ALT")) {
                con = lines[a].replace("ALT ","");
                String altseq;
                switch (con) {
                    case "END":
                        altseq = "end";
                        break;
                    case "ESC":
                        altseq = "esc";
                        break;
                    case "ESCAPE":
                        altseq = "escape";
                        break;
                    case "SPACE":
                        altseq = "space";
                        break;
                    case "TAB":
                        altseq = "tab";
                        break;
                    case "F1":
                        altseq = "f1";
                        break;
                    case "F2":
                        altseq = "f2";
                        break;
                    case "F3":
                        altseq = "f3";
                        break;
                    case "F4":
                        altseq = "f4";
                        break;
                    case "F5":
                        altseq = "f5";
                        break;
                    case "F6":
                        altseq = "f6";
                        break;
                    case "F7":
                        altseq = "f7";
                        break;
                    case "F8":
                        altseq = "f8";
                        break;
                    case "F9":
                        altseq = "f9";
                        break;
                    case "F10":
                        altseq = "f10";
                        break;
                    case "F11":
                        altseq = "f11";
                        break;
                    case "F12":
                        altseq = "f12";
                        break;
                    default:
                        altseq = "" + con.charAt(0) + "";
                        break;
                }
                dos.writeBytes("echo left-alt "+altseq+" | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //CONTROL or CTRL
            else if (lines[a].startsWith("CONTROL") || lines[a].startsWith("CTRL")) {
                con = lines[a];
                con = con.replace("CONTROL ", "");
                con = con.replace("CTRL ", "");
                String ctrlseq;
                switch (con) {
                    case "PAUSE":
                    case "BREAK":
                        ctrlseq = "pause";
                        break;
                    case "F1":
                        ctrlseq = "f1";
                        break;
                    case "F2":
                        ctrlseq = "f2";
                        break;
                    case "F3":
                        ctrlseq = "f3";
                        break;
                    case "F4":
                        ctrlseq = "f4";
                        break;
                    case "F5":
                        ctrlseq = "f5";
                        break;
                    case "F6":
                        ctrlseq = "f6";
                        break;
                    case "F7":
                        ctrlseq = "f7";
                        break;
                    case "F8":
                        ctrlseq = "f8";
                        break;
                    case "F9":
                        ctrlseq = "f9";
                        break;
                    case "F10":
                        ctrlseq = "f10";
                        break;
                    case "F11":
                        ctrlseq = "f11";
                        break;
                    case "F12":
                        ctrlseq = "f12";
                        break;
                    case "ESC":
                        ctrlseq = "esc";
                        break;
                    case "ESCAPE":
                        ctrlseq = "escape";
                        break;
                    default:
                        ctrlseq = "" + con.charAt(0) + "";
                        break;
                }
                dos.writeBytes("echo left-ctrl "+ctrlseq+" | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //DOWNARROW or DOWN
            else if (lines[a].startsWith("DOWNARROW") || lines[a].startsWith("DOWN")) {
                dos.writeBytes("echo down | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //UPARROW or UP
            else if (lines[a].startsWith("UPARROW") || lines[a].startsWith("UP")) {
                dos.writeBytes("echo up | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //LEFTARROW or LEFT
            else if (lines[a].startsWith("LEFTARROW") || lines[a].startsWith("LEFT")) {
                dos.writeBytes("echo left | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //RIGHTARROW or RIGHT
            else if (lines[a].startsWith("RIGHTARROW") || lines[a].startsWith("RIGHT")) {
                dos.writeBytes("echo right | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //BREAK or PAUSE
            else if (lines[a].startsWith("BREAK") || lines[a].startsWith("PAUSE")) {
                dos.writeBytes("echo pause | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //CAPSLOCK
            else if (lines[a].startsWith("CAPSLOCK")) {
                dos.writeBytes("echo capslock | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //DELETE
            else if (lines[a].startsWith("DELETE")) {
                dos.writeBytes("echo delete | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //END
            else if (lines[a].startsWith("END")) {
                dos.writeBytes("echo end | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //ESC
            else if (lines[a].startsWith("ESC")) {
                dos.writeBytes("echo esc | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //ESCAPE
            else if (lines[a].startsWith("ESCAPE")) {
                dos.writeBytes("echo escape | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //HOME
            else if (lines[a].startsWith("HOME")) {
                dos.writeBytes("echo home | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //INSERT
            else if (lines[a].startsWith("INSERT")) {
                dos.writeBytes("echo insert | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //NUMLOCK
            else if (lines[a].startsWith("NUMLOCK")) {
                dos.writeBytes("echo numlock | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //PAGEUP
            else if (lines[a].startsWith("PAGEUP")) {
                dos.writeBytes("echo pgup | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //PAGEDOWN
            else if (lines[a].startsWith("PAGEDOWN")) {
                dos.writeBytes("echo pgdown | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //PRINTSCREEN or PRINTSCRN or PRNTSCRN or PRTSCN or PRSC or PRTSCR
            else if (lines[a].startsWith("PRINTSCREEN") || lines[a].startsWith("PRINTSCRN") ||
                    lines[a].startsWith("PRNTSCRN") || lines[a].startsWith("PRTSCN") ||
                    lines[a].startsWith("PRSC") || lines[a].startsWith("PRTSCR")) {
                dos.writeBytes("echo print | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //SCROLLLOCK
            else if (lines[a].startsWith("SCROLLLOCK")) {
                dos.writeBytes("echo scrolllock | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //SPACE
            else if (lines[a].startsWith("SPACE")) {
                dos.writeBytes("echo space | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //TAB
            else if (lines[a].startsWith("TAB")) {
                dos.writeBytes("echo tab | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //BACKSPACE or BKSP
            else if (lines[a].startsWith("BACKSPACE") || lines[a].startsWith("BKSP")) {
                dos.writeBytes("echo backspace | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //ENTER
            else if (lines[a].startsWith("ENTER")) {
                dos.writeBytes("echo enter | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                dos.flush();
            }
            //REPEAT
            else if (lines[a].startsWith("REPEAT")) {
                con = lines[a].replace("REPEAT ","");
                int x = parseInt(con);
                String last = lines[a-1];
                for(int i = 0; i < x; i++) {
                    genScript(last);
                }
            }
            //STRING
            else if (lines[a].startsWith("STRING")) {
                con = lines[a].replace("STRING ", "");
                con = con.replace("\n", "");
                char[] ch = con.toCharArray();
                String cha;
                for (char aCh : ch) {
                    cha = convert(aCh);
                    dos.writeBytes("echo " + cha + " | /data/local/tmp/rucky-hid /dev/hidg0 keyboard\n");
                    dos.flush();
                }
            }
            dos.writeBytes("sleep "+defdelay+"\n");
            dos.flush();
        }
    }

    String convert(char ch) {
        if(ch == ' ') {
            return "space";
        }
        else if(ch == '!') {
            return "left-shift 1";
        }
        else if(ch == '.') {
            return "period";
        }
        else if(ch == '`') {
            return "backquote";
        }
        else if(ch == '~') {
            return "left-shift tilde";
        }
        else if(ch == '+') {
            return "kp-plus";
        }
        else if(ch == '=') {
            return "equal";
        }
        else if(ch == '_') {
            return "left-shift minus";
        }
        else if(ch == '-') {
            return "minus";
        }
        else if(ch == '"') {
            return "left-shift quote";
        }
        else if(ch == '\'') {
            return "quote";
        }
        else if(ch == ':') {
            return "left-shift semicolon";
        }
        else if(ch == ';') {
            return "semicolon";
        }
        else if(ch == '<') {
            return "left-shift comma";
        }
        else if(ch == ',') {
            return "comma";
        }
        else if(ch == '>') {
            return "left-shift period";
        }
        else if(ch == '?') {
            return "left-shift slash";
        }
        else if(ch == '\\') {
            return "backslash";
        }
        else if(ch == '|') {
            return "left-shift backslash";
        }
        else if(ch == '/') {
            return "slash";
        }
        else if(ch == '{') {
            return "left-shift lbracket";
        }
        else if(ch == '}') {
            return "left-shift rbracket";
        }
        else if(ch == '(') {
            return "left-shift 9";
        }
        else if(ch == ')') {
            return "left-shift 0";
        }
        else if(ch == '[') {
            return "lbracket";
        }
        else if(ch == ']') {
            return "rbracket";
        }
        else if(ch == '#') {
            return "left-shift 3";
        }
        else if(ch == '@') {
            return "left-shift 2";
        }
        else if(ch == '$') {
            return "left-shift 4";
        }
        else if(ch == '%') {
            return "left-shift 5";
        }
        else if(ch == '^') {
            return "left-shift 6";
        }
        else if(ch == '&') {
            return "left-shift 7";
        }
        else if(ch == '*') {
            return "kp-multiply";
        }
        else if(ch >= 'A' && ch <= 'Z') {
            String temp = ""+ch+"";
            temp = temp.toLowerCase();
            temp = "left-shift "+temp;
            return temp;

        }
        else return ""+ch+"";
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
                            downloadId = 1;
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
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                            });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }
        } else {
            if (mode == 1) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage("Please check the network connection")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                        });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        }
    }

    void download(Uri uri) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Please leave the app open till install screen starts")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
        File fDel = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky.apk");
        if (fDel.exists()) {
            fDel.delete();
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
                    if(downloadId == 1) generateHash();
                    else if(downloadId == 2) generateDependencyHash();
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
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            getSHA512 = result;
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
        Intent installer = new Intent(Intent.ACTION_VIEW);
        installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        installer.setDataAndType(apkUri, "application/vnd.android.package-archive");
        installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(installer);
    }

    private void permission() {
        //STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM);
        }
        //ROOT
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if(dos != null) {
                dos.writeBytes("id\n");
                dos.flush();
                String rootCheck = dis.readLine();
                if(rootCheck.contains("uid=0")) root = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supportedFiles() {
        String pathDev = "/dev";
        String pathTmp = "/data/local/tmp";
        File file1 = new File(pathDev,"hidg0");
        File file2 = new File(pathDev,"hidg1");
        File file3 = new File(pathTmp,"rucky-hid");
        if(!file1.exists() && !file2.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Kernel Not Supported!");
            builder.setCancelable(false);
            builder.setPositiveButton("Exit", (dialog, which) -> {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            });
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
        if(!file3.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Dependency file missing");
            builder.setCancelable(false);
            builder.setPositiveButton("Download & Install", (dialog, which) -> {
                String arch = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI;
                downloadDependenciesHash(arch);
                downloadId = 2;
                Uri dl = Uri.parse("https://raw.githubusercontent.com/mayankmetha/Rucky/master/release/" + arch + "/rucky-hid");
                downloadDependencies(dl);
            });
            builder.setNegativeButton("Exit", ((dialog, which) -> {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }));
            AlertDialog fileMissing = builder.create();
            fileMissing.show();
        } else {
            try {
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERM) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                permission();
            }
        }
    }

    private void downloadDependenciesHash(String arch) {
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
                updateNotify.setContentTitle("Verifying dependencies")
                        .setContentText("Please Wait...")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);
                getManager().notify(4,updateNotify.build());
                URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/master/release/"+arch+"/rucky-hid.sha512");
                new fetchHash().execute(url);
                getManager().cancel(4);
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

    private void downloadDependencies(Uri uri) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Please leave the app open")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
        File fDel = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky-hid");
        if (fDel.exists()) {
            fDel.delete();
        }
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        req.setTitle("rucky-hid");
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/rucky-hid");
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

    void generateDependencyHash() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky-hid");
        try {
            genSHA512 = Files.asByteSource(file).hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(getSHA512.equals(genSHA512)) {
            installDependency();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Dependency file corrupted!")
                    .setCancelable(false)
                    .setNegativeButton("TRY AGAIN LATER", (dialog, which) -> {
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    private void installDependency() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rucky-hid");
        String path = file.getAbsolutePath();
        try {
            dos.writeBytes("mv "+path+" /data/local/tmp/rucky-hid;chmod 755 /data/local/tmp/rucky-hid\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}