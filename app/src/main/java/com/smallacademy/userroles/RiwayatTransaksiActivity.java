package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RiwayatTransaksiActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView navHome, navSaved, navCart, navHistory, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Bottom navigation
        navHome = findViewById(R.id.navHome);
        navSaved = findViewById(R.id.navSaved);
        navCart = findViewById(R.id.navCart);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        // Highlight the active navigation item
        navHistory.setColorFilter(getResources().getColor(R.color.white));
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Bottom navigation click listeners
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScreen(HomepageActivity.class);
            }
        });

        navSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScreen(KatalogActivity.class);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScreen(KeranjangActivity.class);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScreen(ProfileActivity.class);
            }
        });
    }

    private void navigateToScreen(Class<?> destinationClass) {
        // Skip navigation if already on this screen
        if (this.getClass().equals(destinationClass)) {
            return;
        }

        Intent intent = new Intent(this, destinationClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}