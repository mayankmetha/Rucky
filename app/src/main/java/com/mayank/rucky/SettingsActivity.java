package com.mayank.rucky;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_SETTINGS = "settings";
    public static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    public static final String PREF_SETTINGS_LAUNCH_ICON = "launchIcon";
    public static Boolean darkTheme;
    public static Boolean launchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE);
        darkTheme = settings.getBoolean(PREF_SETTINGS_DARK_THEME,true);
        launchIcon = settings.getBoolean(PREF_SETTINGS_LAUNCH_ICON,false);
        setTheme(darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_container, new RootSettingsFragment()).commit();
        toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}