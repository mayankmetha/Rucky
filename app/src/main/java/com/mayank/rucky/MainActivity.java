package com.mayank.rucky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Runtime.getRuntime().exec("sh -c su");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
