package com.smallacademy.userroles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnSkip, btnNext;
    private LinearLayout indicatorLayout;
    private ImageView[] indicators;

    private static final String PREFS_NAME = "OnboardingPrefs";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    private final int[] onboardingImages = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupViewPager();
        setupIndicators();
        setupButtons();
    }

    private void initViews() {
        viewPager = findViewById(R.id.onboardingViewPager);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        indicatorLayout = findViewById(R.id.indicatorLayout);
    }

    private void setupViewPager() {
        OnboardingAdapter adapter = new OnboardingAdapter(onboardingImages);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
                updateButtons(position);
            }
        });
    }

    private void setupIndicators() {
        indicators = new ImageView[onboardingImages.length];

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            indicators[i].setLayoutParams(params);

            indicatorLayout.addView(indicators[i]);
        }

        // Set first indicator as active
        if (indicators.length > 0) {
            indicators[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
        }
    }

    private void setupButtons() {
        btnSkip.setOnClickListener(v -> goToLogin());

        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < onboardingImages.length - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                goToLogin();
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            if (i == position) {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }

    private void updateButtons(int position) {
        if (position == onboardingImages.length - 1) {
            btnNext.setText("Get Started");
            btnSkip.setVisibility(View.GONE);
        } else {
            btnNext.setText("Next");
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void goToLogin() {
        // Mark onboarding as completed
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ONBOARDING_COMPLETED, true);
        editor.apply();

        // Navigate to login
        startActivity(new Intent(this, Login.class));
        finish();
    }
}