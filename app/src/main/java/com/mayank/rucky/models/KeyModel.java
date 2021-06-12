package com.mayank.rucky.models;

public class KeyModel {

    char key;
    String keyname;
    int keycode;
    int modifier;

    public KeyModel(char key, String keyname, int keycode, int modifier) {
        this.key = key;
        this.keyname = keyname;
        this.keycode = keycode;
        this.modifier = modifier;
    }

    public KeyModel(char key, String keyname, int keycode, String ctrl, String shift, String alt, String meta) {
        this.key = key;
        this.keyname = keyname;
        this.keycode = keycode;
        this.modifier = keyModifierGen(ctrl,shift,alt,meta);
    }

    public void setKey(char key) {
        this.key = key;
    }

    public char getKey() {
        return key;
    }

    public void setKeyName(String keyname) {
        this.keyname = keyname;
    }

    public String getKeyName() {
        return keyname;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public int getKeycode() {
        return keycode;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getModifier() {
        return modifier;
    }

    public int keyModifierGen(String ctrl, String shift, String alt, String meta) {
        int mod = 0x00;

        if(ctrl.equalsIgnoreCase("left"))
            mod |= 1;
        else if(ctrl.equalsIgnoreCase("right"))
            mod |= (1<<4);

        if(shift.equalsIgnoreCase("left"))
            mod |= (1<<1);
        else if(shift.equalsIgnoreCase("right"))
            mod |= (1<<5);

        if(alt.equalsIgnoreCase("left"))
            mod |= (1<<2);
        else if(alt.equalsIgnoreCase("right"))
            mod |= (1<<6);

        if(meta.equalsIgnoreCase("left"))
            mod |= (1<<3);
        else if(meta.equalsIgnoreCase("right"))
            mod |= (1<<7);

        return mod;
    }

}
