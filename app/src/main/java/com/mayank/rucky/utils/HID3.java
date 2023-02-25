package com.mayank.rucky.utils;

import androidx.annotation.NonNull;

import com.mayank.rucky.models.KeyModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Pattern;

public class HID3 {

    private float defdelay = 0;
    private boolean is_hold = false;
    private boolean is_release = false;
    private boolean is_inject_mod = false;
    private boolean remove_inject = false;
    private String modKeyInjected = "00";
    private static String modKeypress = "00";
    private static LinkedList<String> keypress;
    private int intent = 0;
    private final ArrayList<String> shell = new ArrayList<>();
    private final HashMap<String, String> keys = new HashMap<>();
    private final HashMap<Character, String> keymap = new HashMap<>();


    public HID3(String jsonStr) {
        keypress = new LinkedList<>();
        initKey();
        keymap.clear();
        try{
            if(!jsonStr.isEmpty()) {
                JSONObject json = new JSONObject(jsonStr).getJSONObject("mapping");
                for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                    String key = it.next();
                    KeyModel keymodel = new KeyModel(key.charAt(0),
                            json.getJSONObject(key).getString("name"),
                            Integer.parseInt(json.getJSONObject(key).getString("keycode"),16),
                            json.getJSONObject(key).getJSONObject("modifier").getString("ctrl"),
                            json.getJSONObject(key).getJSONObject("modifier").getString("shift"),
                            json.getJSONObject(key).getJSONObject("modifier").getString("alt"),
                            json.getJSONObject(key).getJSONObject("modifier").getString("meta"));
                    keymap.put(keymodel.getKey(), keyboard(String.format("%02X",keymodel.getModifier()), String.format("%02X",keymodel.getKeycode())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initKey() {
        keys.put("MODIFIERKEY_RESET","00");
        keys.put("MODIFIERKEY_CTRL","01");
        keys.put("MODIFIERKEY_SHIFT","02");
        keys.put("MODIFIERKEY_CTRL_SHIFT","03");
        keys.put("MODIFIERKEY_ALT","04");
        keys.put("MODIFIERKEY_CTRL_ALT","05");
        keys.put("MODIFIERKEY_ALT_SHIFT","06");
        keys.put("MODIFIERKEY_CTRL_SHIFT_ALT","07");
        keys.put("MODIFIERKEY_GUI","08");
        keys.put("MODIFIERKEY_CTRL_GUI","09");
        keys.put("MODIFIERKEY_SHIFT_GUI","0A");
        keys.put("MODIFIERKEY_CTRL_SHIFT_GUI","0B");
        keys.put("MODIFIERKEY_ALT_GUI","0C");
        keys.put("MODIFIERKEY_CTRL_ALT_GUI","0D");
        keys.put("MODIFIERKEY_ALT_SHIFT_GUI","0E");
        keys.put("MODIFIERKEY_CTRL_SHIFT_ALT_GUI","0F");
        keys.put("MODIFIERKEY_LEFT_CTRL","01");
        keys.put("MODIFIERKEY_LEFT_SHIFT","02");
        keys.put("MODIFIERKEY_LEFT_CTRL_SHIFT","03");
        keys.put("MODIFIERKEY_LEFT_ALT","04");
        keys.put("MODIFIERKEY_LEFT_CTRL_ALT","05");
        keys.put("MODIFIERKEY_LEFT_ALT_SHIFT","06");
        keys.put("MODIFIERKEY_LEFT_CTRL_SHIFT_ALT","07");
        keys.put("MODIFIERKEY_LEFT_GUI","08");
        keys.put("MODIFIERKEY_LEFT_CTRL_GUI","09");
        keys.put("MODIFIERKEY_LEFT_SHIFT_GUI","0A");
        keys.put("MODIFIERKEY_LEFT_CTRL_SHIFT_GUI","0B");
        keys.put("MODIFIERKEY_LEFT_ALT_GUI","0C");
        keys.put("MODIFIERKEY_LEFT_CTRL_ALT_GUI","0D");
        keys.put("MODIFIERKEY_LEFT_ALT_SHIFT_GUI","0E");
        keys.put("MODIFIERKEY_LEFT_CTRL_SHIFT_ALT_GUI","0F");
        keys.put("MODIFIERKEY_RIGHT_CTRL","10");
        keys.put("MODIFIERKEY_RIGHT_SHIFT","20");
        keys.put("MODIFIERKEY_RIGHT_CTRL_SHIFT","30");
        keys.put("MODIFIERKEY_RIGHT_ALT","40");
        keys.put("MODIFIERKEY_RIGHT_CTRL_ALT","50");
        keys.put("MODIFIERKEY_RIGHT_ALT_SHIFT","60");
        keys.put("MODIFIERKEY_RIGHT_CTRL_SHIFT_ALT","70");
        keys.put("MODIFIERKEY_RIGHT_GUI","80");
        keys.put("MODIFIERKEY_RIGHT_CTRL_GUI","90");
        keys.put("MODIFIERKEY_RIGHT_SHIFT_GUI","A0");
        keys.put("MODIFIERKEY_RIGHT_CTRL_SHIFT_GUI","B0");
        keys.put("MODIFIERKEY_RIGHT_ALT_GUI","C0");
        keys.put("MODIFIERKEY_RIGHT_CTRL_ALT_GUI","D0");
        keys.put("MODIFIERKEY_RIGHT_ALT_SHIFT_GUI","E0");
        keys.put("MODIFIERKEY_RIGHT_CTRL_SHIFT_ALT_GUI","F0");
        keys.put("KEY_A","04");
        keys.put("KEY_B","05");
        keys.put("KEY_C","06");
        keys.put("KEY_D","07");
        keys.put("KEY_E","08");
        keys.put("KEY_F","09");
        keys.put("KEY_G","0A");
        keys.put("KEY_H","0B");
        keys.put("KEY_I","0C");
        keys.put("KEY_J","0D");
        keys.put("KEY_K","0E");
        keys.put("KEY_L","0F");
        keys.put("KEY_M","10");
        keys.put("KEY_N","11");
        keys.put("KEY_O","12");
        keys.put("KEY_P","13");
        keys.put("KEY_Q","14");
        keys.put("KEY_R","15");
        keys.put("KEY_S","16");
        keys.put("KEY_T","17");
        keys.put("KEY_U","18");
        keys.put("KEY_V","19");
        keys.put("KEY_W","1A");
        keys.put("KEY_X","1B");
        keys.put("KEY_Y","1C");
        keys.put("KEY_Z","1D");
        keys.put("KEY_1","1E");
        keys.put("KEY_2","1F");
        keys.put("KEY_3","20");
        keys.put("KEY_4","21");
        keys.put("KEY_5","22");
        keys.put("KEY_6","23");
        keys.put("KEY_7","24");
        keys.put("KEY_8","25");
        keys.put("KEY_9","26");
        keys.put("KEY_0","27");
        keys.put("KEY_ENTER","28");
        keys.put("KEY_ESC","29");
        keys.put("KEY_BACKSPACE","2A");
        keys.put("KEY_TAB","2B");
        keys.put("KEY_SPACE","2C");
        keys.put("KEY_MINUS","2D");
        keys.put("KEY_EQUAL","2E");
        keys.put("KEY_LEFTBRACE","2F");
        keys.put("KEY_RIGHTBRACE","30");
        keys.put("KEY_BACKSLASH","31");
        keys.put("KEY_TILDE","32");
        keys.put("KEY_SEMICOLON","33");
        keys.put("KEY_QUOTE","34");
        keys.put("KEY_GRAVE","35");
        keys.put("KEY_COMMA","36");
        keys.put("KEY_PERIOD","37");
        keys.put("KEY_SLASH","38");
        keys.put("KEY_CAPSLOCK","39");
        keys.put("KEY_F1","3A");
        keys.put("KEY_F2","3B");
        keys.put("KEY_F3","3C");
        keys.put("KEY_F4","3D");
        keys.put("KEY_F5","3E");
        keys.put("KEY_F6","3F");
        keys.put("KEY_F7","40");
        keys.put("KEY_F8","41");
        keys.put("KEY_F9","42");
        keys.put("KEY_F10","43");
        keys.put("KEY_F11","44");
        keys.put("KEY_F12","45");
        keys.put("KEY_SYSRQ","46");
        keys.put("KEY_SCROLLLOCK","47");
        keys.put("KEY_PAUSE","48");
        keys.put("KEY_INSERT","49");
        keys.put("KEY_HOME","4a");
        keys.put("KEY_PAGEUP","4b");
        keys.put("KEY_DELETE","4c");
        keys.put("KEY_END","4d");
        keys.put("KEY_PAGEDOWN","4e");
        keys.put("KEY_RIGHT","4f");
        keys.put("KEY_LEFT","50");
        keys.put("KEY_DOWN","51");
        keys.put("KEY_UP","52");
        keys.put("KEY_NUMLOCK","53");
        keys.put("KEY_KPSLASH","54");
        keys.put("KEY_KPASTERISK","55");
        keys.put("KEY_KPMINUS","56");
        keys.put("KEY_KPPLUS","57");
        keys.put("KEY_KPENTER","58");
        keys.put("KEY_KP1","59");
        keys.put("KEY_KP2","5a");
        keys.put("KEY_KP3","5b");
        keys.put("KEY_KP4","5c");
        keys.put("KEY_KP5","5d");
        keys.put("KEY_KP6","5e");
        keys.put("KEY_KP7","5f");
        keys.put("KEY_KP8","60");
        keys.put("KEY_KP9","61");
        keys.put("KEY_KP0","62");
        keys.put("KEY_KPDOT","63");
        keys.put("KEY_102ND","64");
        keys.put("KEY_COMPOSE","65");
        keys.put("KEY_POWER","66");
        keys.put("KEY_KPEQUAL","67");
        keys.put("KEY_F13","68");
        keys.put("KEY_F14","69");
        keys.put("KEY_F15","6a");
        keys.put("KEY_F16","6b");
        keys.put("KEY_F17","6c");
        keys.put("KEY_F18","6d");
        keys.put("KEY_F19","6e");
        keys.put("KEY_F20","6f");
        keys.put("KEY_F21","70");
        keys.put("KEY_F22","71");
        keys.put("KEY_F23","72");
        keys.put("KEY_F24","73");
        keys.put("KEY_OPEN","74");
        keys.put("KEY_HELP","75");
        keys.put("KEY_PROPS","76");
        keys.put("KEY_FRONT","77");
        keys.put("KEY_STOP","78");
        keys.put("KEY_AGAIN","79");
        keys.put("KEY_UNDO","7a");
        keys.put("KEY_CUT","7b");
        keys.put("KEY_COPY","7c");
        keys.put("KEY_PASTE","7d");
        keys.put("KEY_FIND","7e");
        keys.put("KEY_MUTE","7f");
        keys.put("KEY_VOLUMEUP","80");
        keys.put("KEY_VOLUMEDOWN","81");
        keys.put("KEY_KPLEFTPAREN","b6");
        keys.put("KEY_KPRIGHTPAREN","b7");
        keys.put("KEY_LEFTCTRL","e0");
        keys.put("KEY_LEFTSHIFT","e1");
        keys.put("KEY_LEFTALT","e2");
        keys.put("KEY_LEFTMETA","e3");
        keys.put("KEY_RIGHTCTRL","e4");
        keys.put("KEY_RIGHTSHIFT","e5");
        keys.put("KEY_RIGHTALT","e6");
        keys.put("KEY_RIGHTMETA","e7");
        keys.put("KEY_MEDIA_PLAYPAUSE","e8");
        keys.put("KEY_MEDIA_STOPCD","e9");
        keys.put("KEY_MEDIA_PREVIOUSSONG","ea");
        keys.put("KEY_MEDIA_NEXTSONG","eb");
        keys.put("KEY_MEDIA_EJECTCD","ec");
        keys.put("KEY_MEDIA_VOLUMEUP","ed");
        keys.put("KEY_MEDIA_VOLUMEDOWN","ee");
        keys.put("KEY_MEDIA_MUTE","ef");
        keys.put("KEY_MEDIA_WWW","f0");
        keys.put("KEY_MEDIA_BACK","f1");
        keys.put("KEY_MEDIA_FORWARD","f2");
        keys.put("KEY_MEDIA_STOP","f3");
        keys.put("KEY_MEDIA_FIND","f4");
        keys.put("KEY_MEDIA_SCROLLUP","f5");
        keys.put("KEY_MEDIA_SCROLLDOWN","f6");
        keys.put("KEY_MEDIA_EDIT","f7");
        keys.put("KEY_MEDIA_SLEEP","f8");
        keys.put("KEY_MEDIA_COFFEE","f9");
        keys.put("KEY_MEDIA_REFRESH","fa");
        keys.put("KEY_MEDIA_CALC","fb");
    }

    private String comboKeys(String k) {
        k = k.toLowerCase();
        switch (k) {
            case "esc": case "escape": return keys.get("KEY_ESC");
            case "enter": return keys.get("KEY_ENTER");
            case "space": return keys.get("KEY_SPACE");
            case "backspace": case "bksp": return keys.get("KEY_BACKSPACE");
            case "tab": return keys.get("KEY_TAB");
            case "insert": case "ins": return keys.get("KEY_INSERT");
            case "delete": case "del": return keys.get("KEY_DELETE");
            case "pageup": return keys.get("KEY_PAGEUP");
            case "pagedown": return keys.get("KEY_PAGEDOWN");
            case "home": return keys.get("KEY_HOME");
            case "end": return keys.get("KEY_END");
            case "up": case "uparrow": return keys.get("KEY_UP");
            case "down": case "downarrow": return keys.get("KEY_DOWN");
            case "right": case "rightarrow": return keys.get("KEY_RIGHT");
            case "left": case "leftarrow": return keys.get("KEY_LEFT");
            case "break": case "pause": return keys.get("KEY_PAUSE");
            case "f1": return keys.get("KEY_F1");
            case "f2": return keys.get("KEY_F2");
            case "f3": return keys.get("KEY_F3");
            case "f4": return keys.get("KEY_F4");
            case "f5": return keys.get("KEY_F5");
            case "f6": return keys.get("KEY_F6");
            case "f7": return keys.get("KEY_F7");
            case "f8": return keys.get("KEY_F8");
            case "f9": return keys.get("KEY_F9");
            case "f10": return keys.get("KEY_F10");
            case "f11": return keys.get("KEY_F11");
            case "f12": return keys.get("KEY_F12");
            case "f13": return keys.get("KEY_F13");
            case "f14": return keys.get("KEY_F14");
            case "f15": return keys.get("KEY_F15");
            case "f16": return keys.get("KEY_F16");
            case "f17": return keys.get("KEY_F17");
            case "f18": return keys.get("KEY_F18");
            case "f19": return keys.get("KEY_F19");
            case "f20": return keys.get("KEY_F20");
            case "f21": return keys.get("KEY_F21");
            case "f22": return keys.get("KEY_F22");
            case "f23": return keys.get("KEY_F23");
            case "f24": return keys.get("KEY_F24");
            case "0": return keys.get("KEY_0");
            case "1": return keys.get("KEY_1");
            case "2": return keys.get("KEY_2");
            case "3": return keys.get("KEY_3");
            case "4": return keys.get("KEY_4");
            case "5": return keys.get("KEY_5");
            case "6": return keys.get("KEY_6");
            case "7": return keys.get("KEY_7");
            case "8": return keys.get("KEY_8");
            case "9": return keys.get("KEY_9");
            case "a": return keys.get("KEY_A");
            case "b": return keys.get("KEY_B");
            case "c": return keys.get("KEY_C");
            case "d": return keys.get("KEY_D");
            case "e": return keys.get("KEY_E");
            case "f": return keys.get("KEY_F");
            case "g": return keys.get("KEY_G");
            case "h": return keys.get("KEY_H");
            case "i": return keys.get("KEY_I");
            case "j": return keys.get("KEY_J");
            case "k": return keys.get("KEY_K");
            case "l": return keys.get("KEY_L");
            case "m": return keys.get("KEY_M");
            case "n": return keys.get("KEY_N");
            case "o": return keys.get("KEY_O");
            case "p": return keys.get("KEY_P");
            case "q": return keys.get("KEY_Q");
            case "r": return keys.get("KEY_R");
            case "s": return keys.get("KEY_S");
            case "t": return keys.get("KEY_T");
            case "u": return keys.get("KEY_U");
            case "v": return keys.get("KEY_V");
            case "w": return keys.get("KEY_W");
            case "x": return keys.get("KEY_X");
            case "y": return keys.get("KEY_Y");
            case "z": return keys.get("KEY_Z");
            case "`": case "~": return keys.get("KEY_GRAVE");
            case "[": case "{": return keys.get("KEY_LEFTBRACE");
            case "]": case "}": return keys.get("KEY_RIGHTBRACE");
            case "\\": case "|": return keys.get("KEY_BACKSLASH");
            case ";": case ":": return keys.get("KEY_SEMICOLON");
            case "'": case "\"": return keys.get("KEY_QUOTE");
            case ",": case "<": return keys.get("KEY_COMMA");
            case ".": case ">": return keys.get("KEY_PERIOD");
            case "/": case "?": return keys.get("KEY_SLASH");
            case "-": case "_": return keys.get("KEY_MINUS");
            case "=": case "+": return keys.get("KEY_EQUAL");
        }
        return "00";
    }

    private String insert_intent() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < intent; i++) {
            str.append("    ");
        }
        return str.toString();
    }

    private String keyboard(String modArg, String[] args) {
        switch (args.length) {
            case 0:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            case 1:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            case 2:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x"+args[1]+"\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            case 3:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x"+args[1]+"\\\\x"+args[2]+"\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            case 4:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x"+args[1]+"\\\\x"+args[2]+"\\\\x"+args[3]+"\\\\x00\\\\x00\" > /dev/hidg0\n";
            case 5:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x"+args[1]+"\\\\x"+args[2]+"\\\\x"+args[3]+"\\\\x"+args[4]+"\\\\x00\" > /dev/hidg0\n";
            case 6:
                return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args[0]+"\\\\x"+args[1]+"\\\\x"+args[2]+"\\\\x"+args[3]+"\\\\x"+args[4]+"\\\\x"+args[5]+"\" > /dev/hidg0\n";
            default:
                return insert_intent()+"echo \"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
        }
    }

    private String keyboard(String modArg, String args) {
        return insert_intent()+"echo \"\\\\x"+modArg+"\\\\x00\\\\x"+args+"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
    }

    private String keyboard() {
        return insert_intent()+"echo \"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
    }

    private int getMouseLoopVar(String str) {
        int count = 1;
        try {
            if (str == null || str.replaceAll("\\s+","").isEmpty()) {
                return count;
            } else {
                count = Integer.parseInt(str);
            }
        } catch (Exception e) {
            return count;
        }
        return count;
    }

    private ArrayList<String> mouse(String[] mbytes, int count) {
        ArrayList<String> tmpStr = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String tmp = insert_intent()+"echo \"\\\\x" + mbytes[0] + "\\\\x" + mbytes[1] + "\\\\x" + mbytes[2] + "\\\\x" + mbytes[3] + "\" > /dev/hidg1\n";
            tmpStr.add(tmp);
        }
        return tmpStr;
    }

    private boolean validateConstant(@NonNull String constant) {
        final Pattern pattern = Pattern.compile("^[A-Za-z_]\\w*$");
        return pattern.matcher(constant).matches();
    }

    private boolean validateFunctionName(@NonNull String function) {
        final Pattern pattern = Pattern.compile("^[A-Za-z_][A-Za-z_0-9]*\\(\\)$");
        return pattern.matcher(function).matches();
    }

    private boolean validateVariable(@NonNull String variable) {
        final Pattern pattern = Pattern.compile("^\\$[A-Za-z_][A-Za-z_0-9]*$");
        return pattern.matcher(variable).matches();
    }

    private boolean validateVariableContent(@NonNull String variable) {
        return variable.matches("(?i)true") || variable.matches("(?i)false") || variable.matches("0")
                || variable.matches("([1-9]\\d{0,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])")
                || variable.contains("(") || variable.contains(")") || variable.contains("+") || variable.contains("-")
                || variable.contains("*") || variable.contains("/") || variable.contains("%") || variable.contains("^")
                || variable.contains("<") || variable.contains(">") || variable.contains("=") || variable.contains("!")
                || variable.contains("&") || variable.contains("|");
    }

    public ArrayList<String> getCmd() {
        return shell;
    }

    private void configFS() {
        shell.add("echo 0 > /data/local/tmp/rucky.error\n");
        shell.add("if [ \"$(printf '%s\\n%s\\n' \"3.19\" \"$(uname -r)\" | sort -V | head -n1)\" = \"3.19\" ]; then\n");
        shell.add("    if [ -d /config/usb_gadget -o -f /sys/devices/virtual/android_usb/android0/enable ]; then\n");
        shell.add("        if [ ! -d /config/usb_gadget/hid ]; then\n");
        shell.add("            mkdir -p /config/usb_gadget/hid/configs/c.1 > /dev/null 2>&1\n");
        shell.add("            mkdir -p /config/usb_gadget/hid/strings/0x409 > /dev/null 2>&1\n");
        shell.add("            mkdir -p /config/usb_gadget/hid/functions/hid.keyboard > /dev/null 2>&1\n");
        shell.add("            chmod -R 0777 /config/usb_gadget/hid\n");
        shell.add("            echo \"1\" > /config/usb_gadget/hid/functions/hid.keyboard/protocol\n");
        shell.add("            echo \"1\" > /config/usb_gadget/hid/functions/hid.keyboard/subclass\n");
        shell.add("            echo \"8\" > /config/usb_gadget/hid/functions/hid.keyboard/report_length\n");
        shell.add("            echo -n -e '\\x05\\x01\\x09\\x06\\xa1\\x01\\x05\\x07\\x19\\xe0\\x29\\xe7\\x15\\x00\\x25\\x01\\x75\\x01\\x95\\x08\\x81\\x02\\x95\\x01\\x75\\x08\\x81\\x03\\x95\\x05\\x75\\x01\\x05\\x08\\x19\\x01\\x29\\x05\\x91\\x02\\x95\\x01\\x75\\x03\\x91\\x03\\x95\\x06\\x75\\x08\\x15\\x00\\x25\\x65\\x05\\x07\\x19\\x00\\x29\\x65\\x81\\x00\\xc0' > /config/usb_gadget/hid/functions/hid.keyboard/report_desc\n");
        shell.add("            mkdir -p /config/usb_gadget/hid/functions/hid.mouse > /dev/null 2>&1\n");
        shell.add("            echo \"2\" > /config/usb_gadget/hid/functions/hid.mouse/protocol\n");
        shell.add("            echo \"1\" > /config/usb_gadget/hid/functions/hid.mouse/subclass\n");
        shell.add("            echo \"4\" > /config/usb_gadget/hid/functions/hid.mouse/report_length\n");
        shell.add("            echo -n -e '\\x05\\x01\\x09\\x02\\xa1\\x01\\x09\\x01\\xa1\\x00\\x05\\x09\\x19\\x01\\x29\\x05\\x15\\x00\\x25\\x01\\x95\\x05\\x75\\x01\\x81\\x02\\x95\\x01\\x75\\x03\\x81\\x01\\x05\\x01\\x09\\x30\\x09\\x31\\x09\\x38\\x15\\x81\\x25\\x7F\\x75\\x08\\x95\\x03\\x81\\x06\\xc0\\xc0' > /config/usb_gadget/hid/functions/hid.mouse/report_desc\n");
        shell.add("            cp /config/usb_gadget/g1/idVendor /config/usb_gadget/hid/idVendor\n");
        shell.add("            cp /config/usb_gadget/g1/idProduct /config/usb_gadget/hid/idProduct\n");
        shell.add("            cp /config/usb_gadget/g1/strings/0x409/manufacturer /config/usb_gadget/hid/strings/0x409/manufacturer\n");
        shell.add("            cp /config/usb_gadget/g1/strings/0x409/product /config/usb_gadget/hid/strings/0x409/product\n");
        shell.add("            cp /config/usb_gadget/g1/strings/0x409/serialnumber /config/usb_gadget/hid/strings/0x409/serialnumber\n");
        shell.add("            echo 120 > /config/usb_gadget/hid/configs/c.1/MaxPower\n");
        shell.add("            ln -s /config/usb_gadget/hid/functions/hid.keyboard /config/usb_gadget/hid/configs/c.1/hid.keyboard\n");
        shell.add("            ln -s /config/usb_gadget/hid/functions/hid.mouse /config/usb_gadget/hid/configs/c.1/hid.mouse\n");
        shell.add("        fi\n");
        shell.add("    else\n");
        shell.add("        if [ -f /dev/hidg0 ]; then\n");
        shell.add("            chmod 0777 /dev/hidg0;chmod 0777 /dev/hidg1\n");
        shell.add("        else\n");
        shell.add("            echo \"kernel=2\" > /data/local/tmp/rucky.error\n");
        shell.add("        fi\n");
        shell.add("    fi\n");
        shell.add("else\n");
        shell.add("    if [ -f /dev/hidg0 ]; then\n");
        shell.add("        chmod 0777 /dev/hidg0;chmod 0777 /dev/hidg1\n");
        shell.add("    else\n");
        shell.add("        echo \"kernel=1\" > /data/local/tmp/rucky.error\n");
        shell.add("    fi\n");
        shell.add("fi\n");
        shell.add("if [ -f /dev/hidg0 ]; then\n");
        shell.add("    chmod 0777 /dev/hidg0;chmod 0777 /dev/hidg1\n");
        shell.add("else\n");
        shell.add("    echo \"kernel=0\" > /data/local/tmp/rucky.error\n");
        shell.add("fi\n");
    }

    private void enableAttackMode() {
        shell.add("if [ -f /config/usb_gadget/hid/UDC ]; then\n");
        shell.add("    echo \"\" > /config/usb_gadget/g1/UDC\n");
        shell.add("    echo $(getprop sys.usb.controller) > /config/usb_gadget/hid/UDC\n");
        shell.add("fi\n");
    }

    private void disableAttackMode() {
        shell.add("if [ -f /config/usb_gadget/hid/UDC ]; then\n");
        shell.add("    echo \"\" > /config/usb_gadget/hid/UDC\n");
        shell.add("    echo $(getprop sys.usb.controller) > /config/usb_gadget/g1/UDC\n");
        shell.add("fi\n");
    }

    public void parse(@NonNull String str) {
        str = str.replaceAll(" +"," ");
        String[] lines = str.split("\\r?\\n");
        String con;
        float delay;
        boolean is_repeat = false;

        LinkedList<String> pass1Result = new LinkedList<>();
        HashMap<String, String>constants = new HashMap<>();
        HashMap<String, String>functions = new HashMap<>();

        configFS();
        enableAttackMode();

        // Pass1: Replace constants, variables, functions, spaces, comments
        for (int a = 0; a < lines.length; a++) {
            lines[a] = lines[a].trim();

            // empty line and comments
            if (lines[a].isEmpty() || lines[a].startsWith("REM")) {
                continue;
            }

            // REPEAT to LOOP Conversion
            else if (lines[a].startsWith("REPEAT")) {
                is_repeat = true;
                pass1Result.add(lines[a]);
                continue;
            }

            // CONSTANTS
            else if (lines[a].startsWith("DEFINE")) {
                con = lines[a].replace("DEFINE ", "");
                String[] constantsStr = con.split(" ");
                if (validateConstant(constantsStr[0])) {
                    constants.put(constantsStr[0], constantsStr[1]);
                }
                continue;
            }

            // VARIABLES
            else if (lines[a].startsWith("VAR") || lines[a].startsWith("$")) {
                boolean isVariableDeclaration = false;
                if (lines[a].startsWith("VAR")) {
                    isVariableDeclaration = true;
                    lines[a] = lines[a].replace("VAR ","");
                    if(!lines[a].contains("=")) {
                        continue;
                    }
                    lines[a] = lines[a].replaceAll("\\s","");
                    if (!validateVariable(lines[a].split("=")[0])) {
                        continue;
                    }
                    if (!validateVariableContent(lines[a].split("=")[1])) {
                        continue;
                    }
                }
                lines[a] = lines[a].replaceAll("\\s","");
                lines[a] = lines[a].replace("$","");
                lines[a] = lines[a].replace("^","**");
                lines[a] = lines[a].replace("0x","16#");
                lines[a] = lines[a].replace("=","=$((");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                lines[a] = lines[a]+"))";
                if (isVariableDeclaration) {
                    lines[a] = "export "+lines[a];
                }
            }

            // Collect function names
            else if (lines[a].startsWith("FUNCTION")) {
                con = lines[a].replace("FUNCTION ","");
                if (!validateFunctionName(con)) {
                    continue;
                }
                functions.put(con,con.substring(0,con.length()-2));
            }

            // Replace
            else {
                // Replace Constants
                for(String conStr: constants.keySet()) {
                    if (lines[a].equals(conStr)) {
                        lines[a] = lines[a].replace(conStr, Objects.requireNonNull(constants.get(conStr)));
                    } else if (lines[a].startsWith("STRING")) {
                        con = lines[a].split(" ",2)[1];
                        if (con.equals(conStr)) {
                            lines[a] = lines[a].replace(conStr, Objects.requireNonNull(constants.get(conStr)));
                        }
                    } else if (lines[a].endsWith(" "+conStr)) {
                        lines[a] = lines[a].replace(conStr, Objects.requireNonNull(constants.get(conStr)));
                    }
                }
                for(String funStr: functions.keySet()) {
                    if (lines[a].equals(funStr)) {
                        lines[a] = "CALL_FUNCTION "+functions.get(funStr);
                    } else if (!lines[a].startsWith("STRING") && lines[a].contains(funStr)) {
                        pass1Result.add("CALL_FUNCTION "+functions.get(funStr));
                        lines[a] = lines[a].replace(funStr, "$?");
                    }
                }
            }

            pass1Result.add(lines[a]);

            if (is_repeat) {
                is_repeat = false;
                pass1Result.add("END_REPEAT");
            }
        }

        lines = pass1Result.toArray(new String[0]);

        // Pass2
        for (int a = 0; a < lines.length; a++) {
            lines[a] = lines[a].trim();
            delay = defdelay;

            //DEFAULTDELAY or DEFAULT_DELAY
            if (a == 0 && (lines[a].startsWith("DEFAULTDELAY") || lines[a].startsWith("DEFAULT_DELAY"))) {
                con = lines[a];
                con = con.replace("DEFAULTDELAY ", "");
                con = con.replace("DEFAULT_DELAY ", "");
                defdelay = Float.parseFloat(con);
            }
            //DELAY
            else if (lines[a].startsWith("DELAY")) {
                con = lines[a].replace("DELAY ", "");
                delay = Float.parseFloat(con);
                delay += defdelay;
                if (is_inject_mod) {
                    remove_inject = true;
                    is_inject_mod = false;
                }
            }

            //ATTACKMODE
            else if (lines[a].startsWith("ATTACKMODE")) {
                lines[a] = lines[a].replace("ATTACKMODE ","");
                String[] attackmodePart = lines[a].split(" ");
                for (String attackmode: attackmodePart) {
                    if (attackmode.startsWith("VID_")) {
                        lines[a] = lines[a].replace(attackmode,"");
                        attackmode = attackmode.replace("VID_","");
                        shell.add("echo "+attackmode+" > config/usb_gadget/hid/idVendor\n");
                    }
                    if (attackmode.startsWith("PID_")) {
                        lines[a] = lines[a].replace(attackmode,"");
                        attackmode = attackmode.replace("PID_","");
                        shell.add("echo "+attackmode+" > config/usb_gadget/hid/idProduct\n");
                    }
                    if (attackmode.startsWith("MAN_")) {
                        lines[a] = lines[a].replace(attackmode,"");
                        attackmode = attackmode.replace("MAN_","");
                        shell.add("echo "+attackmode+" > /config/usb_gadget/hid/strings/0x409/manufacturer\n");
                    }
                    if (attackmode.startsWith("PROD_")) {
                        lines[a] = lines[a].replace(attackmode,"");
                        attackmode = attackmode.replace("PROD_","");
                        shell.add("echo "+attackmode+" > /config/usb_gadget/hid/strings/0x409/product\n");
                    }
                    if (attackmode.startsWith("SERIAL_")) {
                        lines[a] = lines[a].replace(attackmode,"");
                        attackmode = attackmode.replace("SERIAL_","");
                        shell.add("echo "+attackmode+" > /config/usb_gadget/hid/strings/0x409/serialnumber\n");
                    }
                    lines[a] = lines[a].replaceAll("\\s","");
                }
                if (lines[a].contains("HID")) {
                    enableAttackMode();
                } else if (lines[a].contains("OFF")) {
                    disableAttackMode();
                }
                continue;
            }

            //MOUSE or POINTER
            else if(lines[a].startsWith("MOUSE") || lines[a].startsWith("POINTER")) {
                con = lines[a];
                con = con.replace("MOUSE ","");
                con = con.replace("POINTER ","");
                String[] mbytes = {"00", "00", "00", "00"};
                int mloop;
                //CLICK or TOUCH or PRESS
                if(con.startsWith("CLICK") || con.startsWith("TOUCH") || con.startsWith("PRESS")) {
                    con = con.replace("CLICK ","");
                    con = con.replace("TOUCH ","");
                    con = con.replace("PRESS ","");
                    if(con.startsWith("LEFT")) {
                        con = con.replace("LEFT ", "");
                        mbytes[0] = "01";
                    } else if(con.startsWith("RIGHT")) {
                        con = con.replace("RIGHT ", "");
                        mbytes[0] = "02";
                    } else if(con.startsWith("MIDDLE")) {
                        con = con.replace("MIDDLE ", "");
                        mbytes[0] = "04";
                    } else mbytes[0] = "00";
                    mloop = getMouseLoopVar(con);
                    shell.addAll(mouse(mbytes, mloop));
                }
                //HOLD or DRAG
                else if(con.startsWith("HOLD") || con.startsWith("DRAG")) {
                    con = con.replace("HOLD ","");
                    con = con.replace("DRAG ","");
                    if(con.startsWith("LEFT")) {
                        con = con.replace("LEFT ", "");
                        mbytes[0] = "01";
                    } else if(con.startsWith("RIGHT")) {
                        con = con.replace("RIGHT ", "");
                        mbytes[0] = "02";
                    } else if(con.startsWith("MIDDLE")) {
                        con = con.replace("MIDDLE ", "");
                        mbytes[0] = "04";
                    } else mbytes[0] = "00";
                    String[] numParams = con.split("\\s+");
                    if(numParams.length == 2 || numParams.length == 3) {
                        if (Integer.parseInt(numParams[0]) >= -127 && Integer.parseInt(numParams[0]) <= 127)
                            mbytes[1] = String.format("%02X", Byte.parseByte(numParams[0]));
                        else mbytes[1] = "00";
                        if (Integer.parseInt(numParams[1]) >= -127 && Integer.parseInt(numParams[1]) <= 127)
                            mbytes[2] = String.format("%02X", Byte.parseByte(numParams[1]));
                        else mbytes[2] = "00";
                        if (numParams.length == 3) mloop = getMouseLoopVar(numParams[2]);
                        else mloop = 1;
                        shell.addAll(mouse(mbytes, mloop));
                    }
                }
                //MOVE or TRANSLATE
                else if(con.startsWith("MOVE") || con.startsWith("TRANSLATE")) {
                    con = con.replace("MOVE ","");
                    con = con.replace("TRANSLATE ","");
                    String[] numParams = con.split("\\s+");
                    if(numParams.length == 2 || numParams.length == 3) {
                        if (Integer.parseInt(numParams[0]) >= -127 && Integer.parseInt(numParams[0]) <= 127)
                            mbytes[1] = String.format("%02X", Byte.parseByte(numParams[0]));
                        else mbytes[1] = "00";
                        if (Integer.parseInt(numParams[1]) >= -127 && Integer.parseInt(numParams[1]) <= 127)
                            mbytes[2] = String.format("%02X", Byte.parseByte(numParams[1]));
                        else mbytes[2] = "00";
                        if (numParams.length == 3) mloop = getMouseLoopVar(numParams[2]);
                        else mloop = 1;
                        shell.addAll(mouse(mbytes, mloop));
                    }
                }
                //KNOB or WHEEL or SCROLL
                if(con.startsWith("KNOB") || con.startsWith("WHEEL") || con.startsWith("SCROLL")) {
                    con = con.replace("KNOB ","");
                    con = con.replace("WHEEL ","");
                    con = con.replace("SCROLL ","");
                    if(con.startsWith("UP")) {
                        con = con.replace("UP ", "");
                        mbytes[3] = "01";
                    } else if(con.startsWith("DOWN")) {
                        con = con.replace("DOWN ", "");
                        mbytes[3] = "FF";
                    } else mbytes[3] = "00";
                    mloop = getMouseLoopVar(con);
                    shell.addAll(mouse(mbytes, mloop));
                }
            }

            // INJECT_MOD
            else if (lines[a].startsWith("INJECT_MOD")) {
                is_inject_mod = true;
                delay = 0;
            }
            // HOLD
            else if(lines[a].startsWith("HOLD")) {
                delay = 0;
                is_hold = true;
                con = lines[a].replace("HOLD ","");
                parse(con);
            }
            // RELEASE
            else if(lines[a].startsWith("RELEASE")) {
                delay = 0;
                is_release = true;
                con = lines[a].replace("RELEASE ","");
                parse(con);
            }

            // Modifier Keys
            else if (lines[a].startsWith("GUI") || lines[a].startsWith("WINDOWS") ||
                    lines[a].startsWith("META") || lines[a].startsWith("COMMAND") ||
                    lines[a].startsWith("SHIFT") || lines[a].startsWith("ALT") || lines[a].startsWith("OPTION") ||
                    lines[a].startsWith("CTRL") || lines[a].startsWith("CONTROL")) {
                int modifierValue = 0;
                con = lines[a].replace("-", " ");
                if (con.contains("GUI") || con.contains("WINDOWS") || con.contains("META") || con.contains("COMMAND")) {
                    con = con.replace("GUI ", "");
                    con = con.replace("WINDOWS ", "");
                    con = con.replace("META ", "");
                    con = con.replace("COMMAND ", "");
                    modifierValue |= Integer.parseInt(Objects.requireNonNull(keys.get("MODIFIERKEY_GUI")), 16);
                }
                if (con.contains("SHIFT")) {
                    con = con.replace("SHIFT ", "");
                    modifierValue |= Integer.parseInt(Objects.requireNonNull(keys.get("MODIFIERKEY_SHIFT")), 16);
                }
                if (con.contains("ALT") || lines[a].startsWith("OPTION")) {
                    con = con.replace("ALT ", "");
                    con = con.replace("OPTION ", "");
                    modifierValue |= Integer.parseInt(Objects.requireNonNull(keys.get("MODIFIERKEY_ALT")), 16);
                }
                if (con.contains("CTRL") || con.contains("CONTROL") || con.contains("META") || con.contains("COMMAND")) {
                    con = con.replace("CTRL ", "");
                    con = con.replace("CONTROL ", "");
                    modifierValue |= Integer.parseInt(Objects.requireNonNull(keys.get("MODIFIERKEY_CTRL")), 16);
                }
                if (is_hold) {
                    delay = defdelay;
                    modKeypress = String.format("%02x", (modifierValue | Integer.parseInt(modKeypress, 16)));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_inject_mod = false;
                    is_hold = false;
                } else if (is_release) {
                    delay = defdelay;
                    modKeypress = String.format("%02x", Integer.parseInt(modKeypress, 16)&(~(modifierValue)));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else if (is_inject_mod) {
                    delay = defdelay;
                    modKeyInjected = String.format("%02x", (modifierValue));
                    modKeypress = String.format("%02x", (modifierValue | Integer.parseInt(modKeypress, 16)));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                } else {
                    modKeypress = String.format("%02x", (modifierValue | Integer.parseInt(modKeypress, 16)));
                    if (!con.trim().isEmpty()) {
                        keypress.push(comboKeys(con));
                    }
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    modKeypress = String.format("%02x", Integer.parseInt(modKeypress, 16)&(~(modifierValue)));
                    if (!con.trim().isEmpty()) {
                        keypress.pop();
                    }
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                }
            }

            // FUNCTION
            else if (lines[a].startsWith("FUNCTION")) {
                lines[a] = lines[a].replace("FUNCTION ","");
                lines[a] = lines[a]+" {\n";
                shell.add(insert_intent()+lines[a]);
                intent += 1;
            } else if (lines[a].startsWith("CALL_FUNCTION")) {
                lines[a] = lines[a].replace("CALL_FUNCTION ","");
                shell.add(insert_intent()+lines[a]+"\n");
            } else if (lines[a].startsWith("END_FUNCTION")) {
                intent -= 1;
                shell.add(insert_intent()+"}\n");
            }

            // RETURN
            else if (lines[a].startsWith("RETURN")) {
                lines[a] = lines[a].replace("RETURN","return");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                shell.add(insert_intent()+lines[a]+"\n");
            }

            // REPEAT
            else if (lines[a].startsWith("REPEAT")) {
                lines[a] = lines[a].replace("REPEAT ","");
                con = insert_intent()+"for repeat_var in {1.."+lines[a]+"}; do\n";
                shell.add(con);
                intent += 1;
            } else if (lines[a].startsWith("END_REPEAT")) {
                intent -= 1;
                shell.add(insert_intent()+"done\n");
            }

            // WHILE
            else if (lines[a].startsWith("WHILE (") && lines[a].endsWith(")")) {
                lines[a] = lines[a].replaceAll("WHILE \\( *", "while [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll(" *\\)$",")) == 1 ]; do");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("WHILE $")) {
                lines[a] = lines[a].replaceAll("WHILE +", "while [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                lines[a] = insert_intent()+lines[a]+")) == 1 ]; do";
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("WHILE TRUE")) {
                shell.add(insert_intent()+"while [ $((1)) == 1 ]; do\n");
                intent += 1;
            } else if (lines[a].startsWith("WHILE FALSE")) {
                shell.add(insert_intent()+"while [ $((0)) == 1 ]; do\n");
                intent += 1;
            } else if (lines[a].startsWith("END_WHILE")) {
                intent -= 1;
                shell.add(insert_intent()+"done\n");
            }

