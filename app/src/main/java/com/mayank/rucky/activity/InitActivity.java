package com.mayank.rucky.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mayank.rucky.R;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.Constants;
import com.mayank.rucky.utils.CustomTransformation;

import java.util.Timer;
import java.util.TimerTask;

public class InitActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private Config config;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        config = new Config(this);
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideNavigationBar();
        setTheme(Constants.themeList[config.getAccentTheme()]);
        config.setAccentTheme(config.getAccentTheme());
        setContentView(R.layout.activity_init);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        CustomTransformation transformation = new CustomTransformation();
        viewPager.setPageTransformer(transformation);

        layouts = new int[]{
                R.layout.activity_init_slide1,
                R.layout.activity_init_slide2,
                R.layout.activity_init_slide3,
                R.layout.activity_init_slide4,
                R.layout.activity_init_slide5,
                R.layout.activity_init_slide6
        };

        addBottomDots(0);

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> launchHomeScreen());

        btnNext.setOnClickListener(v -> {
            int current = getItem();
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (config.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(Constants.themeList[config.getAccentTheme()]);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar();
    }

    public void hideNavigationBar() {
        final View decorView = this.getWindow().getDecorView();
        final int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                InitActivity.this.runOnUiThread(() -> decorView.setSystemUiVisibility(uiOptions));
            }
        };
        timer.scheduleAtFixedRate(task, 1, 2);

    }

    @SuppressLint("ResourceType")
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.foreground));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            final TypedValue value = new TypedValue();
            TypedArray typedArray = this.getTheme().obtainStyledAttributes(value.data,new int[]{ R.attr.appColorAccent });
            dots[currentPage].setTextColor(typedArray.getColor(0, 0));
        }
    }

    private int getItem() {
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen() {
        config.setInitState(false);
        startActivity(new Intent(InitActivity.this, EditorActivity.class));
        finish();
    }

    ViewPager2.OnPageChangeCallback viewPagerPageChangeListener = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.init_start));
                btnSkip.setVisibility(View.GONE);
            } else if (position == 3 || position == 4) {
                btnNext.setText(getString(R.string.init_agree));
                btnSkip.setVisibility(View.VISIBLE);
            } else {
                btnNext.setText(getString(R.string.init_next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
    };

    public class MyViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            assert layoutInflater != null;
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {}

        @Override
        public int getItemCount() {
            return layouts.length;
        }

        @Override
        public int getItemViewType(int position) {
            return layouts[position];
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            public SliderViewHolder(View view) {
                super(view);
            }
        }
    }
}
