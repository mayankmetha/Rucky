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

    public KeyModel(char key, String keyname, int keycode, int ctrl, int shift, int alt, int meta) {
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

    public String getCtrl(int modifier) {
        if ((modifier & 1) == 1) return "left";
        else if (((modifier>>4) & 1) == 1) return "right";
        else return "none";
    }

    public String getShift(int modifier) {
        if (((modifier>>1) & 1) == 1) return "left";
        else if (((modifier>>5) & 1) == 1) return "right";
        else return "none";
    }

    public String getAlt(int modifier) {
        if (((modifier>>2) & 1) == 1) return "left";
        else if (((modifier>>6) & 1) == 1) return "right";
        else return "none";
    }

    public String getMeta(int modifier) {
        if (((modifier>>3) & 1) == 1) return "left";
        else if (((modifier>>7) & 1) == 1) return "right";
        else return "none";
    }

    public int getCtrlInt(int modifier) {
        if ((modifier & 1) == 1) return -1;
        else if (((modifier>>4) & 1) == 1) return 1;
        else return 0;
    }

    public int getShiftInt(int modifier) {
        if (((modifier>>1) & 1) == 1) return -1;
        else if (((modifier>>5) & 1) == 1) return 1;
        else return 0;
    }

    public int getAltInt(int modifier) {
        if (((modifier>>2) & 1) == 1) return -1;
        else if (((modifier>>6) & 1) == 1) return 1;
        else return 0;
    }

    public int getMetaInt(int modifier) {
        if (((modifier>>3) & 1) == 1) return -1;
        else if (((modifier>>7) & 1) == 1) return 1;
        else return 0;
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

    public int keyModifierGen(int ctrl, int shift, int alt, int meta) {
        int mod = 0x00;

        // left = -1
        // right = 1

        if(ctrl == -1)
            mod |= 1;
        else if(ctrl == 1)
            mod |= (1<<4);

        if(shift == -1)
            mod |= (1<<1);
        else if(shift == 1)
            mod |= (1<<5);

        if(alt == -1)
            mod |= (1<<2);
        else if(alt == 1)
            mod |= (1<<6);

        if(meta == -1)
            mod |= (1<<3);
        else if(meta == 1)
            mod |= (1<<7);

        return mod;
    }

    public boolean exists(KeyModel key) {
        if (key.getKey() == this.key)
            return true;
        else return key.getKeycode() == this.keycode && key.getModifier() == this.modifier;
    }

}