            // IF/ELSE
            else if (lines[a].startsWith("IF (") && lines[a].endsWith(") THEN")) {
                lines[a] = lines[a].replaceAll("IF \\( *", "if [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll(" *\\) THEN",")) == 1 ]; then");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("IF $") && lines[a].endsWith(" THEN")) {
                lines[a] = lines[a].replaceAll("IF +", "if [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll(" +THEN",")) == 1 ]; then");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                lines[a] = insert_intent()+lines[a]+")) == 1 ]; do";
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("ELSE IF (") && lines[a].endsWith(") THEN")) {
                lines[a] = lines[a].replaceAll("ELSE IF \\( *", "elif [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll(" *\\) THEN",")) == 1 ]; then");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                intent -= 1;
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("ELSE IF $") && lines[a].endsWith(" THEN")) {
                lines[a] = lines[a].replaceAll("ELSE IF +", "elif [");
                lines[a] = lines[a].replace("[", "[ $((");
                lines[a] = lines[a].replaceAll(" +THEN",")) == 1 ]; then");
                lines[a] = lines[a].replaceAll("(?i)true","1");
                lines[a] = lines[a].replaceAll("(?i)false","0");
                lines[a] = insert_intent()+lines[a]+")) == 1 ]; do";
                intent -= 1;
                shell.add(insert_intent()+lines[a]+"\n");
                intent += 1;
            } else if (lines[a].startsWith("ELSE")) {
                intent -= 1;
                shell.add(insert_intent()+"else\n");
                intent += 1;
            } else if (lines[a].startsWith("END_IF")) {
                intent -= 1;
                shell.add(insert_intent()+"fi\n");
            }

            // STRING/STRINGLN/STRING_DELAY/STRINGDELAY
            else if(lines[a].startsWith("STRING") || lines[a].startsWith("STRINGLN") || lines[a].startsWith("STRINGDELAY") || lines[a].startsWith("STRING_DELAY")) {
                float stringDelay = 0;
                boolean add_enter = false;
                con = lines[a];
                if (lines[a].startsWith("STRINGLN")) {
                    add_enter = true;
                    con = con.replace("STRINGLN ","");
                } else if(lines[a].contains("DELAY")) {
                    con = con.replace("STRINGDELAY ","");
                    con = con.replace("STRING_DELAY ","");
                    stringDelay = Long.parseLong(con.split(" ")[0]);
                    con = con.split(" ")[1];
                } else {
                    con = con.replace("STRING ","");
                }
                for (char aCh: con.toCharArray()) {
                    if (keymap.containsKey(aCh)) {
                        shell.add(keymap.get(aCh));
                        if (stringDelay != 0) {
                            shell.add(insert_intent()+"sleep " + stringDelay/1000 + "\n");
                        }
                    }
                }
                if (add_enter) {
                    keypress.add(keys.get("KEY_ENTER"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_ENTER"));
                }
                shell.add(keyboard());
            }

            //DOWNARROW or DOWN
            else if (lines[a].equals("DOWN") || lines[a].equals("DOWNARROW")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_DOWN"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_DOWN"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_DOWN"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_DOWN"));
                    shell.add(keyboard());
                }
            }
            //UPARROW or UP
            else if (lines[a].equals("UP") || lines[a].equals("UPARROW")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_UP"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_UP"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_UP"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_UP"));
                    shell.add(keyboard());
                }
            }
            //LEFTARROW or LEFT
            else if (lines[a].equals("LEFT") || lines[a].equals("LEFTARROW")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_LEFT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_LEFT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_LEFT"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_LEFT"));
                    shell.add(keyboard());
                }
            }
            //RIGHTARROW or RIGHT
            else if (lines[a].equals("RIGHT") || lines[a].equals("RIGHTARROW")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_RIGHT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_RIGHT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_RIGHT"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_RIGHT"));
                    shell.add(keyboard());
                }
            }

            //PAGEUP
            else if (lines[a].equals("PAGEUP")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_PAGEUP"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_PAGEUP"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_PAGEUP"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_PAGEUP"));
                    shell.add(keyboard());
                }
            }
            //PAGEDOWN
            else if (lines[a].equals("PAGEDOWN")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_PAGEDOWN"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_PAGEDOWN"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_PAGEDOWN"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_PAGEDOWN"));
                    shell.add(keyboard());
                }
            }
            //END
            else if (lines[a].equals("END")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_END"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_END"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_END"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_END"));
                    shell.add(keyboard());
                }
            }
            //HOME
            else if (lines[a].equals("HOME")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_HOME"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_HOME"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_HOME"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_HOME"));
                    shell.add(keyboard());
                }
            }

            //INSERT/INS
            else if (lines[a].equals("INSERT") || lines[a].equals("INS")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_INSERT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_INSERT"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_INSERT"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_INSERT"));
                    shell.add(keyboard());
                }
            }
            //DELETE/DEL
            else if (lines[a].equals("DELETE") || lines[a].equals("DEL")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_DELETE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_DELETE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_DELETE"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_DELETE"));
                    shell.add(keyboard());
                }
            }
            //BACKSPACE or BKSP
            else if (lines[a].equals("BACKSPACE") || lines[a].equals("BKSP")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_BACKSPACE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_BACKSPACE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_BACKSPACE"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_BACKSPACE"));
                    shell.add(keyboard());
                }
            }
            //TAB
            else if (lines[a].equals("TAB")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_TAB"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_TAB"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_TAB"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_TAB"));
                    shell.add(keyboard());
                }
            }
            //SPACE
            else if (lines[a].equals("SPACE")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_SPACE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_SPACE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_SPACE"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_SPACE"));
                    shell.add(keyboard());
                }
            }
            //ENTER
            else if (lines[a].equals("ENTER")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_ENTER"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_ENTER"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_ENTER"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_ENTER"));
                    shell.add(keyboard());
                }
            }
            //ESC or ESCAPE
            else if (lines[a].equals("ESCAPE") || lines[a].equals("ESC")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_ESC"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_ESC"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_ESC"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_ESC"));
                    shell.add(keyboard());
                }
            }
            //BREAK or PAUSE
            else if (lines[a].equals("BREAK") || lines[a].equals("PAUSE")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_PAUSE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_PAUSE"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_PAUSE"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_PAUSE"));
                    shell.add(keyboard());
                }
            }
            //PRINTSCREEN or PRINTSCRN or PRNTSCRN or PRTSCN or PRSC or PRTSCR
            else if (lines[a].equals("PRINTSCREEN") || lines[a].equals("PRINTSCRN") ||
                    lines[a].equals("PRNTSCRN") || lines[a].equals("PRTSCN") ||
                    lines[a].equals("PRSC") || lines[a].equals("PRTSCR")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_SYSRQ"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_SYSRQ"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_SYSRQ"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_SYSRQ"));
                    shell.add(keyboard());
                }
            }
            //MENU or APP
            else if (lines[a].equals("MENU") || lines[a].equals("APP")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F10"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_SHIFT"), keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F10"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F10"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_SHIFT"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F10"));
                    shell.add(keyboard());
                }
            }

            //F1
            else if (lines[a].equals("F1")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F1"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F1"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F1"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F1"));
                    shell.add(keyboard());
                }
            }
            //F2
            else if (lines[a].equals("F2")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F2"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F2"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F2"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F2"));
                    shell.add(keyboard());
                }
            }
            //F3
            else if (lines[a].equals("F3")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F3"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F3"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F3"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F3"));
                    shell.add(keyboard());
                }
            }
            //F4
            else if (lines[a].equals("F4")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F4"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F4"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F4"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F4"));
                    shell.add(keyboard());
                }
            }
            //F5
            else if (lines[a].equals("F5")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F5"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F5"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F5"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F5"));
                    shell.add(keyboard());
                }
            }
            //F6
            else if (lines[a].equals("F6")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F6"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F6"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F6"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F6"));
                    shell.add(keyboard());
                }
            }
            //F7
            else if (lines[a].equals("F7")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F7"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F7"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F7"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F7"));
                    shell.add(keyboard());
                }
            }
            //F8
            else if (lines[a].equals("F8")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F8"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F8"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F8"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F8"));
                    shell.add(keyboard());
                }
            }
            //F9
            else if (lines[a].equals("F9")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F9"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F9"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F9"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F9"));
                    shell.add(keyboard());
                }
            }
            //F10
            else if (lines[a].equals("F10")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F10"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F10"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F10"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F10"));
                    shell.add(keyboard());
                }
            }
            //F11
            else if (lines[a].equals("F11")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F11"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F11"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F11"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F11"));
                    shell.add(keyboard());
                }
            }
            //F12
            else if (lines[a].equals("F12")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F12"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F12"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F12"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F12"));
                    shell.add(keyboard());
                }
            }
            //F13
            else if (lines[a].equals("F13")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F13"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F13"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F13"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F13"));
                    shell.add(keyboard());
                }
            }
            //F14
            else if (lines[a].equals("F14")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F14"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F14"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F14"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F14"));
                    shell.add(keyboard());
                }
            }
            //F15
            else if (lines[a].equals("F15")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F15"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F15"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F15"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F15"));
                    shell.add(keyboard());
                }
            }
            //F16
            else if (lines[a].equals("F16")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F16"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F16"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F16"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F16"));
                    shell.add(keyboard());
                }
            }
            //F17
            else if (lines[a].equals("F17")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F17"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F17"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F17"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F17"));
                    shell.add(keyboard());
                }
            }
            //F18
            else if (lines[a].equals("F18")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F18"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F18"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F18"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F18"));
                    shell.add(keyboard());
                }
            }
            //F19
            else if (lines[a].equals("F19")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F19"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F19"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F19"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F19"));
                    shell.add(keyboard());
                }
            }
            //F20
            else if (lines[a].equals("F20")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F20"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F20"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F20"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F20"));
                    shell.add(keyboard());
                }
            }
            //F21
            else if (lines[a].equals("F21")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F21"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F21"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F21"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F21"));
                    shell.add(keyboard());
                }
            }
            //F22
            else if (lines[a].equals("F22")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F22"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F22"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F22"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F22"));
                    shell.add(keyboard());
                }
            }
            //F23
            else if (lines[a].equals("F23")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F23"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F23"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F23"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F23"));
                    shell.add(keyboard());
                }
            }
            //F24
            else if (lines[a].equals("F24")) {
                if (is_hold) {
                    keypress.push(keys.get("KEY_F24"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_hold = false;
                } else if (is_release) {
                    keypress.remove(keys.get("KEY_F24"));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    is_release = false;
                } else {
                    keypress.push(keys.get("KEY_F24"));
                    shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                    keypress.remove(keys.get("KEY_F24"));
                    shell.add(keyboard());
                }
            }

            //NUMLOCK
            else if (lines[a].equals("NUMLOCK")) {
                keypress.push(keys.get("KEY_NUMLOCK"));
                shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                keypress.remove(keys.get("KEY_NUMLOCK"));
                shell.add(keyboard());
            }
            //CAPSLOCK
            else if (lines[a].equals("CAPSLOCK")) {
                keypress.push(keys.get("KEY_CAPSLOCK"));
                shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                keypress.remove(keys.get("KEY_CAPSLOCK"));
                shell.add(keyboard());
            }
            //SCROLLLOCK
            else if (lines[a].equals("SCROLLLOCK")) {
                keypress.push(keys.get("KEY_SCROLLLOCK"));
                shell.add(keyboard(keys.get("MODIFIERKEY_RESET"), keypress.toArray(new String[0])));
                keypress.remove(keys.get("KEY_SCROLLLOCK"));
                shell.add(keyboard());
            }

            else {
                if (is_hold) {
                    for (char aCh: lines[a].toCharArray()) {
                        keypress.push(comboKeys(String.valueOf(aCh)));
                        shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    }
                    is_hold = false;
                } else if (is_release) {
                    for (char aCh: lines[a].toCharArray()) {
                        keypress.remove(comboKeys(String.valueOf(aCh)));
                        shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    }
                    is_release = false;
                } else {
                    for (char aCh: lines[a].toCharArray()) {
                        keypress.push(comboKeys(String.valueOf(aCh)));
                        shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                        keypress.remove(comboKeys(String.valueOf(aCh)));
                        shell.add(keyboard());
                    }
                }
                delay = 0;
            }


            if(delay > 0.0) {
                shell.add(insert_intent()+"sleep " + delay/1000 + "\n");
                if(remove_inject) {
                    modKeypress = String.format("%02x", (Integer.parseInt(modKeypress, 16)&~(Integer.parseInt(modKeyInjected, 16))));
                    shell.add(keyboard(modKeypress, keypress.toArray(new String[0])));
                    remove_inject = false;
                }
            }
        }
    }

}
