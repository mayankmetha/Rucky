package com.mayank.rucky.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Config extends Application {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    final static int randomTheme = new Randoms().randomTheme();

    @SuppressLint("CommitPrefEdits")
    public Config(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // dark theme
    public boolean getDarkMode() {
        return sharedPreferences.getBoolean(Constants.PREF_SETTINGS_DARK_THEME, true);
    }

    public void setDarkMode(boolean isDarkModeEnabled) {
        editor.putBoolean(Constants.PREF_SETTINGS_DARK_THEME, isDarkModeEnabled).apply();
    }

    // init screen
    public boolean getInitState() {
        return !sharedPreferences.getBoolean(Constants.PREF_SETTING_INIT, true);
    }

    public void setInitState(boolean isInitModeEnabled) {
        editor.putBoolean(Constants.PREF_SETTING_INIT, isInitModeEnabled).apply();
    }

    // update allowed
    public boolean getUpdateFlag() {
        return sharedPreferences.getBoolean(Constants.PREF_SETTING_UPDATE, false);
    }

    public void setUpdateFlag(boolean canUpdate) {
        editor.putBoolean(Constants.PREF_SETTING_UPDATE, canUpdate).apply();
    }

    // accent theme
    public int getAccentTheme() {
        return sharedPreferences.getInt(Constants.PREF_SETTING_ACCENT, randomTheme);
    }

    public void setAccentTheme(int accentTheme) {
        editor.putInt(Constants.PREF_SETTING_ACCENT, accentTheme).apply();
    }

    // launcher icon
    public boolean getIcon() {
        return sharedPreferences.getBoolean(Constants.PREF_SETTING_ICON, false);
    }

    public void setIcon(boolean hideIcon) {
        editor.putBoolean(Constants.PREF_SETTING_ICON, hideIcon).apply();
    }

    // advance security
    public boolean getSec() {
        return sharedPreferences.getBoolean(Constants.PREF_SETTING_ADV_SECURITY, false);
    }

    public void setSec(boolean enabled) {
        editor.putBoolean(Constants.PREF_SETTING_ADV_SECURITY, enabled).apply();
    }

    // usb mode
    public boolean getUSB() {
        return  sharedPreferences.getBoolean(Constants.PREF_DEV_USB, false);
    }

    public void setUSB(boolean enable) {
        editor.putBoolean(Constants.PREF_DEV_USB, enable).apply();
    }

    // network mode
    public boolean getNet() {
        return  sharedPreferences.getBoolean(Constants.PREF_DEV_NET, false);
    }

    public void setNet(boolean enable) {
        editor.putBoolean(Constants.PREF_DEV_NET, enable).apply();
    }

    // hid
    public boolean getHIDCustomise() { return sharedPreferences.getBoolean(Constants.PREF_HID_CHOICE, false); }

    public void setHIDCustomise(boolean choice) {
        editor.putBoolean(Constants.PREF_HID_CHOICE, choice).apply();
    }

    public int getHIDLanguage() {
        return sharedPreferences.getInt(Constants.PREF_HID_LANG, 0);
    }

    public void setHIDLanguage(int language) {
        editor.putInt(Constants.PREF_HID_LANG, language).apply();
    }

    public int getHIDMode() {
        return sharedPreferences.getInt(Constants.PREF_HID_MODE, 0);
    }

    public void setHIDMode(int mode) {
        editor.putInt(Constants.PREF_HID_MODE, mode).apply();
    }

    // hid intent
    public String getHIDIntent() {
        return sharedPreferences.getString(Constants.PREF_HID_INTENT, "");
    }

    public void setHIDIntent(String hidIntent) {
        editor.putString(Constants.PREF_HID_INTENT, hidIntent).apply();
    }

    public String getHIDFile() {
        return sharedPreferences.getString(Constants.PREF_HID_FILE, "");
    }

    public void setHIDFile(String hidFile) {
        editor.putString(Constants.PREF_HID_FILE, hidFile).apply();
    }

    public String getHIDFileSelected() {
        return sharedPreferences.getString(Constants.PREF_HID_FILE_SELECTED, "");
    }

    public void setHIDFileSelected(String hidFile) {
        editor.putString(Constants.PREF_HID_FILE_SELECTED, hidFile).apply();
    }

    // usb connection status
    public boolean getUSBStatus() {
        return sharedPreferences.getBoolean(Constants.PREF_DEV_USB_CONNECTION, false);
    }

    public void setUSBStatus(boolean status) {
        editor.putBoolean(Constants.PREF_DEV_USB_CONNECTION, status).apply();
    }

    // network socket connection status
    public boolean getNetworkStatus() {
        return sharedPreferences.getBoolean(Constants.PREF_DEV_NET_CONNECTION, false);
    }

    public void setNetworkStatus(boolean status) {
        editor.putBoolean(Constants.PREF_DEV_NET_CONNECTION, status).apply();
    }

    // network socket address
    public String getNetworkAddress() {
        return sharedPreferences.getString(Constants.PREF_DEV_NET_ADDRESS, "0.0.0.0:8000");
    }

    public void setNetworkAddress(String address) {
        editor.putString(Constants.PREF_DEV_NET_ADDRESS, address).apply();
    }
}
