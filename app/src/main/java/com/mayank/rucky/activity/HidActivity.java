package com.mayank.rucky.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appmattus.certificatetransparency.CTHostnameVerifierBuilder;
import com.datatheorem.android.trustkit.TrustKit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mayank.rucky.R;
import com.mayank.rucky.models.HidModel;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.HidAdapter;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class HidActivity extends AppCompatActivity {

    Config config;
    ListView hidList;
    HidAdapter adapter;

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
        setContentView(R.layout.activity_hid);
        config.setHIDIntent("");
        config.setHIDFile("");
        hidList = findViewById(R.id.hid_list);
        adapter = new HidAdapter(EditorActivity.keymap, this);
        hidList.removeAllViewsInLayout();
        hidList.removeAllViewsInLayout();
        hidList.setAdapter(adapter);
        hidList.setOnItemClickListener((parent, view, position, id) -> {
            HidModel model = Objects.requireNonNull(adapter.getItem(position));
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(model.getHidModelName());
            builder.setCancelable(true);

            switch (model.getHidModelState()) {
                case Constants.HID_DOWNLOAD:
                    builder.setPositiveButton(R.string.hid_download, (dialog, which) -> {
                        hidDownload(this, view, model);
                        dialog.cancel();
                    });
                    break;
                case Constants.HID_UPDATE:
                    builder.setPositiveButton(R.string.hid_update, (dialog, which) -> {
                        hidDownload(this, view, model);
                        dialog.cancel();
                    });
                    builder.setNegativeButton(R.string.hid_delete, (dialog, which) -> {
                        hidDelete(model);
                        dialog.cancel();
                    });
                    builder.setNeutralButton(R.string.hid_edit, (dialog, which) -> {
                        keylist(model.getHidModelName(), model.getHidModelFilename());
                        dialog.cancel();
                    });
                case Constants.HID_OFFLINE:
                    builder.setNegativeButton(R.string.hid_delete, (dialog, which) -> {
                        hidDelete(model);
                        dialog.cancel();
                    });
                    builder.setNeutralButton(R.string.hid_edit, (dialog, which) -> {
                        keylist(model.getHidModelName(), model.getHidModelFilename());
                        dialog.cancel();
                    });
                    break;
            }
            builder.show();
        });

        Button refreshHid = findViewById(R.id.refresh_hid_btn);
        refreshHid.setFilterTouchesWhenObscured(true);
        refreshHid.setOnClickListener(view -> updateListView());

        Button addNewHid = findViewById(R.id.add_hid_btn);
        addNewHid.setFilterTouchesWhenObscured(true);
        LayoutInflater saveLI = LayoutInflater.from(this);
        final View saveView = saveLI.inflate(R.layout.editor_save, null);
        final EditText fileName = saveView.findViewById(R.id.save_filename);
        addNewHid.setOnClickListener(view -> new MaterialAlertDialogBuilder(HidActivity.this)
                .setTitle(getResources().getString(R.string.file_name))
                .setView(saveView)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
                    File file;
                    String fileNameString = fileName.getText().toString().replaceAll("[\\\\/.]+","");
                    if (fileNameString.isEmpty()) {
                        fileNameString = String.valueOf(new Date().getTime());
                    }
                    final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
                    assert tmp != null;
                    for(File lfile : tmp) {
                        if (fileNameString.equals(lfile.getName().replace(".json",""))) {
                            fileNameString = String.valueOf(new Date().getTime());
                            break;
                        }
                    }
                    file = new File(getExternalFilesDir("keymap"),fileNameString+".json");
                    FileOutputStream fOutputStream;
                    OutputStream outputStream;
                    JSONObject jsonFile = new JSONObject();
                    try {
                        jsonFile.put("version","0");
                        jsonFile.put("mapping", new JSONObject());
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(fOutputStream);
                        outputStream.write(jsonFile.toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.close();
                        fOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                    updateListView();
                })
                .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                .show());
    }

    @Override
    protected void onResume() {
        config.setHIDFile("");
        config.setHIDIntent("");
        super.onResume();
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    void updateListView() {
        EditorActivity.keymapListRefresh(getApplicationContext());
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            hidList.removeAllViewsInLayout();
            hidList.setAdapter(adapter);
        }, 500);
    }

    void hidDelete(HidModel model) {
        File file = new File(this.getExternalFilesDir("keymap"),model.getHidModelFilename());
        file.delete();
        if(file.exists()){
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file.exists()){
                this.deleteFile(file.getName());
            }
        }
        hidList.removeAllViewsInLayout();
        adapter.notifyDataSetChanged();
        updateListView();
    }

    void hidDownload(Context context, View v, HidModel hidViewModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack() {
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

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, hidViewModel.getHidModelUrl(), null,
            response -> {
                try {
                    File file = new File(context.getExternalFilesDir("keymap"),hidViewModel.getHidModelFilename());
                    FileOutputStream fOutputStream = new FileOutputStream(file);
                    OutputStream outputStream = new BufferedOutputStream(fOutputStream);
                    outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    fOutputStream.close();
                    hidViewModel.setHidModelState(Constants.HID_OFFLINE);
                    hidViewModel.setHidModelRevision(response.getInt("version"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                redrawItem(v, hidViewModel);
                adapter.notifyDataSetChanged();
            }, Throwable::printStackTrace);
        requestQueue.add(req);
    }

    void keylist(String name, String file) {
        Intent intent = new Intent(HidActivity.this, KeylistActivity.class);
        config.setHIDIntent(name);
        config.setHIDFile(file);
        startActivity(intent);
    }

    void redrawItem(View view, HidModel model) {
        TextView name = view.findViewById(R.id.hid_name);
        name.setText(model.getHidModelName());
        TextView rev = view.findViewById(R.id.hid_version);
        rev.setText(getResources().getString(R.string.hid_version,model.getHidModelRevision()));
        ImageView icon = view.findViewById(R.id.hid_icon);
        icon.setFilterTouchesWhenObscured(true);
        switch (model.getHidModelState()) {
            case Constants.HID_DOWNLOAD:
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hid_download));
                break;
            case Constants.HID_UPDATE:
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hid_update));
                break;
            case Constants.HID_OFFLINE:
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hid_offline));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

}