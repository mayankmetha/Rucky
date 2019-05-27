package com.mayank.rucky;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class RootSettingsFragment extends PreferenceFragmentCompat {

    private static final String PREF_SETTINGS = "settings";
    private static final String PREF_SETTINGS_DARK_THEME = "darkTheme";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences settings = Objects.requireNonNull(this.getActivity()).getSharedPreferences(PREF_SETTINGS,MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, rootKey);
        final SwitchPreferenceCompat darkThemeSwitch = (SwitchPreferenceCompat) findPreference("theme");
        darkThemeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = !((SwitchPreferenceCompat) preference).isChecked();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREF_SETTINGS_DARK_THEME,switched).apply();
                MainActivity.didThemeChange = true;
                return true;
            }
        });
    }
}
