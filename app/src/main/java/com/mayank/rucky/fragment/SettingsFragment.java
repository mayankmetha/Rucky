package com.mayank.rucky.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.mayank.rucky.activity.WelcomeActivity;
import com.mayank.rucky.utils.ColorAdapter;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;

import java.util.Objects;

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
        final SwitchPreference themeSwitch = findPreference(Constants.PREF_KEY_DARK_THEME);
        assert themeSwitch != null;
        themeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setDarkMode(switched);
            restartActivity();
            return true;
        });
    }

    private void accentColorSetting() {
        final Preference accentPreference = findPreference(Constants.PREF_KEY_ACCENT_COLOR);
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
        final SwitchPreference iconSwitch = findPreference(Constants.PREF_KEY_LAUNCHER_ICON);
        assert iconSwitch != null;
        iconSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setIcon(switched);
            if (switched) {
                requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(Constants.PACKAGE_NAME, Constants.MAIN_ACTIVITY),PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(Constants.PACKAGE_NAME, Constants.MAIN_ACTIVITY),PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            return true;
        });
    }

    private void security() {
        final SwitchPreference securitySwitch = findPreference(Constants.PREF_KEY_SECURITY);
        assert securitySwitch != null;
        securitySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setSec(switched);
            restartActivity();
            return true;
        });
    }

    private void cleanup() {
        final Preference depPreference = findPreference(Constants.PREF_KEY_CLEANUP);
        assert depPreference != null;
        depPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(Constants.PACKAGE_URI));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            return true;
        });
    }

    private void hidSettings() {
        final SwitchPreference hidCustomise = findPreference(Constants.PREF_KEY_HID_CUSTOMISE);
        assert hidCustomise != null;
        final Preference hidPreference = findPreference(Constants.PREF_KEY_HID);
        assert hidPreference != null;
        hidPreference.setEnabled(config.getHIDCustomise());
        hidPreference.setSelectable(config.getHIDCustomise());
        hidPreference.setShouldDisableView(config.getHIDCustomise());
        hidPreference.setVisible(config.getHIDCustomise());
        hidCustomise.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setHIDCustomise(switched);
            hidPreference.setEnabled(config.getHIDCustomise());
            hidPreference.setSelectable(config.getHIDCustomise());
            hidPreference.setShouldDisableView(config.getHIDCustomise());
            hidPreference.setVisible(config.getHIDCustomise());
            return true;
        });
        hidPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), HidActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void usb() {
        final SwitchPreference usb = findPreference(Constants.PREF_KEY_USB);
        assert usb != null;
        usb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setUSB(switched);
            restartActivity();
            return true;
        });
    }

    private void net() {
        final SwitchPreference net = findPreference(Constants.PREF_KEY_NET);
        assert net != null;
        net.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean switched = !((SwitchPreference) preference).isChecked();
            config.setNet(switched);
            restartActivity();
            return true;
        });
    }

    private void developer() {
        final Preference developerPreference = findPreference(Constants.PREF_KEY_DEV);
        assert developerPreference != null;
        developerPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.setting_developer_title));
            intent.putExtra(Constants.webViewID, Constants.DEV_WEBSITE);
            startActivity(intent);
            return true;
        });
    }

    private void version() {
        final Preference versionPreference = findPreference(Constants.PREF_KEY_VERSION);
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
        versionPreference.setSummary(requireContext().getResources().getString(R.string.app_version, currentVersion, currentVersionCode));
    }

    private void arch() {
        String currentArch = Build.SUPPORTED_ABIS[0];
        final Preference archPreference = findPreference(Constants.PREF_KEY_ARCH);
        assert archPreference != null;
        archPreference.setSummary(currentArch);
    }

    private void source() {
        final Preference distributionPreference = findPreference(Constants.PREF_KEY_DISTRIBUTION);
        assert distributionPreference != null;
        distributionPreference.setSummary(EditorActivity.distro);
        String title = "";
        String url = "";
        boolean canIntent = true;
        if (EditorActivity.distro == R.string.releaseGitHub) {
            title = getResources().getString(R.string.releaseGitHub);
            url = Constants.GITHUB_RELEASE;
        } else if (EditorActivity.distro == R.string.releaseGitHubNightly) {
            title = getResources().getString(R.string.releaseGitHubNightly);
            url = Constants.GITHUB_RELEASE;
        } else if (EditorActivity.distro == R.string.releaseTest) {
            title = getResources().getString(R.string.releaseTest);
            url = Constants.GITHUB_RELEASE;
        } else if (EditorActivity.distro == R.string.releaseNetHunter) {
            title = getResources().getString(R.string.releaseNetHunter);
            url = Constants.KALI_NETHUNTER_RELEASE;
        } else {
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
        final Preference licencePreference = findPreference(Constants.PREF_KEY_LICENCE);
        assert licencePreference != null;
        licencePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_lic_title));
            intent.putExtra(Constants.webViewID, Constants.APP_LIC);
            startActivity(intent);
            return true;
        });
    }

    private void gitIssue() {
        Preference gitPreference = findPreference(Constants.PREF_KEY_GIT);
        assert gitPreference != null;
        gitPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_git_title));
            intent.putExtra(Constants.webViewID, Constants.GIT_ISSUES);
            startActivity(intent);
            return true;
        });
    }

    private void translate() {
        final Preference localePreference = findPreference(Constants.PREF_KEY_LOCALE);
        assert localePreference != null;
        localePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Constants.activityTitle, getResources().getString(R.string.settings_locale_title));
            intent.putExtra(Constants.webViewID,Constants.CROWDIN_JOIN);
            startActivity(intent);
            return true;
        });
    }

    private void restartActivity() {
        Intent restartIntent = new Intent(this.getActivity(), WelcomeActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.requireActivity().finish();
        startActivity(restartIntent);
    }

}