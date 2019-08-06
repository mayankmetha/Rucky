package com.mayank.rucky;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class SplashActivity extends AppCompatActivity {

    public static final String PREF_SETTINGS_INIT = "init";
    private static Boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        init = settings.getBoolean(PREF_SETTINGS_INIT,true);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_splash);
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        ImageView i = findViewById(R.id.imageViewFG);
        i.setColorFilter(getResources().getColor((SettingsActivity.darkTheme?R.color.pri_dark:R.color.pri_light)));
        FrameLayout l1 = findViewById(R.id.splashIcon);
        TextView l2= findViewById(R.id.splashTextView);
        l1.setAnimation(AnimationUtils.loadAnimation(this,R.anim.uptodown));
        l2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.downtoup));
        new Handler().postDelayed(this::launchNext, 5000);
        getSignature();
    }

    void launchNext() {
        if(!init) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, InitActivity.class));
            finish();
        }
    }

    void getSignature() {
        String NetHunter = "Fr3kIMwYhByAjtQM2hi+Zb8QJnzdvVej+P6j7o01PbY=";
        String GitHub = "0Xv/I6xP6Q1wKbIqCgXi4CafhKZtOZLOR575TiqN93s=";
        String debug = "zb2SIJ2H4C4ZUV6zGrGHgJiOnGzJBdxeA3rZomYZfcY=";
        String hash = "";
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA256");
                md.update(signature.toByteArray());
                hash = new String(Base64.encode(md.digest(), 0));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        if(hash.trim().equals(NetHunter)) {
            MainActivity.distro = R.string.releaseNetHunter;
            MainActivity.updateEnable = false;
        } else if(hash.trim().equals(GitHub)) {
            MainActivity.distro = R.string.releaseGitHub;
            MainActivity.updateEnable = true;
        } else if(hash.trim().equals(debug)) {
            MainActivity.distro = R.string.releaseTest;
            MainActivity.updateEnable = true;
        } else {
            MainActivity.distro = R.string.releaseOthers;
            MainActivity.updateEnable = false;
        }
    }
}
