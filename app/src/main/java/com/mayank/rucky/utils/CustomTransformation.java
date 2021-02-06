package com.mayank.rucky.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CustomTransformation implements ViewPager2.PageTransformer{
    @Override
    public void transformPage(@NonNull View page, float position) {

        page.setCameraDistance(20000);

        if (position < -1) {
            page.setAlpha(0);
        } else if (position <= 0) {
            page.setAlpha(1);
            page.setRotationY(90*Math.abs(position));
        } else if (position <= 1) {
            page.setAlpha(1);
            page.setRotationY(-90*Math.abs(position));
        } else {
            page.setAlpha(0);
        }

        if (Math.abs(position) <= 1) {
            page.setScaleY(Math.max(.4f,1-Math.abs(position)));
        }
    }
}