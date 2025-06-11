package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupDrawer();

        // Load dashboard data
        loadDashboardData();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this);

        // Highlight the dashboard menu item
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dasbor Utama");
        }
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadDashboardData() {
        // Set metrics values from database or hardcoded for demo
        TextView tvActiveRentals = findViewById(R.id.tvActiveRentalsValue);
        TextView tvTodayRevenue = findViewById(R.id.tvTodayRevenueValue);
        TextView tvPendingReturns = findViewById(R.id.tvPendingReturnsValue);
        TextView tvAvailableClothes = findViewById(R.id.tvAvailableClothesValue);

        tvActiveRentals.setText("128");
        tvTodayRevenue.setText("Rp 5.380.000");
        tvPendingReturns.setText("15");
        tvAvailableClothes.setText("428");

        // Set percentage changes
        TextView tvActiveRentalsChange = findViewById(R.id.tvActiveRentalsChange);
        TextView tvTodayRevenueChange = findViewById(R.id.tvTodayRevenueChange);
        TextView tvPendingReturnsChange = findViewById(R.id.tvPendingReturnsChange);
        TextView tvTotalClothes = findViewById(R.id.tvTotalClothes);

        tvActiveRentalsChange.setText("↑ 12% dari bulan lalu");
        tvTodayRevenueChange.setText("↑ 8% dari kemarin");
        tvPendingReturnsChange.setText("↑ 3 dari kemarin");
        tvTotalClothes.setText("dari total 580");

        // Update current date in calendar
        highlightCurrentDateInCalendar();
    }

    private void highlightCurrentDateInCalendar() {
        // Get current date to highlight in calendar
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Find the TextView corresponding to today's date in calendar grid
        String resourceName = "tvDay" + currentDay;
        int resourceId = getResources().getIdentifier(resourceName, "id", getPackageName());

        if (resourceId != 0) {
            TextView tvToday = findViewById(resourceId);
            if (tvToday != null) {
                tvToday.setBackgroundResource(R.drawable.circle_green_background);
                tvToday.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Toast.makeText(this, "Dasbor Utama", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_inventory) {
            Log.d("AdminDashboardActivity", "Navigating to ManajemenInventarisActivity");
            try {
                Intent intent = new Intent(AdminDashboardActivity.this, ManajemenInventarisActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("AdminDashboardActivity", "Error navigating to ManajemenInventarisActivity: " + e.getMessage(), e);
                Toast.makeText(this, "Gagal membuka Inventaris: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_orders) {
            Log.d("AdminDashboardActivity", "Navigating to ManajemenPesananActivity");
            try {
                Intent intent = new Intent(AdminDashboardActivity.this, ManajemenPesananActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("AdminDashboardActivity", "Error navigating to ManajemenPesananActivity: " + e.getMessage(), e);
                Toast.makeText(this, "Gagal membuka Pesanan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_loyalty) {
            Toast.makeText(this, "Loyalitas", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProgramLoyalitasActivity.class);
             startActivity(intent);
        } else if (id == R.id.nav_reports) {
            Toast.makeText(this, "Laporan", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, LaporanAnalitikActivity.class);
             startActivity(intent);
        } else if (id == R.id.nav_users) {
            Toast.makeText(this, "Pengguna", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, ManajemenPenggunaActivity.class);
             startActivity(intent);
        } else if (id == R.id.nav_users) {
            Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_users) {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onNotificationCardClick(View view) {
        int id = view.getId();
        if (id == R.id.cardLowStock) {
            Toast.makeText(this, "Menuju halaman stok rendah", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.cardLateReturns) {
            Toast.makeText(this, "Menuju halaman keterlambatan", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.cardMaintenanceRequired) {
            Toast.makeText(this, "Menuju halaman perawatan", Toast.LENGTH_SHORT).show();
        }
    }

    public void onViewCalendarClick(View view) {
        Toast.makeText(this, "Menuju kalender lengkap", Toast.LENGTH_SHORT).show();
    }

    public void onViewAllNotificationsClick(View view) {
        Toast.makeText(this, "Menuju semua notifikasi", Toast.LENGTH_SHORT).show();
    }
}