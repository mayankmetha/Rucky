package com.mayank.rucky.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.mayank.rucky.R;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigActivity extends AppCompatActivity {

    Config config;
    @SuppressLint("StaticFieldLeak")
    public static TextView statusText;
    @SuppressLint("StaticFieldLeak")
    public static ImageView statusImage;
    public Button ipButton;
    public View ipStatusDivider;

    public static Pattern SOCKET_ADDRESS;

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
        setContentView(R.layout.activity_config);

        statusText = findViewById(R.id.status_text);
        statusImage = findViewById(R.id.status_icon);
        ipButton = findViewById(R.id.ipBtn);
        ipStatusDivider = findViewById(R.id.divider_config3);

        ipButton.setText(config.getNetworkAddress());
        SOCKET_ADDRESS = Pattern.compile(Patterns.IP_ADDRESS+":"+"([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])");

        updateStatus();
        language();
        mode();
        networkAddress();
    }

    private void language() {
        Button langBtn = findViewById(R.id.hidBtn);
        ArrayList<String> languages;
        if(!config.getHIDCustomise()) {
            languages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hidLanguages)));
            langBtn.setText(languages.get(config.getHIDLanguage()));
            langBtn.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
                builder.setCancelable(false);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.hidLanguages), config.getHIDLanguage(), (dialog, i) -> {
                    config.setHIDLanguage(i);
                    dialog.dismiss();
                    langBtn.setText(languages.get(config.getHIDLanguage()));
                });
                builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
                AlertDialog hidDialog = builder.create();
                Objects.requireNonNull(hidDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                hidDialog.show();
            });
        } else {
            ArrayList<String> filename = new ArrayList<>();
            languages = new ArrayList<>();
            final File[] tmp = Objects.requireNonNull(this.getExternalFilesDir("keymap")).listFiles();
            assert tmp != null;
            for(File file: tmp) {
                if(file.getPath().endsWith(".json"))
                    filename.add(file.getName());
            }
            for(int i = 0; i < filename.size(); i++) {
                languages.add(filename.get(i).replace(".json", "").replace("_"," ").toUpperCase());
            }
            langBtn.setText(languages.get(filename.indexOf(config.getHIDFileSelected())));
            langBtn.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
                builder.setCancelable(false);
                builder.setSingleChoiceItems(languages.toArray(new CharSequence[0]), filename.indexOf(config.getHIDFileSelected()), (dialog, i) -> {
                    config.setHIDFileSelected(filename.get(i));
                    dialog.dismiss();
                    langBtn.setText(languages.get(i));
                });
                builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
                AlertDialog hidDialog = builder.create();
                Objects.requireNonNull(hidDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                hidDialog.show();
            });
        }
    }

    private void mode() {
        ArrayList<String> modes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.modes)));
        Button modeBtn = findViewById(R.id.modeBtn);
        modeBtn.setText(modes.get(config.getHIDMode()));
        modeBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
            builder.setCancelable(false);
            builder.setSingleChoiceItems(getResources().getStringArray(R.array.modes), config.getHIDMode(), (dialog, i) -> {
                config.setHIDMode(i);
                dialog.dismiss();
                modeBtn.setText(modes.get(config.getHIDMode()));
                updateStatus();
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog modeDialog = builder.create();
            Objects.requireNonNull(modeDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            modeDialog.show();
        });
    }

    private void updateStatus() {
        if (config.getHIDMode() == 0) {
            config.setNetworkStatus(false);
            EditorActivity.stopNetworkSocketService(this);
            ipButton.setVisibility(View.GONE);
            ipStatusDivider.setVisibility(View.GONE);
            if (config.getUSBStatus()) {
                statusText.setText(R.string.config_status_usb_on);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_usb));
            } else {
                statusText.setText(R.string.config_status_usb_off);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_usb_off));
            }
        } else if (config.getHIDMode() == 1) {
            EditorActivity.startNetworkSocketService(this);
            ipButton.setVisibility(View.VISIBLE);
            ipStatusDivider.setVisibility(View.VISIBLE);
            if (config.getNetworkStatus()) {
                statusText.setText(R.string.config_status_net_on);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_net));
            } else {
                statusText.setText(R.string.config_status_net_off);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_net_off));
            }
            EditorActivity.updateNotification(this);
        }
    }

    private void networkAddress() {
        ipButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
            builder.setTitle(getResources().getString(R.string.socket_address));
            LayoutInflater socketLI = LayoutInflater.from(this);
            final View socketView = socketLI.inflate(R.layout.config_network, null);
            builder.setView(socketView);
            EditText address = socketView.findViewById(R.id.socket_title);
            address.setText(config.getNetworkAddress());
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
                Matcher matcher = SOCKET_ADDRESS.matcher(address.getText().toString());
                if (matcher.matches())
                    config.setNetworkAddress(address.getText().toString());
                ipButton.setText(config.getNetworkAddress());
                EditorActivity.updateNotification(this);
            });
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog saveDialog = builder.create();
            Objects.requireNonNull(saveDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            saveDialog.show();
        });
    }

}