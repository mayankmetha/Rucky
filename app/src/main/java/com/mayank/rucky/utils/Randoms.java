package com.mayank.rucky.utils;

import java.util.Date;
import java.security.SecureRandom;

public class Randoms {

    SecureRandom random;

    Randoms() {
        random = new SecureRandom();
        Date date = new Date();
        random.setSeed(date.getTime());
    }

    public int randomTheme() {
        return random.nextInt(Constants.themeList.length);
    }
}
