package com.mayank.rucky.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mayank.rucky.R;
import com.mayank.rucky.models.KeyModel;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.KeyAdapter;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class KeylistActivity extends AppCompatActivity {

    Config config;
    ListView keylist;
    ArrayList<KeyModel> keys;
    KeyAdapter adapter;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        config = new Config(this);
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(Constants.themeList[config.getAccentTheme()]);
        setContentView(R.layout.activity_keylist);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getIntent().getStringExtra(Constants.activityTitle));

        //TODO: toolbar new buttons

        Button refreshBtn = findViewById(R.id.refresh_keymap_btn);
        refreshBtn.setOnClickListener(v -> refreshList());

        keys = new ArrayList<>();
        file = new File(getExternalFilesDir("keymap"), Objects.requireNonNull(getIntent().getStringExtra(Constants.activityFile)));

        keylist = findViewById(R.id.key_list);
        adapter = new KeyAdapter(keys, this);
        keylist.setAdapter(adapter);
        refreshList();

        //TODO: OnClickListener

    }

    void refreshList() {
        keys.clear();
        FileInputStream fInputStream;
        InputStream inputStream;
        StringWriter writer = new StringWriter();
        JSONObject keylistFile;
        try {
            fInputStream = new FileInputStream(file);
            inputStream = new BufferedInputStream(fInputStream);
            IOUtils.copy(inputStream, writer, "UTF-8");
            inputStream.close();
            fInputStream.close();
            if(!writer.toString().isEmpty()) {
                keylistFile = new JSONObject(writer.toString()).getJSONObject("mapping");
                for (Iterator<String> it = keylistFile.keys(); it.hasNext(); ) {
                    String key = it.next();
                    KeyModel keymodel = new KeyModel(key.charAt(0),
                        keylistFile.getJSONObject(key).getString("name"),
                        Integer.parseInt(keylistFile.getJSONObject(key).getString("keycode"),16),
                        keylistFile.getJSONObject(key).getJSONObject("modifier").getString("ctrl"),
                        keylistFile.getJSONObject(key).getJSONObject("modifier").getString("shift"),
                        keylistFile.getJSONObject(key).getJSONObject("modifier").getString("alt"),
                        keylistFile.getJSONObject(key).getJSONObject("modifier").getString("meta")
                    );
                    keys.add(keymodel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
}