package com.mayank.rucky;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    public static final int LOCK_REQUEST_CODE = 221;
    public static final int SECURITY_SETTING_REQUEST_CODE = 233;

    public static final String PREF_SETTINGS = "settings";
    public static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    public static final String PREF_SETTINGS_LAUNCH_ICON = "launchIcon";
    public static final String PREF_SETTING_ADV_SECURITY = "advSecurity";
    public static final String PREF_DEV_USB = "usbConnectionState";
    public static Boolean darkTheme;
    public static Boolean launchIcon;
    public static Boolean advSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE);
        darkTheme = settings.getBoolean(PREF_SETTINGS_DARK_THEME,true);
        launchIcon = settings.getBoolean(PREF_SETTINGS_LAUNCH_ICON,false);
        advSecurity = settings.getBoolean(PREF_SETTING_ADV_SECURITY, false);
        setTheme(darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_container, new RootSettingsFragment()).commit();
        toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
    }
}