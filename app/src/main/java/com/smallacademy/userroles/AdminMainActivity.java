package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.smallacademy.userroles.FirebaseManager;
import com.smallacademy.userroles.R;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // Navigation buttons
    private LinearLayout btnInventory, btnOrders, btnLoyalty, btnReports, btnUsers, btnSettings, btnProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Initialize Firebase
        FirebaseManager.initialize();

        initViews();
        setupToolbar();
        setupNavigation();
        setupNavigationButtons();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Initialize navigation buttons
        btnInventory = findViewById(R.id.btnInventory);
        btnOrders = findViewById(R.id.btnOrders);
        btnLoyalty = findViewById(R.id.btnLoyalty);
        btnReports = findViewById(R.id.btnReports);
        btnUsers = findViewById(R.id.btnUsers);
        btnSettings = findViewById(R.id.btnSettings);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupNavigationButtons() {
        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManajemenInventarisActivity.class);
            startActivity(intent);
        });

        btnOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManajemenPesananActivity.class);
            startActivity(intent);
        });

        btnLoyalty.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProgramLoyalitasActivity.class);
            startActivity(intent);
        });

        btnReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, LaporanAnalitikActivity.class);
            startActivity(intent);
        });

        btnUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManajemenPenggunaActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        navigateToFragment(id);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToFragment(int itemId) {
        Intent intent = null;

        if (itemId == R.id.nav_dashboard) {
            Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_inventory) {
            intent = new Intent(this, ManajemenInventarisActivity.class);
        } else if (itemId == R.id.nav_orders) {
            intent = new Intent(this, ManajemenPesananActivity.class);
        } else if (itemId == R.id.nav_loyalty) {
            intent = new Intent(this, ProgramLoyalitasActivity.class);
        } else if (itemId == R.id.nav_reports) {
            intent = new Intent(this, LaporanAnalitikActivity.class);
        } else if (itemId == R.id.nav_users) {
            intent = new Intent(this, ManajemenPenggunaActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
        } else if (itemId == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (itemId == R.id.nav_logout) {
            showLogoutConfirmation();
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void logout() {
        FirebaseManager.signOut();
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}