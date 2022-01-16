package com.mayank.rucky.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mayank.rucky.R;
import com.mayank.rucky.utils.Config;
import com.mayank.rucky.utils.CustomTransformation;

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
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_init);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if(controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
        else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnSkip.setFilterTouchesWhenObscured(true);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setFilterTouchesWhenObscured(true);

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
        new Handler(Looper.getMainLooper()).postDelayed(() -> addBottomDots(0), 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (config.getDarkMode()) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

    private void addBottomDots(int currentPage) {
        if (dotsLayout != null)
            dotsLayout.removeAllViews();
        ImageView[] dots = new ImageView[layouts.length];
        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource( i == currentPage ? R.drawable.ic_dots : R.drawable.ic_dots_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dotsLayout.addView(dots[i],params);
        }
    }

    private int getItem() {
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen() {
        config.setInitState(false);
        Intent i = new Intent(InitActivity.this, EditorActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


    ViewPager2.OnPageChangeCallback viewPagerPageChangeListener = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.init_start));
                btnSkip.setEnabled(false);
                btnSkip.setAlpha(0);
            } else if (position == 3 || position == 4) {
                btnNext.setText(getString(R.string.init_agree));
                btnSkip.setEnabled(true);
                btnSkip.setAlpha(1);
            } else {
                btnNext.setText(getString(R.string.init_next));
                btnSkip.setEnabled(true);
                btnSkip.setAlpha(1);
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
