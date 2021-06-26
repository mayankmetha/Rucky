package com.mayank.rucky.fragment;

import static android.util.Base64.encodeToString;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mayank.rucky.R;
import com.mayank.rucky.activity.BrowserActivity;
import com.mayank.rucky.activity.EditorActivity;
import com.mayank.rucky.activity.HidActivity;
import com.mayank.rucky.activity.SplashActivity;
import com.mayank.rucky.utils.ColorAdapter;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Config config;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        config = new Config(this.requireContext());
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        this.requireActivity().setTheme(Constants.themeList[config.getAccentTheme()]);

        darkThemeSetting();
        accentColorSetting();
        hideLauncherIcon();
        security();
        cleanup();

        hidSettings();
        usb();
        net();

        developer();
        version();
        arch();
        source();
        license();

        gitIssue();
        translate();
    }

    private void darkThemeSetting() {
        final SwitchPreference themeSwitch = findPreference("theme");
        assert themeSwitch != null;
        themeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setDarkMode(switched);
            restartActivity();
            return true;
        });
    }

    private void accentColorSetting() {
        final Preference accentPreference = findPreference("accent");
        assert accentPreference != null;
        accentPreference.setOnPreferenceClickListener(preference -> {
            int[] colors = getResources().getIntArray(R.array.colors);
            LayoutInflater colorLI = LayoutInflater.from(this.requireActivity());
            RecyclerView view = (RecyclerView) colorLI.inflate(R.layout.color_grid, null);
            ColorAdapter adapter = new ColorAdapter(colors, this.requireActivity());
            adapter.onItemClicked((position, v) -> {
                config.setAccentTheme(position);
                adapter.updateSelection(position);
                ImageView selectedButton = v.findViewById(R.id.color_button);
                selectedButton.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), R.drawable.color_button_selected));
                selectedButton.setColorFilter(colors[position]);
            });
            int gridRowSize = 5;
            view.setAdapter(adapter);
            GridLayoutManager grids = new GridLayoutManager(this.requireActivity(),gridRowSize);
            view.setLayoutManager(grids);
            view.setForegroundGravity(Gravity.CENTER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
            builder.setCancelable(false);
            builder.setView(view);
            builder.setTitle(R.string.accent_theme_title);
            builder.setPositiveButton(getResources().getString(R.string.btn_select), (dialog, which) -> restartActivity());
            builder.setNegativeButton(getResources().getString(R.string.btn_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            dialog.show();
            return true;
        });
    }

    private void hideLauncherIcon() {
        final SwitchPreference iconSwitch = findPreference("icon");
        assert iconSwitch != null;
        iconSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setIcon(switched);
            if (switched) {
                requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky", "com.mayank.rucky.Main"),PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky", "com.mayank.rucky.Main"),PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            return true;
        });
    }

    private void security() {
        final SwitchPreference securitySwitch = findPreference("sec");
        assert securitySwitch != null;
        securitySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            if(!config.getKey()) {
                try {
                    SecureRandom secRnd = new SecureRandom();
                    IvParameterSpec iv = new IvParameterSpec(secRnd.generateSeed(16));
                    KeyGenerator keygen = KeyGenerator.getInstance("AES");
                    keygen.init(256);
                    SecretKey key = keygen.generateKey();
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA,Constants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                    kpg.initialize(new KeyGenParameterSpec.Builder(Constants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE, KeyProperties.PURPOSE_DECRYPT|KeyProperties.PURPOSE_ENCRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setBlockModes(KeyProperties.BLOCK_MODE_ECB).build());
                    KeyPair kp;
                    kp = kpg.generateKeyPair();
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
                    config.setKeyStore2(encodeToString(cipher.doFinal(iv.getIV()), Base64.DEFAULT));
                    config.setKeyStore1(encodeToString(cipher.doFinal(key.getEncoded()), Base64.DEFAULT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                config.setKey(true);
            }
            config.setSec(switched);
            restartActivity();
            return true;
        });
    }

    private void cleanup() {
        final Preference depPreference = findPreference("uninstall");
        assert depPreference != null;
        depPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:com.mayank.rucky"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            return true;
        });
    }

    private void hidSettings() {
        final Preference hidPreference = findPreference("hid");
        assert hidPreference != null;
        hidPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), HidActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void usb() {
        final SwitchPreference usb = findPreference("usb");
        assert usb != null;
        usb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setUSB(switched);
            restartActivity();
            return true;
        });
    }

    private void net() {
        final SwitchPreference net = findPreference("net");
        assert net != null;
        net.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setNet(switched);
            restartActivity();
            return true;
        });
    }

    private void developer() {
        final Preference developerPreference = findPreference("developer");
        assert developerPreference != null;
        developerPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.setting_developer_title));
            intent.putExtra(Constants.webViewID, "https://mayankmetha.github.io");
            startActivity(intent);
            return true;
        });
    }

    private void version() {
        final Preference versionPreference = findPreference("version");
        assert versionPreference != null;
        double currentVersion = 0.0;
        int currentVersionCode = 0;
        try {
            PackageInfo pInfo = this.requireActivity().getPackageManager().getPackageInfo(this.requireActivity().getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionPreference.setSummary(currentVersion +" ("+ currentVersionCode +")");
    }

    private void arch() {
        String currentArch = Build.SUPPORTED_ABIS[0];
        final Preference archPreference = findPreference("arch");
        assert archPreference != null;
        archPreference.setSummary(currentArch);
    }

    @SuppressLint("NonConstantResourceId")
    private void source() {
        final Preference distributionPreference = findPreference("source");
        assert distributionPreference != null;
        distributionPreference.setSummary(EditorActivity.distro);
        String title = "";
        String url = "";
        boolean canIntent = true;
        switch (EditorActivity.distro) {
            case R.string.releaseGitHub:
                title = getResources().getString(R.string.releaseGitHub);
                url = "https://mayankmetha.github.io/Rucky/";
                break;
            case R.string.releaseGitHubNightly:
                title = getResources().getString(R.string.releaseGitHubNightly);
                url = "https://mayankmetha.github.io/Rucky/";
                break;
            case R.string.releaseTest:
                title = getResources().getString(R.string.releaseTest);
                url = "https://mayankmetha.github.io/Rucky/";
                break;
            case R.string.releaseNetHunter:
                title = getResources().getString(R.string.releaseNetHunter);
                url = "https://store.nethunter.com/en/packages/com.mayank.rucky/";
                break;
            default:
                canIntent = false;
        }
        if(canIntent) {
            String finalTitle = title;
            String finalUrl = url;
            distributionPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(Constants.activityTitle, finalTitle);
                intent.putExtra(Constants.webViewID, finalUrl);
                startActivity(intent);
                return true;
            });
        }
    }

    private void license() {
        final Preference licencePreference = findPreference("lic");
        assert licencePreference != null;
        licencePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_lic_title));
            intent.putExtra(Constants.webViewID, "https://raw.githubusercontent.com/mayankmetha/Rucky/master/LICENSE");
            startActivity(intent);
            return true;
        });
    }

    private void gitIssue() {
        Preference gitPreference = findPreference("git");
        assert gitPreference != null;
        gitPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_git_title));
            intent.putExtra(Constants.webViewID, "https://github.com/mayankmetha/Rucky/issues");
            startActivity(intent);
            return true;
        });
    }

    private void translate() {
        final Preference localePreference = findPreference("locale");
        assert localePreference != null;
        localePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_locale_title));
            intent.putExtra(Constants.webViewID,"https://crwd.in/rucky");
            startActivity(intent);
            return true;
        });
    }

    private void restartActivity() {
        Intent restartIntent = new Intent(this.getActivity(), SplashActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.requireActivity().finish();
        startActivity(restartIntent);
    }

}