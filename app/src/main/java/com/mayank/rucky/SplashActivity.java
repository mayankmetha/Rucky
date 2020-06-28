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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.decode;

public class SplashActivity extends AppCompatActivity {

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RUCKY_KEYSTORE = "RuckyKeystore";
    private static final String RUCKY_KEYSTORE2 = "RuckyKeystore2";

    public static final String PREF_SETTINGS_INIT = "init";
    private static Boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        init = settings.getBoolean(PREF_SETTINGS_INIT,true);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        SettingsActivity.advSecurity = settings.getBoolean(SettingsActivity.PREF_SETTING_ADV_SECURITY,false);
        if(!init && SettingsActivity.advSecurity) biometric();
        else splash();
    }

    void launchNext() {
        if(!init) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, InitActivity.class));
        }
        finish();
    }

    void getSignature() {
        String NetHunter = "x2j+O+TND/jjH0ryjO/2ROPpjCvHoHK/XnjrgdAHJfM=";
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
            String k642 = Objects.requireNonNull(settings.getString(RUCKY_KEYSTORE2, null));
            byte[] tmp2 = decode(k642,Base64.DEFAULT);
            MainActivity.iv = new IvParameterSpec(cipher.doFinal(tmp2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void biometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finishAffinity();
                System.exit(0);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                splash();
                getKey();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                finishAffinity();
                System.exit(0);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getResources().getString(R.string.unlock))
                .setSubtitle(getResources().getString(R.string.auth))
                .setConfirmationRequired(false)
                .setDeviceCredentialAllowed(true)
                .build();
        biometricPrompt.authenticate(promptInfo);

    }

    private void splash() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(flags);
        setContentView(R.layout.activity_splash);
        ImageView i1 = findViewById(R.id.imageViewBG);
        i1.setImageDrawable(ContextCompat.getDrawable(this,SettingsActivity.darkTheme ? R.drawable.splash_background_dark : R.drawable.splash_background_light));
        FrameLayout l2= findViewById(R.id.splashTextView);
        i1.setAnimation(AnimationUtils.loadAnimation(this,R.anim.rotate));
        l2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.downtoup));
        new Handler().postDelayed(this::launchNext, 5000);
        getSignature();
    }
}