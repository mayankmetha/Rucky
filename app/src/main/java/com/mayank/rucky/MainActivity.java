package com.mayank.rucky;

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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static double currentVersion;
    public static double newVersion;
    public static long downloadRef;
    public DownloadManager downloadManager;
    public static int dlStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, false);
        theme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            try {
                Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void onResume() {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadBR);
    }

    void theme() {
        if (SettingsActivity.darkTheme) setTheme(R.style.AppThemeDark);
        else {
            setTheme(R.style.AppThemeLight);
        }
    }

    void checkUpdate() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            checkUpdate();
            if (currentVersion < newVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("New update available. Want do update now?")
                        .setCancelable(false)
                        .setPositiveButton("Download & Install", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri dl = Uri.parse("");
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
        File fDel = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/rucky.apk");
        if(fDel.exists()) {
            fDel.delete();
        }
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        req.setTitle("Downloading rucky update...");
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"rucky.apk");
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING);
        Cursor c = downloadManager.query(q);
        while(c.moveToNext()) {
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
            if(c != null && c.moveToFirst()) {
                dlStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refId == downloadRef && dlStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    installUpdate();
                }
            }
        }
    };

    void installUpdate() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/rucky.apk");
        Intent installer = new Intent(Intent.ACTION_VIEW);
        installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName()+".provider",file);
        installer.setDataAndType(apkUri,"application/vnd.android.package-archive");
        installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(installer);
    }
}