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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appmattus.certificatetransparency.CTHostnameVerifierBuilder;
import com.datatheorem.android.trustkit.TrustKit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mayank.rucky.R;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import javax.net.ssl.HttpsURLConnection;

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
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        EditorActivity.updateNotificationManager.cancel(0);
        setContentView(R.layout.activity_update);

        apkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),Constants.APK_NAME);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        deleteOldUpdateFiles();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadBR, intentFilter);

        TextView versionText = findViewById(R.id.version_text);
        if(EditorActivity.nightly)
            versionText.setText(String.format("%s : %s (%s)", getResources().getString(R.string.update_version_nightly), EditorActivity.newVersion, EditorActivity.newNightly));
        else
            versionText.setText(String.format("%s : %s", getResources().getString(R.string.update_version), EditorActivity.newVersion));

        changelog = findViewById(R.id.changelog);
        getChangelog();
        changelog.setMovementMethod(new ScrollingMovementMethod());

        ProgressBar downloadProgress = findViewById(R.id.update_progress);
        downloadProgress.setProgress(0);
        downloadProgress.setMax(100);
        downloadProgress.setEnabled(false);
        downloadProgress.setAlpha(0);

        TextView progressBarLabel = findViewById(R.id.progress_label);
        progressBarLabel.setEnabled(false);
        progressBarLabel.setAlpha(0);

        Button actionButton = findViewById(R.id.action_button);
        actionButton.setFilterTouchesWhenObscured(true);
        actionButton.setText(R.string.update_action);
        actionButton.setOnClickListener(view -> {
            actionButton.setEnabled(false);
            actionButton.setAlpha(0);
            downloadProgress.setEnabled(true);
            downloadProgress.setAlpha(1);
            progressBarLabel.setEnabled(true);
            progressBarLabel.setAlpha(1);
            dlRef = downloadUpdate();
            Runnable runnable = () -> {
                while (downloadProgress.getProgress() < 100 && (checkStatus(dlRef) == DownloadManager.STATUS_PENDING || checkStatus(dlRef) == DownloadManager.STATUS_RUNNING)) {
                    runOnUiThread(() -> downloadProgress.setProgress(checkProgress(dlRef)));
                }
                runOnUiThread(() -> {
                    if (downloadProgress.getProgress() != 100) {
                        downloadProgress.setProgress(0);
                        progressBarLabel.setEnabled(false);
                        progressBarLabel.setAlpha(0);
                        actionButton.setEnabled(true);
                        actionButton.setAlpha(1);
                    } else {
                        progressBarLabel.setText(R.string.install_update);
                    }
                    downloadProgress.setEnabled(false);
                    downloadProgress.setAlpha(0);
                });
            };
            new Thread(runnable).start();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
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
        String url = EditorActivity.nightly ? Constants.CHANGELOG_NIGHTLY : Constants.CHANGELOG_RELEASE+EditorActivity.newVersion;
        RequestQueue queue;
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

        queue.add(new StringRequest(Request.Method.GET, url, response -> changelog.setText(response.trim()), error -> {}));
    }

    void deleteOldUpdateFiles() {
        if (apkFile.exists() || apkFile.isFile()) {
            apkFile.delete();
            if(apkFile.exists() || apkFile.isFile()){
                try {
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
            float fileSize = cursor.getInt(Math.max(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES), 0));
            float downloadedSize = cursor.getInt(Math.max(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR),0));
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
        Uri uri;
        if (EditorActivity.nightly)
            uri = Uri.parse(Constants.URL_NIGHTLY);
         else
            uri = Uri.parse(Constants.URL_BASE_RELEASE+EditorActivity.newVersion+Constants.URL_FILE_RELEASE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        req.setTitle(getString(R.string.update_activity));
        req.setDestinationUri(Uri.fromFile(new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),Constants.APK_NAME)));
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
        String genSHA512 = computeSHA512(apkFile);
        if (genSHA512.equals(EditorActivity.getSHA512)) {
            installUpdate();
        } else {
            config.setUpdateFlag(false);
            new MaterialAlertDialogBuilder(UpdateActivity.this)
                    .setTitle(getResources().getString(R.string.update_hash_error))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.btn_continue), ((dialog, which) -> goBackToEditorActivity()))
                    .show();
        }
    }

    void goBackToEditorActivity() {
        deleteOldUpdateFiles();
        finish();
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
        installer.setDataAndType(apkUri, Constants.URI_INSTALLER);
        installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        finish();
        startActivity(installer);
    }

    private String computeSHA512(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();
            byte[] bytes = digest.digest();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            sb.delete(0, sb.length());
        }
        return sb.toString();
    }

}