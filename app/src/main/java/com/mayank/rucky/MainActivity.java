package com.mayank.rucky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static double currentVersion;
    public static double newVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME,false);
        theme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null) {
            try {
                Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        theme();
        if(didThemeChange) {
            didThemeChange = false;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    void theme() {
        if(SettingsActivity.darkTheme) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
    }

    void checkUpdate() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/mayankmetha/Rucky/blob/master/release/version");
                new fetchVersion().execute(url);
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private static class fetchVersion extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String str = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                str = in.readLine();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
        @Override
        protected void onPostExecute(String result) {
            newVersion = Double.parseDouble(result);
        }
    }

    void updater() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(currentVersion<newVersion) {
            //TODO:update
        } else {
            //TODO:no update
        }
    }
}
