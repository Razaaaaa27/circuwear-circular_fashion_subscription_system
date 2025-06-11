package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomepageActivity extends AppCompatActivity {

    private Button btnSuit, btnSweater, btnShirt, btnHoodie, btnBlazzer, btnTshirt, btnJacket, btnPoloShirt, btnProceed;
    private CardView maleOption, femaleOption;
    private ImageView navHome, navSaved, navCart, navHistory, navProfile;
    private String selectedGender = "";
    private String selectedClothingType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize clothing type buttons
        btnSuit = findViewById(R.id.btnSuit);
        btnSweater = findViewById(R.id.btnSweater);
        btnShirt = findViewById(R.id.btnShirt);
        btnHoodie = findViewById(R.id.btnHoodie);
        btnBlazzer = findViewById(R.id.btnBlazzer);
        btnTshirt = findViewById(R.id.btnTshirt);
        btnJacket = findViewById(R.id.btnJacket);
        btnPoloShirt = findViewById(R.id.btnPoloShirt);

        // Initialize gender options
        maleOption = findViewById(R.id.maleOption);
        femaleOption = findViewById(R.id.femaleOption);

        // Initialize proceed button
        btnProceed = findViewById(R.id.btnProceed);

        // Initialize bottom navigation
        navHome = findViewById(R.id.navHome);
        navSaved = findViewById(R.id.navSaved);
        navCart = findViewById(R.id.navCart);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Set up clothing type button listeners
        View.OnClickListener clothingTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetClothingTypeButtons();

                Button selectedButton = (Button) v;
                selectedButton.setAlpha(0.8f);
                selectedClothingType = selectedButton.getText().toString();
                Toast.makeText(HomepageActivity.this, selectedClothingType + " selected", Toast.LENGTH_SHORT).show();
            }
        };

        btnSuit.setOnClickListener(clothingTypeListener);
        btnSweater.setOnClickListener(clothingTypeListener);
        btnShirt.setOnClickListener(clothingTypeListener);
        btnHoodie.setOnClickListener(clothingTypeListener);
        btnBlazzer.setOnClickListener(clothingTypeListener);
        btnTshirt.setOnClickListener(clothingTypeListener);
        btnJacket.setOnClickListener(clothingTypeListener);
        btnPoloShirt.setOnClickListener(clothingTypeListener);

        // Gender option buttons
        maleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleOption.setCardElevation(8f);
                femaleOption.setCardElevation(4f);
                selectedGender = "Male";
                Toast.makeText(HomepageActivity.this, "Male selected", Toast.LENGTH_SHORT).show();
            }
        });

        femaleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleOption.setCardElevation(8f);
                maleOption.setCardElevation(4f);
                selectedGender = "Female";
                Toast.makeText(HomepageActivity.this, "Female selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Proceed button
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedClothingType.isEmpty() || selectedGender.isEmpty()) {
                    Toast.makeText(HomepageActivity.this, "Please select both clothing type and gender", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomepageActivity.this, "Proceeding with " + selectedClothingType + " for " + selectedGender, Toast.LENGTH_SHORT).show();
                    // Intent intent = new Intent(HomepageActivity.this, NextActivity.class);
                    // startActivity(intent);
                }
            }
        });

        // Navigation bar listener
        View.OnClickListener navListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNavIcons();
                v.setAlpha(1.0f);

                int id = v.getId();
                if (id == R.id.navHome) {
                    Toast.makeText(HomepageActivity.this, "Home", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navSaved) {
                    // 👉 Buka KatalogActivity
                    Intent intent = new Intent(HomepageActivity.this, KatalogActivity.class);
                    startActivity(intent);
                } else if (id == R.id.navCart) {
                    Intent intent = new Intent(HomepageActivity.this, KeranjangActivity.class);
                    startActivity(intent);
                } else if (id == R.id.navHistory) {
                    Intent intent = new Intent(HomepageActivity.this, RiwayatTransaksiActivity.class);
                    startActivity(intent);
                } else if (id == R.id.navProfile) {
                    Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        navHome.setOnClickListener(navListener);
        navSaved.setOnClickListener(navListener);
        navCart.setOnClickListener(navListener);
        navHistory.setOnClickListener(navListener);
        navProfile.setOnClickListener(navListener);

        navHome.setAlpha(1.0f);
        navSaved.setAlpha(0.7f);
        navCart.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }

    private void resetClothingTypeButtons() {
        btnSuit.setAlpha(1.0f);
        btnSweater.setAlpha(1.0f);
        btnShirt.setAlpha(1.0f);
        btnHoodie.setAlpha(1.0f);
        btnBlazzer.setAlpha(1.0f);
        btnTshirt.setAlpha(1.0f);
        btnJacket.setAlpha(1.0f);
        btnPoloShirt.setAlpha(1.0f);
    }

    private void resetNavIcons() {
        navHome.setAlpha(0.7f);
        navSaved.setAlpha(0.7f);
        navCart.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }
}
