package com.mayank.rucky.utils;

import com.mayank.rucky.R;

public class Constants {

    //HIDModel
    public static final int HID_OFFLINE = 1;
    public static final int HID_UPDATE = 2;
    public static final int HID_DOWNLOAD = 3;

    //web view
    public static final String activityTitle = "ACTIVITY_TITLE";
    public static final String webViewID = "WEBVIEW_URL";

    //shared preferences
    public static final String PREF = "settings";
    public static final String PREF_SETTINGS_DARK_THEME = "darkTheme";
    public static final String PREF_SETTING_INIT = "init";
    public static final String PREF_SETTING_UPDATE = "update";
    public static final String PREF_SETTING_ACCENT = "accent";
    public static final String PREF_SETTING_ICON = "icon";
    public static final String PREF_SETTING_ADV_SECURITY = "advSecurity";
    public static final String PREF_DEV_USB = "usbConnectionState";
    public static final String PREF_DEV_NET = "usbConnectionState";
    public static final String PREF_DEV_USB_CONNECTION = "usbConnectionStatus";
    public static final String PREF_DEV_NET_CONNECTION = "netConnectionStatus";
    public static final String PREF_DEV_NET_ADDRESS = "ipSocket";

    //hid
    public static final String PREF_HID_CHOICE = "hidCustomize";
    public static final String PREF_HID_LANG = "hidLang";
    public static final String PREF_HID_MODE = "hidMode";
    public static final String PREF_HID_INTENT = "hidIntent";
    public static final String PREF_HID_FILE = "hidFile";
    public static final String PREF_HID_FILE_SELECTED = "hidFileSelected";

    //notifications
    public static final String CHANNEL_ID = "com.mayank.rucky.update";
    public static final String CHANNEL_NAME = "Update";
    public static final String SCHANNEL_ID = "com.mayank.rucky.service";
    public static final String SCHANNEL_NAME = "Foreground Service";

    //theme
    public static final int[] themeList = {
            R.style.blue_grey,      R.style.brown,
            R.style.deep_orange,    R.style.orange,
            R.style.amber,          R.style.yellow,
            R.style.lime,           R.style.light_green,
            R.style.green,          R.style.teal,
            R.style.cyan,           R.style.light_blue,
            R.style.blue,           R.style.indigo,
            R.style.deep_purple,    R.style.purple,
            R.style.pink,           R.style.red
    };

    public static final int[] themeSplashBorder = {
            R.drawable.splash_gradient_blue_grey,       R.drawable.splash_gradient_brown,
            R.drawable.splash_gradient_deep_orange,     R.drawable.splash_gradient_orange,
            R.drawable.splash_gradient_amber,           R.drawable.splash_gradient_yellow,
            R.drawable.splash_gradient_lime,            R.drawable.splash_gradient_light_green,
            R.drawable.splash_gradient_green,           R.drawable.splash_gradient_teal,
            R.drawable.splash_gradient_cyan,            R.drawable.splash_gradient_light_blue,
            R.drawable.splash_gradient_blue,            R.drawable.splash_gradient_indigo,
            R.drawable.splash_gradient_deep_purple,     R.drawable.splash_gradient_purple,
            R.drawable.splash_gradient_pink,            R.drawable.splash_gradient_red
    };

}
