package com.mayank.rucky.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mayank.rucky.R;
import com.mayank.rucky.models.HidModel;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.HidAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

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
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(Constants.themeList[config.getAccentTheme()]);
        setContentView(R.layout.activity_hid);

        hidList = findViewById(R.id.hid_list);
        adapter = new HidAdapter(EditorActivity.keymap, this);
        hidList.removeAllViewsInLayout();
        if (!EditorActivity.keymap.isEmpty()) {
            hidList.removeAllViewsInLayout();
            hidList.setAdapter(adapter);
            hidList.setOnItemClickListener((parent, view, position, id) -> {
                HidModel model = Objects.requireNonNull(adapter.getItem(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            hidDelete(view, model);
                            dialog.cancel();
                        });
                        builder.setNeutralButton(R.string.hid_edit, (dialog, which) -> {
                            keylist(model.getHidModelName(),model.getHidModelFilename());
                            dialog.cancel();
                        });
                    case Constants.HID_OFFLINE:
                        builder.setNegativeButton(R.string.hid_delete, (dialog, which) -> {
                            hidDelete(view, model);
                            dialog.cancel();
                        });
                        builder.setNeutralButton(R.string.hid_edit, (dialog, which) -> {
                            keylist(model.getHidModelName(),model.getHidModelFilename());
                            dialog.cancel();
                        });
                        break;
                }

                AlertDialog listAction = builder.create();
                Objects.requireNonNull(listAction.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                listAction.show();
            });
        }

        Button refreshHid = findViewById(R.id.refresh_hid_btn);
        refreshHid.setOnClickListener(view -> updateListView());

        Button addNewHid = findViewById(R.id.add_hid_btn);
        addNewHid.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(HidActivity.this);
            builder.setTitle(getResources().getString(R.string.file_name));
            final EditText fileName = new EditText(HidActivity.this);
            builder.setView(fileName);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
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
                try {
                    fOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fOutputStream);
                    outputStream.close();
                    fOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                updateListView();
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog saveDialog = builder.create();
            Objects.requireNonNull(saveDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            saveDialog.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void updateListView() {
        EditorActivity.keymapListRefresh(getApplicationContext());
        new Handler().postDelayed(() -> {
            hidList.removeAllViewsInLayout();
            if (!EditorActivity.keymap.isEmpty()) {
                hidList.setAdapter(adapter);
            }
        }, 500);
    }

    void hidDelete(View view, HidModel model) {
        File file = new File(this.getExternalFilesDir("keymap"),model.getHidModelFilename());
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
                this.deleteFile(file.getName());
            }
        }
        hidList.removeViewInLayout(view);
        adapter.remove(model);
        adapter.notifyDataSetChanged();
        updateListView();
    }

    void hidDownload(Context context, View v, HidModel hidViewModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

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
        intent.putExtra(Constants.activityTitle, name);
        intent.putExtra(Constants.activityFile, file);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    void redrawItem(View view, HidModel model) {
        TextView name = view.findViewById(R.id.hid_name);
        name.setText(model.getHidModelName());
        TextView rev = view.findViewById(R.id.hid_version);
        rev.setText("Version: "+ model.getHidModelRevision());
        ImageView icon = view.findViewById(R.id.hid_icon);
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

}