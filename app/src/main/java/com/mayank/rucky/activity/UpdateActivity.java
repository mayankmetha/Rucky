package com.mayank.rucky.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mayank.rucky.R;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.Networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateActivity extends AppCompatActivity {

    private DownloadManager downloadManager;
    static private File apkFile;
    static long dlRef;
    private Config config;
    private TextView changelog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        SplashActivity.notificationManager.cancel(0);
        setContentView(R.layout.activity_update);

        apkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"rucky.apk");
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        deleteOldUpdateFiles();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadBR, intentFilter);

        TextView versionText = findViewById(R.id.version_text);
        versionText.setText(String.format("%s : %s", getResources().getString(R.string.update_version), SplashActivity.newVersion));

        changelog = findViewById(R.id.changelog);
        getChangelog();
        changelog.setMovementMethod(new ScrollingMovementMethod());

        ProgressBar downloadProgress = findViewById(R.id.update_progress);
        downloadProgress.setProgress(0);
        downloadProgress.setMax(100);
        downloadProgress.setVisibility(View.INVISIBLE);

        TextView progressBarLabel = findViewById(R.id.progress_label);
        progressBarLabel.setVisibility(View.INVISIBLE);

        Button actionButton = findViewById(R.id.action_button);
        actionButton.setText(R.string.update_action);
        actionButton.setOnClickListener(view -> {
            actionButton.setVisibility(View.INVISIBLE);
            downloadProgress.setVisibility(View.VISIBLE);
            progressBarLabel.setVisibility(View.VISIBLE);
            dlRef = downloadUpdate();
            Runnable runnable = () -> {
                while (downloadProgress.getProgress() < 100 && (checkStatus(dlRef) == DownloadManager.STATUS_PENDING || checkStatus(dlRef) == DownloadManager.STATUS_RUNNING)) {
                    runOnUiThread(() -> downloadProgress.setProgress(checkProgress(dlRef)));
                }
                runOnUiThread(() -> {
                    if (downloadProgress.getProgress() != 100) {
                        downloadProgress.setProgress(0);
                        progressBarLabel.setVisibility(View.INVISIBLE);
                        actionButton.setVisibility(View.VISIBLE);
                    } else {
                        progressBarLabel.setText(R.string.install_update);
                    }
                    downloadProgress.setVisibility(View.INVISIBLE);
                });
            };
            new Thread(runnable).start();
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(downloadBR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void getChangelog() {
        StringBuilder changes = new StringBuilder();
        Networks n = new Networks();
        if (n.isNetworkPresent(this)) {
            Runnable runnable = () -> {
                try {
                    URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/master/docs/Changelog_"+SplashActivity.newVersion+"");
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line = in.readLine();
                    while (line != null) {
                        changes.append(line);
                        changes.append("\n");
                        line = in.readLine();
                    }
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                changes.deleteCharAt(changes.length() - 1);
                runOnUiThread(() -> changelog.setText(changes.toString()));
            };
            new Thread(runnable).start();
        }
    }

    void deleteOldUpdateFiles() {
        if (apkFile.exists() || apkFile.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            apkFile.delete();
            if(apkFile.exists() || apkFile.isFile()){
                try {
                    //noinspection ResultOfMethodCallIgnored
                    apkFile.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(apkFile.exists()){
                    getApplicationContext().deleteFile(apkFile.getName());
                }
            }
        }
    }

    private int checkProgress(long downloadRef) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadRef);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            float fileSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            float downloadedSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            float progress = downloadedSize/fileSize;
            if (fileSize != -1)
                return ((int)(progress*100));
        }
        return 0;
    }


    private int checkStatus(long downloadRef) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadRef);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

            return cursor.getInt(columnIndex);
        }
        return 0;
    }

    private long downloadUpdate() {
        Uri uri = Uri.parse("https://github.com/mayankmetha/Rucky/releases/download/"+SplashActivity.newVersion+"/rucky.apk");
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        req.setTitle(getString(R.string.update_activity));
        req.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"rucky.apk");
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        return downloadManager.enqueue(req);
    }

    private final BroadcastReceiver downloadBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkStatus(dlRef) == DownloadManager.STATUS_SUCCESSFUL) {
                generateHash();
            }
        }
    };

    void generateHash() {
        String genSHA512 = null;
        try {
            //noinspection UnstableApiUsage
            genSHA512 = Files.asByteSource(apkFile).hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert genSHA512 != null;
        if (genSHA512.equals(SplashActivity.getSHA512)) {
            installUpdate();
        }
    }

    void installUpdate() {
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        Intent installer = new Intent(Intent.ACTION_VIEW);
        installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        installer.setDataAndType(apkUri, "application/vnd.android.package-archive");
        installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        finish();
        startActivity(installer);
    }

}