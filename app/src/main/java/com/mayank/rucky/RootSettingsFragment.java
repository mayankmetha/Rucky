package com.mayank.rucky;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Base64.encodeToString;

public class RootSettingsFragment extends PreferenceFragmentCompat {

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RUCKY_KEYSTORE = "RuckyKeystore";

    private static final String PREF_SETTINGS = "settings";
    private static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    private static final String PREF_SETTINGS_LAUNCH_ICON = "launchIcon";
    private static final String PREF_SETTING_ADV_SECURITY = "advSecurity";
    private static final String PREF_GEN_KEY = "genKeyDone";
    private static double currentVersion;
    private static String webViewID = "WEBVIEW_URL";
    private static String activityTitle = "WEBVIEW_TITLE";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        final SharedPreferences settings = Objects.requireNonNull(this.getActivity()).getSharedPreferences(PREF_SETTINGS,MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, rootKey);
        final SwitchPreferenceCompat darkThemeSwitch = findPreference("theme");
        assert darkThemeSwitch != null;
        darkThemeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_SETTINGS_DARK_THEME,switched).apply();
            if(!SettingsActivity.launchIcon) {
                if(switched) {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                } else {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                }
            }
            MainActivity.didThemeChange = true;
            getActivity().finish();
            startActivity(getActivity().getIntent());
            return true;
        });

        final SwitchPreferenceCompat launchIconSwitch = findPreference("icon");
        assert launchIconSwitch != null;
        launchIconSwitch.setOnPreferenceChangeListener(((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_SETTINGS_LAUNCH_ICON,switched).apply();
            if(SettingsActivity.darkTheme) {
                if(switched) {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                } else {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                }
            } else {
                if(switched) {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                } else {
                    getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                }
            }
            getActivity().finish();
            startActivity(getActivity().getIntent());
            return true;
        }));

        final SwitchPreferenceCompat securitySwitch = findPreference("sec");
        assert  securitySwitch != null;
        securitySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            if(!settings.getBoolean(PREF_GEN_KEY,false)) {
                try {
                    KeyGenerator keygen = KeyGenerator.getInstance("AES");
                    keygen.init(128);
                    SecretKey key = keygen.generateKey();
                    Calendar start = new GregorianCalendar();
                    Calendar stop = new GregorianCalendar();
                    stop.add(Calendar.YEAR,25);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(Objects.requireNonNull(getContext()))
                            .setKeySize(2048)
                            .setAlias(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                            .setSubject(new X500Principal("CN="+KEYSTORE_PROVIDER_ANDROID_KEYSTORE))
                            .setSerialNumber(BigInteger.ZERO)
                            .setStartDate(start.getTime()).setEndDate(stop.getTime()).build();
                    KeyPairGenerator keyPairGenerator;
                    KeyPair kp;
                    keyPairGenerator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                    keyPairGenerator.initialize(spec);
                    kp = keyPairGenerator.generateKeyPair();
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
                    editor.putString(RUCKY_KEYSTORE,encodeToString(cipher.doFinal(key.getEncoded()), Base64.DEFAULT)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            editor.putBoolean(PREF_GEN_KEY,true).apply();
            editor.putBoolean(PREF_SETTING_ADV_SECURITY,switched).apply();
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().finishAffinity();
            startActivity(intent);
            return true;
        });

        Preference depPreference = findPreference("uninstall");
        assert depPreference != null;
        depPreference.setOnPreferenceClickListener(preference -> {
            try {
                MainActivity.dos.writeBytes("rm -rf /data/local/tmp/rucky-hid\n");
                MainActivity.dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:com.mayank.rucky"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().finish();
            startActivity(intent);
            return true;
        });

        Preference developerPreference = findPreference("developer");
        assert developerPreference != null;
        developerPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, "Developer");
            intent.putExtra(webViewID, "https://mayankmetha.github.io");
            startActivity(intent);
            return true;
        });

        Preference versionPreference = findPreference("version");
        assert versionPreference != null;
        try {
            PackageInfo pInfo = Objects.requireNonNull(this.getActivity()).getPackageManager().getPackageInfo(Objects.requireNonNull(this.getActivity()).getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionPreference.setSummary(Double.toString(currentVersion));

        String currentArch = Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP?Build.SUPPORTED_ABIS[0]:Build.CPU_ABI;
        Preference archPreference = findPreference("arch");
        assert archPreference != null;
        archPreference.setSummary(currentArch);

        Preference distributionPreference = findPreference("source");
        assert distributionPreference != null;
        distributionPreference.setSummary(MainActivity.distro);
        if(MainActivity.distro == R.string.releaseGitHub || MainActivity.distro == R.string.releaseTest) {
            distributionPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(activityTitle, "GitHub Release");
                intent.putExtra(webViewID, "https://github.com/mayankmetha/Rucky/releases/latest");
                startActivity(intent);
                return true;
            });
        }

        Preference licencePreference = findPreference("lic");
        assert licencePreference != null;
        licencePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, "License");
            intent.putExtra(webViewID, "https://raw.githubusercontent.com/mayankmetha/Rucky/master/LICENSE");
            startActivity(intent);
            return true;
        });

        Preference localePreference = findPreference("locale");
        assert localePreference != null;
        localePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, "Localization");
            intent.putExtra(webViewID, "https://mayankmetha.github.io/Rucky/");
            startActivity(intent);
            return true;
        });

    }
}
