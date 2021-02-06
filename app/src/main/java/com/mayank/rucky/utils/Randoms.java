package com.mayank.rucky.utils;

import java.util.Date;
import java.util.Random;

public class Randoms {

    Random random;

    Randoms() {
        random = new Random();
        Date date = new Date();
        random.setSeed(date.getTime());
    }

    public int randomTheme() {
        return random.nextInt(Constants.themeList.length);
    }
}
