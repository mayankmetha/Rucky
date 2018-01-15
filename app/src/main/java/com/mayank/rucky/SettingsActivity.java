package com.mayank.rucky;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_SETTINGS = "settings";
    public static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    public static Boolean darkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE);
        darkTheme = settings.getBoolean(PREF_SETTINGS_DARK_THEME,false);
        theme();
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        Switch themeSwitch = findViewById(R.id.darkThemeSwitch);
        themeSwitch.setChecked(darkTheme);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                darkTheme = isChecked;
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREF_SETTINGS_DARK_THEME, darkTheme).apply();
                MainActivity.didThemeChange = true;
            }
        });
        String version = "Version: ";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            version = version+pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView versionText = findViewById(R.id.Version);
        versionText.setText(version);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        theme();
    }

    void theme() {
        if(darkTheme) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
    }
}