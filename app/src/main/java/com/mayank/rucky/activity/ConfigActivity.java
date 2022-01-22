package com.mayank.rucky.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mayank.rucky.R;
import com.mayank.rucky.service.SocketHeartbeatService;
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
    public TextView statusText;
    public ImageView statusImage;
    public Button ipButton;

    public static Pattern SOCKET_ADDRESS;

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
        setContentView(R.layout.activity_config);

        statusText = findViewById(R.id.status_text);
        statusImage = findViewById(R.id.status_icon);
        statusImage.setFilterTouchesWhenObscured(true);
        ipButton = findViewById(R.id.ipBtn);
        ipButton.setFilterTouchesWhenObscured(true);

        ipButton.setText(config.getNetworkAddress());
        SOCKET_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                + "|[1-9][0-9]|[0-9])):([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])");

        updateStatus();
        language();
        mode();
        networkAddress();

        config.getAppSharedPreferences().registerOnSharedPreferenceChangeListener((sharedPreferences, s) -> {
            if (s.equals(Constants.PREF_UI_STATUS_TEXT) || s.equals(Constants.PREF_UI_STATUS_IMG))
                updateStatus();
        });
    }

    private void language() {
        Button langBtn = findViewById(R.id.hidBtn);
        langBtn.setFilterTouchesWhenObscured(true);
        ArrayList<String> languages;
        if(!config.getHIDCustomise()) {
            languages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hidLanguages)));
            langBtn.setText(languages.get(config.getHIDLanguage()));
            langBtn.setOnClickListener(view -> new MaterialAlertDialogBuilder(ConfigActivity.this)
                    .setCancelable(false)
                    .setSingleChoiceItems(getResources().getStringArray(R.array.hidLanguages), config.getHIDLanguage(), (dialog, i) -> {
                        config.setHIDLanguage(i);
                        dialog.dismiss();
                        langBtn.setText(languages.get(config.getHIDLanguage()));
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show());
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
            langBtn.setOnClickListener(view -> new MaterialAlertDialogBuilder(ConfigActivity.this)
                    .setCancelable(false)
                    .setSingleChoiceItems(languages.toArray(new CharSequence[0]), filename.indexOf(config.getHIDFileSelected()), (dialog, i) -> {
                        config.setHIDFileSelected(filename.get(i));
                        dialog.dismiss();
                        langBtn.setText(languages.get(i));
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show());
        }
    }

    private void mode() {
        ArrayList<String> modes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.modes)));
        Button modeBtn = findViewById(R.id.modeBtn);
        modeBtn.setFilterTouchesWhenObscured(true);
        modeBtn.setText(modes.get(config.getHIDMode()));
        modeBtn.setOnClickListener(view -> new MaterialAlertDialogBuilder(ConfigActivity.this)
                .setCancelable(false)
                .setSingleChoiceItems(getResources().getStringArray(R.array.modes), config.getHIDMode(), (dialog, i) -> {
                    config.setHIDMode(i);
                    dialog.dismiss();
                    modeBtn.setText(modes.get(config.getHIDMode()));
                    updateStatus();
                })
                .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                .show());
    }

    private void updateStatus() {
        if (config.getHIDMode() == 0) {
            config.setNetworkStatus(false);
            getApplicationContext().stopService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
            ipButton.setEnabled(false);
            if (config.getUSBStatus()) {
                config.setStatusTextRes(R.string.config_status_usb_on);
                config.setStatusImageRes(R.drawable.ic_usb);
            } else {
                config.setStatusTextRes(R.string.config_status_usb_off);
                config.setStatusImageRes(R.drawable.ic_usb_off);
            }
        } else if (config.getHIDMode() == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
            } else {
                getApplicationContext().startService(new Intent(getApplicationContext(), SocketHeartbeatService.class));
            }
            updateNotification();
            ipButton.setEnabled(true);
            if (config.getNetworkStatus()) {
                config.setStatusTextRes(R.string.config_status_net_on);
                config.setStatusImageRes(R.drawable.ic_net);
            } else {
                config.setStatusTextRes(R.string.config_status_net_off);
                config.setStatusImageRes(R.drawable.ic_net_off);
            }
            updateNotification();
        }
        statusImage.setImageDrawable(ContextCompat.getDrawable(this,config.getStatusImageRes()));
        statusText.setText(config.getStatusTextRes());
    }

    private void networkAddress() {
        ipButton.setOnClickListener(view -> {
            LayoutInflater socketLI = LayoutInflater.from(this);
            final View socketView = socketLI.inflate(R.layout.config_network, null);
            EditText address = socketView.findViewById(R.id.socket_title);
            address.setText(config.getNetworkAddress());
            new MaterialAlertDialogBuilder(ConfigActivity.this)
                    .setTitle(getResources().getString(R.string.socket_address))
                    .setView(socketView)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.btn_save), (dialog, which) -> {
                        Matcher matcher = SOCKET_ADDRESS.matcher(address.getText().toString());
                        if (matcher.matches())
                            config.setNetworkAddress(address.getText().toString());
                        ipButton.setText(config.getNetworkAddress());
                        updateNotification();
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel())
                    .show();
        });
    }

    private void updateNotification() {
        EditorActivity.serviceNotificationManager.notify(1, new NotificationCompat.Builder(getApplicationContext(), Constants.SCHANNEL_ID)
                .setContentTitle(getApplicationContext().getString(config.getStatusTextRes()))
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setGroup(Constants.PACKAGE_NAME)
                .setGroupSummary(true)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        PendingIntent.FLAG_IMMUTABLE))
                .setAutoCancel(false).build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
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

}