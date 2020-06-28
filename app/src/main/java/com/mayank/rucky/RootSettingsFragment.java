package com.mayank.rucky;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Base64.encodeToString;

public class RootSettingsFragment extends PreferenceFragmentCompat {

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RUCKY_KEYSTORE = "RuckyKeystore";
    private static final String RUCKY_KEYSTORE2 = "RuckyKeystore2";

    private static final String PREF_SETTINGS = "settings";
    private static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    private static final String PREF_SETTINGS_LAUNCH_ICON = "launchIcon";
    private static final String PREF_SETTING_ADV_SECURITY = "advSecurity";
    private static final String PREF_GEN_KEY = "genKeyDone";
    private static final String PREF_DEV_USB = "usbConnectionState";
    private static final String PREF_DEV_RPI = "rpiConnectionState";
    private static double currentVersion;
    private static String webViewID = "WEBVIEW_URL";
    private static String activityTitle = "WEBVIEW_TITLE";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        final SharedPreferences settings = this.requireActivity().getSharedPreferences(PREF_SETTINGS,MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, rootKey);
        final SwitchPreferenceCompat darkThemeSwitch = findPreference("theme");
        assert darkThemeSwitch != null;
        darkThemeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_SETTINGS_DARK_THEME,switched).apply();
            if(!SettingsActivity.launchIcon) {
                if(switched) {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                } else {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                }
            }
            MainActivity.didThemeChange = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(getActivity(), TransparentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            requireActivity().finish();
            return true;
        });

        final SwitchPreferenceCompat launchIconSwitch = findPreference("icon");
        assert launchIconSwitch != null;
        launchIconSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_SETTINGS_LAUNCH_ICON,switched).apply();
            if(SettingsActivity.darkTheme) {
                if(switched) {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                } else {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Dark"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                }
            } else {
                if(switched) {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                } else {
                    requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky","com.mayank.rucky.Light"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(getActivity(), TransparentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            requireActivity().finish();
            return true;
        });

        final SwitchPreferenceCompat securitySwitch = findPreference("sec");
        assert securitySwitch != null;
        securitySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            if(!settings.getBoolean(PREF_GEN_KEY,false)) {
                try {
                    SecureRandom secRnd = new SecureRandom();
                    IvParameterSpec iv = new IvParameterSpec(secRnd.generateSeed(16));
                    KeyGenerator keygen = KeyGenerator.getInstance("AES");
                    keygen.init(256);
                    SecretKey key = keygen.generateKey();
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA,KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                    kpg.initialize(new KeyGenParameterSpec.Builder(KEYSTORE_PROVIDER_ANDROID_KEYSTORE, KeyProperties.PURPOSE_DECRYPT|KeyProperties.PURPOSE_ENCRYPT)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB).build());
                    KeyPair kp;
                    kp = kpg.generateKeyPair();
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
                    editor.putString(RUCKY_KEYSTORE2,encodeToString(cipher.doFinal(iv.getIV()), Base64.DEFAULT)).apply();
                    editor.putString(RUCKY_KEYSTORE,encodeToString(cipher.doFinal(key.getEncoded()), Base64.DEFAULT)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            editor.putBoolean(PREF_GEN_KEY,true).apply();
            editor.putBoolean(PREF_SETTING_ADV_SECURITY,switched).apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(getActivity(), TransparentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            requireActivity().finish();
            return true;
        });

        Preference depPreference = findPreference("uninstall");
        assert depPreference != null;
        depPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:com.mayank.rucky"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            requireActivity().finish();
            startActivity(intent);
            return true;
        });

        final SwitchPreferenceCompat usb = findPreference("usb");
        assert usb != null;
        usb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_DEV_USB, switched).apply();
            MainActivity.usbConnected = switched;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(getActivity(), TransparentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            requireActivity().finish();
            return true;
        });

        final SwitchPreferenceCompat rpi = findPreference("rpi");
        assert rpi != null;
        rpi.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_DEV_RPI, switched).apply();
            MainActivity.piConnected = switched;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(getActivity(), TransparentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            requireActivity().finish();
            return true;
        });

        Preference developerPreference = findPreference("developer");
        assert developerPreference != null;
        developerPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, getResources().getString(R.string.setting_developer_title));
            intent.putExtra(webViewID, "https://mayankmetha.github.io");
            startActivity(intent);
            return true;
        });

        Preference versionPreference = findPreference("version");
        assert versionPreference != null;
        try {
            PackageInfo pInfo = this.requireActivity().getPackageManager().getPackageInfo(this.requireActivity().getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionPreference.setSummary(Double.toString(currentVersion));

        String currentArch = Build.SUPPORTED_ABIS[0];
        Preference archPreference = findPreference("arch");
        assert archPreference != null;
        archPreference.setSummary(currentArch);

        Preference distributionPreference = findPreference("source");
        assert distributionPreference != null;
        distributionPreference.setSummary(MainActivity.distro);
        if(MainActivity.distro == R.string.releaseGitHub || MainActivity.distro == R.string.releaseTest) {
            distributionPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(activityTitle, getResources().getString(R.string.releaseGitHub));
                intent.putExtra(webViewID, "https://github.com/mayankmetha/Rucky/releases/latest");
                startActivity(intent);
                return true;
            });
        }

        Preference licencePreference = findPreference("lic");
        assert licencePreference != null;
        licencePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, getResources().getString(R.string.settings_lic_title));
            intent.putExtra(webViewID, "https://raw.githubusercontent.com/mayankmetha/Rucky/master/LICENSE");
            startActivity(intent);
            return true;
        });

        Preference gitPreference = findPreference("git");
        assert gitPreference != null;
        gitPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, getResources().getString(R.string.settings_git_title));
            intent.putExtra(webViewID, "https://github.com/mayankmetha/Rucky/issues");
            startActivity(intent);
            return true;
        });

        Preference localePreference = findPreference("locale");
        assert localePreference != null;
        localePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(activityTitle, getResources().getString(R.string.settings_locale_title));
            intent.putExtra(webViewID, "https://mayankmetha.github.io/Rucky/");
            startActivity(intent);
            return true;
        });
    }

}
