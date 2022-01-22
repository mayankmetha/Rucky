package com.mayank.rucky.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mayank.rucky.R;
import com.mayank.rucky.models.KeyModel;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.KeyAdapter;

import org.apache.commons.io.IOUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class KeylistActivity extends AppCompatActivity {

    Config config;
    ListView keylist;
    ArrayList<KeyModel> keys;
    KeyAdapter adapter;
    File file;
    JSONObject json;
    JSONObject mapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        config = new Config(this);
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_keylist);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if(config.getHIDIntent().isEmpty() || config.getHIDIntent().equals(""))
            finish();
        toolbarTitle.setText(config.getHIDIntent());

        keys = new ArrayList<>();
        if(config.getHIDFile().isEmpty() || config.getHIDFile().equals(""))
            finish();
        file = new File(getExternalFilesDir("keymap"), config.getHIDFile());

        keylist = findViewById(R.id.key_list);
        adapter = new KeyAdapter(keys, this);
        keylist.setAdapter(adapter);
        refreshList();

        Button refreshBtn = findViewById(R.id.refresh_keymap_btn);
        refreshBtn.setFilterTouchesWhenObscured(true);
        refreshBtn.setOnClickListener(v -> refreshList());

        Button newKeyBtn = findViewById(R.id.add_keymap_btn);
        newKeyBtn.setFilterTouchesWhenObscured(true);
        newKeyBtn.setOnClickListener(v -> keyDetailDialog(null));

        keylist.setOnItemClickListener((parent, view, position, id) -> {
            KeyModel model = Objects.requireNonNull(adapter.getItem(position));
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.key_details);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.hid_delete, (dialog, which) -> {
                deleteKeyDetails(model);
                dialog.cancel();
            });
            builder.setNeutralButton(R.string.hid_edit, (dialog, which) -> {
                keyDetailDialog(model);
                dialog.cancel();
            });
            builder.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        refreshList();
    }

    void refreshList() {
        keylist.removeAllViewsInLayout();
        keys.clear();
        try {
            String jsonStr = jsonRead(file);
            if(!jsonStr.isEmpty()) {
                json = new JSONObject(jsonStr);
                mapping = json.getJSONObject("mapping");
                for (Iterator<String> it = mapping.keys(); it.hasNext(); ) {
                    String key = it.next();
                    KeyModel keymodel = new KeyModel(key.charAt(0),
                        mapping.getJSONObject(key).getString("name"),
                        Integer.parseInt(mapping.getJSONObject(key).getString("keycode"),16),
                        mapping.getJSONObject(key).getJSONObject("modifier").getString("ctrl"),
                        mapping.getJSONObject(key).getJSONObject("modifier").getString("shift"),
                        mapping.getJSONObject(key).getJSONObject("modifier").getString("alt"),
                        mapping.getJSONObject(key).getJSONObject("modifier").getString("meta")
                    );
                    keys.add(keymodel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    void deleteKeyDetails(KeyModel model) {
        mapping.remove(String.valueOf(model.getKey()));
        json.remove("mapping");
        try {
            json.put("mapping",mapping);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonWrite(json.toString());
        adapter.remove(model);
        adapter.notifyDataSetChanged();
        refreshList();
    }

    void keyDetailDialog(KeyModel model) {
        AtomicInteger ctrl = new AtomicInteger(0);
        AtomicInteger alt = new AtomicInteger(0);
        AtomicInteger shift = new AtomicInteger(0);
        AtomicInteger meta = new AtomicInteger(0);
        AtomicInteger keyCode = new AtomicInteger(0);
        AtomicReference<String> keyString = new AtomicReference<>("");
        AtomicReference<Character> key = new AtomicReference<>('\0');
        MaterialAlertDialogBuilder keyDialog = new MaterialAlertDialogBuilder(KeylistActivity.this);
        keyDialog.setTitle(R.string.key_details);
        LayoutInflater keyLI = LayoutInflater.from(this);
        final View keyDetailView = keyLI.inflate(R.layout.key_details, null);
        keyDialog.setView(keyDetailView);
        keyDialog.setCancelable(false);
        EditText keyTitle = keyDetailView.findViewById(R.id.key_title_edittext);
        EditText keyCharacter = keyDetailView.findViewById(R.id.key_character_edittext);
        EditText keyCodeString = keyDetailView.findViewById(R.id.key_code_edittext);
        ChipGroup ctrlKey = keyDetailView.findViewById(R.id.key_ctrl_options);
        ChipGroup altKey = keyDetailView.findViewById(R.id.key_alt_options);
        ChipGroup shiftKey = keyDetailView.findViewById(R.id.key_shift_options);
        ChipGroup metaKey = keyDetailView.findViewById(R.id.key_meta_options);

        if(model != null)  {
            keyTitle.setText(model.getKeyName().trim());
            keyCharacter.setText(String.valueOf(model.getKey()));
            keyCodeString.setText(String.format("%02X",model.getKeycode()));
        }

        Chip checked = null;
        boolean checkedFlag = false;
        for(int i = ctrlKey.getChildCount()-1; i >=0 ; i--) {
            checked = (Chip) ctrlKey.getChildAt(i);
            if(model != null && model.getCtrl(model.getModifier()).equalsIgnoreCase(checked.getText().toString())) {
                checkedFlag = true;
                checked.setChecked(true);
                ctrl.set(model.getCtrlInt(model.getModifier()));
            }
        }
        if(!checkedFlag) {
            assert checked != null;
            checked.setChecked(true);
        }
        checkedFlag = false;
        for(int i = altKey.getChildCount()-1; i >=0 ; i--) {
            checked = (Chip) altKey.getChildAt(i);
            if(model != null && model.getAlt(model.getModifier()).equalsIgnoreCase(checked.getText().toString())) {
                checkedFlag = true;
                checked.setChecked(true);
                alt.set(model.getAltInt(model.getModifier()));
            }
        }
        if(!checkedFlag) {
            assert checked != null;
            checked.setChecked(true);
        }
        checkedFlag = false;
        for(int i = shiftKey.getChildCount()-1; i >=0 ; i--) {
            checked = (Chip) shiftKey.getChildAt(i);
            if(model != null && model.getShift(model.getModifier()).equalsIgnoreCase(checked.getText().toString())) {
                checkedFlag = true;
                checked.setChecked(true);
                shift.set(model.getShiftInt(model.getModifier()));
            }
        }
        if(!checkedFlag) {
            assert checked != null;
            checked.setChecked(true);
        }
        checkedFlag = false;
        for(int i = metaKey.getChildCount()-1; i >=0 ; i--) {
            checked = (Chip) metaKey.getChildAt(i);
            if(model != null && model.getMeta(model.getModifier()).equalsIgnoreCase(checked.getText().toString())) {
                checkedFlag = true;
                checked.setChecked(true);
                meta.set(model.getMetaInt(model.getModifier()));
            }
        }
        if(!checkedFlag) {
            assert checked != null;
            checked.setChecked(true);
        }

        ctrlKey.setOnCheckedChangeListener((view, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if (chip != null) {
                if(chip.getText().toString().equals(getResources().getString(R.string.key_no))) ctrl.set(0);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_left))) ctrl.set(-1);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_right))) ctrl.set(1);
            }
        });

        altKey.setOnCheckedChangeListener((view, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if (chip != null) {
                if(chip.getText().toString().equals(getResources().getString(R.string.key_no))) alt.set(0);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_left))) alt.set(-1);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_right))) alt.set(1);
            }
        });

        shiftKey.setOnCheckedChangeListener((view, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if (chip != null) {
                if(chip.getText().toString().equals(getResources().getString(R.string.key_no))) shift.set(0);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_left))) shift.set(-1);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_right))) shift.set(1);
            }
        });

        metaKey.setOnCheckedChangeListener((view, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if (chip != null) {
                if(chip.getText().toString().equals(getResources().getString(R.string.key_no))) meta.set(0);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_left))) meta.set(-1);
                else if(chip.getText().toString().equals(getResources().getString(R.string.key_right))) meta.set(1);
            }
        });

        keyDialog.setPositiveButton(R.string.saveBtn, (dialog, which) -> {
            keyString.set(keyTitle.getText().toString().isEmpty() ? UUID.randomUUID().toString() : keyTitle.getText().toString().trim());
            keyCode.set(keyCodeString.getText().toString().isEmpty() ? 0 : Integer.decode("0x"+keyCodeString.getText().toString()));
            key.set(keyCharacter.getText().toString().isEmpty() ? '\0' : keyCharacter.getText().toString().charAt(0));
            KeyModel keyModel = new KeyModel(key.get(), keyString.get(), keyCode.get(), ctrl.get(), shift.get(), alt.get(), meta.get());

            if(model != null) {
                keys.remove(model);
                mapping.remove(String.valueOf(model.getKey()));
            }

            if(keys.isEmpty()) {
                keys.add(keyModel);
                addToJson(keyModel);
            }
            else {
                boolean exist = false;
                for (int i = 0; i < keys.size(); i++) {
                    if (keyModel.exists(keys.get(i))) {
                        exist = true;
                    }
                }
                if (!exist) {
                    keys.add(keyModel);
                    addToJson(keyModel);
                }
            }
            refreshList();
        });
        keyDialog.setNegativeButton(R.string.btn_cancel, (dialog, which) -> dialog.cancel());
        keyDialog.show();
    }

    String jsonRead(File file) {
        FileInputStream fInputStream;
        InputStream inputStream;
        StringWriter writer = new StringWriter();
        try {
            fInputStream = new FileInputStream(file);
            inputStream = new BufferedInputStream(fInputStream);
            IOUtil.copy(inputStream, writer, "UTF-8");
            inputStream.close();
            fInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    void addToJson(KeyModel key) {
        JSONObject modifier = new JSONObject();
        JSONObject keymapping = new JSONObject();
        try {
            modifier.put("ctrl",key.getCtrl(key.getModifier()));
            modifier.put("shift",key.getShift(key.getModifier()));
            modifier.put("alt",key.getAlt(key.getModifier()));
            modifier.put("meta",key.getMeta(key.getModifier()));

            keymapping.put("name",key.getKeyName());
            keymapping.put("keycode",String.format("%02X",key.getKeycode()));
            keymapping.put("modifier",modifier);

            mapping.put("\\u" + Integer.toHexString(key.getKey() | 0x10000).substring(1), keymapping);
            json.remove("mapping");
            json.put("mapping",mapping);

            jsonWrite(new String(json.toString().getBytes(), StandardCharsets.US_ASCII).replace("\\\\u","\\u"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void jsonWrite(String str) {
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

}