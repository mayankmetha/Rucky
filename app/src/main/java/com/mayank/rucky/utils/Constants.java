package com.mayank.rucky.utils;

public class Constants {

    //package
    public static final String PACKAGE_NAME = "com.mayank.rucky";
    public static final String MAIN_ACTIVITY = "com.mayank.rucky.Main";
    public static final String PACKAGE_URI = "package:com.mayank.rucky";
    public static final String DEV_WEBSITE = "https://mayankmetha.github.io";
    public static final String GITHUB_RELEASE = "https://mayankmetha.github.io/Rucky/";
    public static final String GITHUB_RELEASE_LATEST = "https://github.com/mayankmetha/Rucky/releases/latest";
    public static final String KALI_NETHUNTER_RELEASE = "https://store.nethunter.com/en/packages/com.mayank.rucky/";
    public static final String APP_LIC = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/LICENSE";
    public static final String GIT_ISSUES = "https://github.com/mayankmetha/Rucky/issues";
    public static final String CROWDIN_JOIN = "https://crwd.in/rucky";
    public static final String APK_NAME = "rucky.apk";
    public static final String CHANGELOG_NIGHTLY = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/Changelog";
    public static final String CHANGELOG_RELEASE = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/docs/Changelog_";
    public static final String URL_NIGHTLY = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky-nightly.apk";
    public static final String URL_BASE_RELEASE = "https://github.com/mayankmetha/Rucky/releases/download/";
    public static final String URL_FILE_RELEASE = "/rucky.apk";
    public static final String CFG_NIGHTLY = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.cfg";
    public static final String CFG_BASE_RELEASE = "https://github.com/mayankmetha/Rucky/releases/download/";
    public static final String CFG_FILE_RELEASE = "/rucky.cfg";
    public static final String SIG_NIGHTLY = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.sha512";
    public static final String SIG_BASE_RELEASE = "https://github.com/mayankmetha/Rucky/releases/download/";
    public static final String SIG_FILE_RELEASE = "/rucky.sha512";
    public static final String URI_INSTALLER = "application/vnd.android.package-archive";
    public static final String KEYBOARDMAP_URL = "https://raw.githubusercontent.com/mayankmetha/Rucky-KeyMap/main/keymap.json";

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
    public static final String PREF_SETTING_ICON = "icon";
    public static final String PREF_SETTING_ADV_SECURITY = "advSecurity";
    public static final String PREF_CONFIGFS = "configfsUSB";
    public static final String PREF_DEV_USB = "usbConnectionState";
    public static final String PREF_DEV_NET = "usbConnectionState";
    public static final String PREF_DEV_USB_CONNECTION = "usbConnectionStatus";
    public static final String PREF_DEV_NET_CONNECTION = "netConnectionStatus";
    public static final String PREF_DEV_NET_ADDRESS = "ipSocket";
    public static final String PREF_UI_STATUS_TEXT = "configStatusText";
    public static final String PREF_UI_STATUS_IMG = "configStatusImage";

    //shared preference keys
    public static final String PREF_KEY_DARK_THEME = "theme";
    public static final String PREF_KEY_LAUNCHER_ICON = "icon";
    public static final String PREF_KEY_SECURITY = "sec";
    public static final String PREF_KEY_CLEANUP = "uninstall";
    public static final String PREF_KEY_CONFIGFS = "configfs";
    public static final String PREF_KEY_HID_CUSTOMISE = "hidSelect";
    public static final String PREF_KEY_HID = "hid";
    public static final String PREF_KEY_USB = "usb";
    public static final String PREF_KEY_NET = "net";
    public static final String PREF_KEY_DEV = "developer";
    public static final String PREF_KEY_VERSION= "version";
    public static final String PREF_KEY_ARCH = "arch";
    public static final String PREF_KEY_DISTRIBUTION = "source";
    public static final String PREF_KEY_LICENCE = "lic";
    public static final String PREF_KEY_GIT = "git";
    public static final String PREF_KEY_LOCALE = "locale";

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

    //service & receivers
    public static final String NET_SOCKET_CONNECTED = "com.mayank.rucky.netSocketConnected";
    public static final String NET_SOCKET_DISCONNECTED = "com.mayank.rucky.netSocketDisconnected";
    public static final String TELEPHONY = "android.provider.Telephony.SECRET_CODE";
    public static final String USB_ACTION = "android.hardware.usb.action.USB_STATE";
    public static final String USB_ACTION_STATE = "connected";

    public static final String[] hostnames = {
            "*.github.io",
            "*.github.com",
            "*.crwd.in",
            "*.crowdin.com",
    };

}
