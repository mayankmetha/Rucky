package com.mayank.rucky;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static double currentVersion;
    public static double newVersion;
    public static long downloadRef;
    public DownloadManager downloadManager;
    public static int dlStatus;
    private static ProgressDialog mProgressDialog;
    final static private int STORAGE_PERM = 0;
    Process p;
    DataOutputStream dos;

    @Override
    protected void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, false);
        theme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            permission();
        }
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/master/release/version");
                final ProgressDialog dialog = ProgressDialog.show(this, "Checking for update", "Please Wait...", true);
                new fetchVersion().execute(url);
                dialog.dismiss();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        Button SaveBtn = (Button) findViewById(R.id.svBtb);
        Button LoadBtn = (Button) findViewById(R.id.ldBtn);
        Button ExeBtn = (Button) findViewById(R.id.exBtn);
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("File Name");
                final EditText fileName = new EditText(MainActivity.this);
                builder.setView(fileName);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText scripts = (EditText) findViewById(R.id.code);
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
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        LoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File[] files = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).listFiles();
                CharSequence[] fileName = new CharSequence[files.length];
                for (int i = 0; i < files.length; i++) {
                    fileName[i] = files[i].getName();
                }
                AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select File");
                builder.setItems(fileName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        EditText scripts = (EditText) findViewById(R.id.code);
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
                                stringBuilder.append(str+"\n");
                            }
                            str = stringBuilder.toString();
                            scripts.setText(str);
                            bufferedReader.close();
                            inputStreamReader.close();
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            }
        });
        ExeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText scripts = (EditText) findViewById(R.id.code);
                try {
                    genScript(scripts.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void genScript(String str)throws Exception {

        String[] lines = str.split("\\r?\\n");
        String con;
        int defdelay = 0;
        for(int a = 0; a < lines.length; a++) {
            //DEFAULTDELAY or DEFAULT_DELAY
            if(a == 0 && (lines[a].startsWith("DEFAULTDELAY ") || lines[a].startsWith("DEFAULT_DELAY "))) {
                con = lines[a];
                con = con.replace("DEFAULTDELAY ","");
                con = con.replace("DEFAULT_DELAY ","");
                defdelay = parseInt(con);
            }
            //DELAY
            if(lines[a].startsWith("DELAY ")) {
                con = lines[a].replace("DELAY ","");
                int delay = parseInt(con);
                p.waitFor(delay, TimeUnit.MILLISECONDS);
            }
            //REM
            if(lines[a].startsWith("REM")) {
                continue;
            }
            //STRING
            if(lines[a].startsWith("STRING ")) {
               con = lines[a].replace("STRING ","");
               con = con.replace("\n","");
               char[] ch = con.toCharArray();
               String cha;
               for(int b = 0; b < ch.length; b++) {
                    cha = convert(ch[b]);
                   dos.writeBytes("echo "+cha+" | /data/local/tmp/hid-gadget-test /dev/hidg0 keyboard > /dev/null\n");
                   dos.flush();
               }
            }
            p.waitFor(defdelay, TimeUnit.MILLISECONDS);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.Update:
                updater();
                break;
        }
        return true;
    }

    @Override
    public void onResume()throws NullPointerException {
        super.onResume();
        theme();
        if (didThemeChange) {
            didThemeChange = false;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadBR, filter);
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                mProgressDialog = ProgressDialog.show(this, "Checking for update", "Please Wait...", true);
                URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/master/release/version");
                new fetchVersion().execute(url);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mProgressDialog.dismiss();
                    }
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadBR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void theme() {
        if (SettingsActivity.darkTheme) setTheme(R.style.AppThemeDark);
        else {
            setTheme(R.style.AppThemeLight);
        }
    }

    void checkUpdate()throws NullPointerException {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/master/release/version");
                mProgressDialog = ProgressDialog.show(this, "Checking for update", "Please Wait...", true);
                new fetchVersion().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Please check the network connection")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    private static class fetchVersion extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
            newVersion = Double.parseDouble(result);
            mProgressDialog.dismiss();
        }
    }

    void updater() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            checkUpdate();
            if (currentVersion < newVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("New update available. Want do update now?")
                        .setCancelable(false)
                        .setPositiveButton("Download & Install", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri dl = Uri.parse("https://github.com/mayankmetha/Rucky/blob/master/release/rucky-" + newVersion + ".apk?raw=true");
                                download(dl);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage("No update found")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Please check the network connection")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    void download(Uri uri) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Please leave the app open till install screen starts")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
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
        req.setTitle("Downloading rucky update...");
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "rucky.apk");
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

    private BroadcastReceiver downloadBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadRef);
            Cursor c = downloadManager.query(q);
            if (c != null && c.moveToFirst()) {
                dlStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refId == downloadRef && dlStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    installUpdate();
                }
            }
        }
    };

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
        //SU
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERM:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do nothing
                    break;
                } else {
                    permission();
                }
                break;
        }
    }
}