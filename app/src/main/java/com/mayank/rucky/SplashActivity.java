package com.mayank.rucky;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.decode;

public class SplashActivity extends AppCompatActivity {

    public static final int LOCK_REQUEST_CODE = 221;
    public static final int SECURITY_SETTING_REQUEST_CODE = 233;

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RUCKY_KEYSTORE = "RuckyKeystore";

    public static final String PREF_SETTINGS_INIT = "init";
    private static Boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        init = settings.getBoolean(PREF_SETTINGS_INIT,true);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        SettingsActivity.advSecurity = settings.getBoolean(SettingsActivity.PREF_SETTING_ADV_SECURITY,false);
        if(!init && SettingsActivity.advSecurity) authenticate();
        else splash();
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
        String debug = "im5KgLli2rx4iEvMVXotXGpfiR1/eqXEwBO2YQ6uP70=";
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

    void getKey() {
        try{
            final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(KEYSTORE_PROVIDER_ANDROID_KEYSTORE,null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String k64 = Objects.requireNonNull(settings.getString(RUCKY_KEYSTORE, null));
            byte[] tmp = decode(k64, Base64.DEFAULT);
            MainActivity.key = new SecretKeySpec(cipher.doFinal(tmp),"AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authenticate() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assert keyguardManager != null;
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Unlock","Comfirm authentication");
            try {
                startActivityForResult(i, SettingsActivity.LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SettingsActivity.SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode,resCode,data);
        switch (reqCode) {
            case LOCK_REQUEST_CODE:
                if (resCode == RESULT_OK) {
                    splash();
                    getKey();
                } else {
                    finishAffinity();
                    System.exit(0);
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                if(isDeviceSecure()) {
                    authenticate();
                }
                break;
        }
    }

    private Boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        assert keyguardManager != null;
        return keyguardManager.isKeyguardSecure();
    }

    private void splash() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(flags);
        setContentView(R.layout.activity_splash);
        ImageView i = findViewById(R.id.imageViewFG);
        i.setColorFilter(getResources().getColor((SettingsActivity.darkTheme?R.color.pri_dark:R.color.pri_light)));
        FrameLayout l1 = findViewById(R.id.splashIcon);
        TextView l2= findViewById(R.id.splashTextView);
        l1.setAnimation(AnimationUtils.loadAnimation(this,R.anim.uptodown));
        l2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.downtoup));
        new Handler().postDelayed(this::launchNext, 5000);
        getSignature();
    }
}