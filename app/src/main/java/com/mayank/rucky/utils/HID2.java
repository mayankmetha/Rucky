package com.mayank.rucky.utils;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;

import com.mayank.rucky.models.KeyModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HID2 {

    private float defdelay = 0;
    private ArrayList<String> shell = new ArrayList<>();
    private HashMap<String, String> keys = new HashMap<>();
    private HashMap<Character, String> keymap = new HashMap<>();

    public HID2(String jsonStr) {
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
                    keymap.put(keymodel.getKey(),keyboard(String.format("%02X",keymodel.getKeycode()),String.format("%02X",keymodel.getModifier())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initKey() {
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
            case "insert": return keys.get("KEY_INSERT");
            case "delete": return keys.get("KEY_DELETE");
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

    private ArrayList<String> kblangbytes(long stringDelay, @NonNull String str) {
        ArrayList<String> kbstrokes = new ArrayList<>();
        char[] ch = str.toCharArray();
        for (char aCh: ch) {
            if(keymap.containsKey(aCh)) {
                kbstrokes.add(keymap.get(aCh));
                if (stringDelay != 0) {
                    kbstrokes.add("sleep " + (((float)stringDelay) / 1000) + "\n");
                }
            }
        }
        return kbstrokes;
    }

    private String keyboard(@NonNull String... args) {
        String tmp;
        if(args.length == 1) {
            tmp = "echo \"\\\\x00\\\\x00\\\\x"+args[0]+"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            tmp += "echo \"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
        } else if(args.length == 2) {
            tmp = "echo \"\\\\x"+args[1]+"\\\\x00\\\\x"+args[0]+"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
            tmp += "echo \"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
        } else {
            tmp = "echo \"\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg0\n";
        }
        return tmp;
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
            String tmp = "echo \"\\\\x" + mbytes[0] + "\\\\x" + mbytes[1] + "\\\\x" + mbytes[2] + "\\\\x" + mbytes[3] + "\" > /dev/hidg1\n";
            //tmp += "echo \"\\\\x00\\\\x00\\\\x00\\\\x00\" > /dev/hidg1\n";
            tmpStr.add(tmp);
        }
        return tmpStr;
    }

    public void parse(@NonNull String str) {
        str = str.replaceAll(" +"," ");
        String[] lines = str.split("\\r?\\n");
        String con;
        for (int a = 0; a < lines.length; a++) {
            lines[a] = lines[a].trim();
            //DEFAULTDELAY or DEFAULT_DELAY
            if (a == 0 && (lines[a].startsWith("DEFAULTDELAY") || lines[a].startsWith("DEFAULT_DELAY"))) {
                con = lines[a];
                con = con.replace("DEFAULTDELAY ", "");
                con = con.replace("DEFAULT_DELAY ", "");
                defdelay = Float.parseFloat(con) / 1000;
            }
            //DELAY
            else if (lines[a].startsWith("DELAY")) {
                con = lines[a].replace("DELAY ", "");
                float delay = Float.parseFloat(con) / 1000;
                shell.add("sleep " + delay + "\n");
            }
            //REM
            else if (lines[a].startsWith("REM")) {
                continue;
            }
            //REPEAT
            else if (lines[a].startsWith("REPEAT")) {
                con = lines[a].replace("REPEAT ", "");
                int x = parseInt(con);
                for (int i = 0; i < x; i++) {
                    parse(lines[a - 1]);
                }
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
            //STRING or STRING_DELAY or STRINGDELAY
            else if (lines[a].startsWith("STRING") || lines[a].startsWith("STRINGDELAY") || lines[a].startsWith("STRING_DELAY")) {
                long stringDelay = 0;
                if(lines[a].contains("DELAY")) {
                    con = lines[a];
                    con = con.replace("STRINGDELAY ","");
                    con = con.replace("STRING_DELAY ","");
                    stringDelay = Long.parseLong(con.split(" ",2)[0]);
                    con = con.split(" ",2)[1];
                } else {
                    con = lines[a].replace("STRING ", "");
                }
                shell.addAll(kblangbytes(stringDelay, con));
            }
            //GUI or WINDOWS or COMMAND or META
            else if (lines[a].startsWith("GUI") || lines[a].startsWith("WINDOWS") ||
                    lines[a].startsWith("META") || lines[a].startsWith("COMMAND")) {
                con = lines[a];
                con = con.replace("GUI ","");
                con = con.replace("WINDOWS ", "");
                con = con.replace("META ","");
                con = con.replace("COMMAND ", "");
                shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_GUI")));
            }
            //SHIFT & SHIFT+GUI & SHIFT+ALT & SHIFT+CTRL & SHIFT+CONTROL
            else if (lines[a].startsWith("SHIFT")) {
                con = lines[a];
                con = con.replace("SHIFT ","");
                if (con.startsWith("GUI") || con.startsWith("WINDOWS") ||
                        con.startsWith("META") || con.startsWith("COMMAND")) {
                    con = con.replace("GUI ","");
                    con = con.replace("WINDOWS ", "");
                    con = con.replace("META ","");
                    con = con.replace("COMMAND ", "");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_SHIFT_GUI")));
                } else if (con.startsWith("ALT")) {
                    con = con.replace("ALT ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_ALT_SHIFT")));
                } else if (con.startsWith("CTRL") || con.startsWith("CONTROL")) {
                    con = con.replace("CTRL ","");
                    con = con.replace("CONTROL ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_CTRL_SHIFT")));
                } else {
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_SHIFT")));
                }
            }
            //ALT & ALT+SHIFT & ALT+CTRL & ALT+CONTROL
            else if (lines[a].startsWith("ALT")) {
                con = lines[a];
                con = con.replace("ALT ","");
                if (con.startsWith("CTRL") || con.startsWith("CONTROL")) {
                    con = con.replace("CTRL ","");
                    con = con.replace("CONTROL ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_CTRL_ALT")));
                } else if (con.startsWith("SHIFT")) {
                    con = con.replace("SHIFT ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_ALT_SHIFT")));
                } else {
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_ALT")));
                }
            }
            //CONTROL or CTRL & CONTROL+ALT or CTRL+ALT & CONTROL+SHIFT or CTRL+SHIFT
            else if (lines[a].startsWith("CTRL") || lines[a].startsWith("CONTROL")) {
                con = lines[a];
                con = con.replace("CTRL ","");
                con = con.replace("CONTROL ","");
                if (con.startsWith("ALT")) {
                    con = con.replace("ALT ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_CTRL_ALT")));
                } else if (con.startsWith("SHIFT")) {
                    con = con.replace("SHIFT ","");
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_CTRL_SHIFT")));
                } else {
                    shell.add(keyboard(comboKeys(con),keys.get("MODIFIERKEY_CTRL")));
                }
            }
            //MENU or APP
            else if (lines[a].equals("MENU") || lines[a].equals("APP")) {
                shell.add(keyboard(keys.get("KEY_F10"),keys.get("MODIFIERKEY_SHIFT")));
            }
            //DOWNARROW or DOWN
            else if (lines[a].equals("DOWN") || lines[a].equals("DOWNARROW")) {
                shell.add(keyboard(keys.get("KEY_DOWN")));
            }
            //UPARROW or UP
            else if (lines[a].equals("UP") || lines[a].equals("UPARROW")) {
                shell.add(keyboard(keys.get("KEY_UP")));
            }
            //LEFTARROW or LEFT
            else if (lines[a].equals("LEFT") || lines[a].equals("LEFTARROW")) {
                shell.add(keyboard(keys.get("KEY_LEFT")));
            }
            //RIGHTARROW or RIGHT
            else if (lines[a].equals("RIGHT") || lines[a].equals("RIGHTARROW")) {
                shell.add(keyboard(keys.get("KEY_RIGHT")));
            }
            //DELETE
            else if (lines[a].equals("DELETE")) {
                shell.add(keyboard(keys.get("KEY_DELETE")));
            }
            //END
            else if (lines[a].equals("END")) {
                shell.add(keyboard(keys.get("KEY_END")));
            }
            //HOME
            else if (lines[a].equals("HOME")) {
                shell.add(keyboard(keys.get("KEY_HOME")));
            }
            //INSERT
            else if (lines[a].equals("INSERT")) {
                shell.add(keyboard(keys.get("KEY_INSERT")));
            }
            //PAGEUP
            else if (lines[a].equals("PAGEUP")) {
                shell.add(keyboard(keys.get("KEY_PAGEUP")));
            }
            //PAGEDOWN
            else if (lines[a].equals("PAGEDOWN")) {
                shell.add(keyboard(keys.get("KEY_PAGEDOWN")));
            }
            //PRINTSCREEN or PRINTSCRN or PRNTSCRN or PRTSCN or PRSC or PRTSCR
            else if (lines[a].equals("PRINTSCREEN") || lines[a].equals("PRINTSCRN") ||
                    lines[a].equals("PRNTSCRN") || lines[a].equals("PRTSCN") ||
                    lines[a].equals("PRSC") || lines[a].equals("PRTSCR")) {
                shell.add(keyboard(keys.get("KEY_SYSRQ")));
            }
            //BREAK or PAUSE
            else if (lines[a].equals("BREAK") || lines[a].equals("PAUSE")) {
                shell.add(keyboard(keys.get("KEY_PAUSE")));
            }
            //NUMLOCK
            else if (lines[a].equals("NUMLOCK")) {
                shell.add(keyboard(keys.get("KEY_NUMLOCK")));
            }
            //CAPSLOCK
            else if (lines[a].equals("CAPSLOCK")) {
                shell.add(keyboard(keys.get("KEY_CAPSLOCK")));
            }
            //SCROLLLOCK
            else if (lines[a].equals("SCROLLLOCK")) {
                shell.add(keyboard(keys.get("KEY_SCROLLLOCK")));
            }
            //ESC or ESCAPE
            else if (lines[a].equals("ESCAPE") || lines[a].equals("ESC")) {
                shell.add(keyboard(keys.get("KEY_ESC")));
            }
            //SPACE
            else if (lines[a].equals("SPACE")) {
                shell.add(keyboard(keys.get("KEY_SPACE")));
            }
            //TAB
            else if (lines[a].equals("TAB")) {
                shell.add(keyboard(keys.get("KEY_TAB")));
            }
            //BACKSPACE or BKSP
            else if (lines[a].equals("BACKSPACE") || lines[a].equals("BKSP")) {
                shell.add(keyboard(keys.get("KEY_BACKSPACE")));
            }
            //ENTER
            else if (lines[a].equals("ENTER")) {
                shell.add(keyboard(keys.get("KEY_ENTER")));
            }
            //F1
            else if (lines[a].equals("F1")) {
                shell.add(keyboard(keys.get("KEY_F1")));
            }
            //F2
            else if (lines[a].equals("F2")) {
                shell.add(keyboard(keys.get("KEY_F2")));
            }
            //F3
            else if (lines[a].equals("F3")) {
                shell.add(keyboard(keys.get("KEY_F3")));
            }
            //F4
            else if (lines[a].equals("F4")) {
                shell.add(keyboard(keys.get("KEY_F4")));
            }
            //F5
            else if (lines[a].equals("F5")) {
                shell.add(keyboard(keys.get("KEY_F5")));
            }
            //F6
            else if (lines[a].equals("F6")) {
                shell.add(keyboard(keys.get("KEY_F6")));
            }
            //F7
            else if (lines[a].equals("F7")) {
                shell.add(keyboard(keys.get("KEY_F7")));
            }
            //F8
            else if (lines[a].equals("F8")) {
                shell.add(keyboard(keys.get("KEY_F8")));
            }
            //F9
            else if (lines[a].equals("F9")) {
                shell.add(keyboard(keys.get("KEY_F9")));
            }
            //F10
            else if (lines[a].equals("F10")) {
                shell.add(keyboard(keys.get("KEY_F10")));
            }
            //F11
            else if (lines[a].equals("F11")) {
                shell.add(keyboard(keys.get("KEY_F11")));
            }
            //F12
            else if (lines[a].equals("F12")) {
                shell.add(keyboard(keys.get("KEY_F12")));
            }
            //F13
            else if (lines[a].equals("F13")) {
                shell.add(keyboard(keys.get("KEY_F13")));
            }
            //F14
            else if (lines[a].equals("F14")) {
                shell.add(keyboard(keys.get("KEY_F14")));
            }
            //F15
            else if (lines[a].equals("F15")) {
                shell.add(keyboard(keys.get("KEY_F15")));
            }
            //F16
            else if (lines[a].equals("F16")) {
                shell.add(keyboard(keys.get("KEY_F16")));
            }
            //F17
            else if (lines[a].equals("F17")) {
                shell.add(keyboard(keys.get("KEY_F17")));
            }
            //F18
            else if (lines[a].equals("F18")) {
                shell.add(keyboard(keys.get("KEY_F18")));
            }
            //F19
            else if (lines[a].equals("F19")) {
                shell.add(keyboard(keys.get("KEY_F19")));
            }
            //F20
            else if (lines[a].equals("F20")) {
                shell.add(keyboard(keys.get("KEY_F20")));
            }
            //F21
            else if (lines[a].equals("F21")) {
                shell.add(keyboard(keys.get("KEY_F21")));
            }
            //F22
            else if (lines[a].equals("F22")) {
                shell.add(keyboard(keys.get("KEY_F22")));
            }
            //F23
            else if (lines[a].equals("F23")) {
                shell.add(keyboard(keys.get("KEY_F23")));
            }
            //F24
            else if (lines[a].equals("F24")) {
                shell.add(keyboard(keys.get("KEY_F24")));
            }
            shell.add("sleep " + defdelay + "\n");
        }
    }

    public ArrayList<String> getCmd() {
        return shell;
    }

}
