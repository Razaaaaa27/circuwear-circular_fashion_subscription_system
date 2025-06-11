package com.smallacademy.userroles;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.smallacademy.userroles.R;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LaporanAnalitikActivity extends AppCompatActivity {

    // Financial Report Views
    private TextView tvRevenue4, tvRevenue3, tvRevenue2, tvRevenue1;
    private TextView tvCost4, tvCost3, tvCost2, tvCost1;
    private TextView tvProfit4, tvProfit3, tvProfit2, tvProfit1;
    private TextView tvMonth4, tvMonth3, tvMonth2, tvMonth1;

    // Analytics Views
    private TextView tvNewUsers, tvActiveUsers, tvAvgOrderValue, tvReturnRate;
    private TextView tvNewUsersChange, tvActiveUsersChange, tvAvgOrderChange, tvReturnRateChange;

    private Button btnExport;

    private FirebaseFirestore db;
    private NumberFormat currencyFormat;
    private SimpleDateFormat monthFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_analitik);

        initViews();
        setupFirebase();
        setupFormatters();
        loadFinancialData();
        loadAnalyticsData();

        btnExport.setOnClickListener(v -> exportReport());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Laporan & Analitik");

        // Financial Report Views
        tvMonth4 = findViewById(R.id.tvMonth4);
        tvMonth3 = findViewById(R.id.tvMonth3);
        tvMonth2 = findViewById(R.id.tvMonth2);
        tvMonth1 = findViewById(R.id.tvMonth1);

        tvRevenue4 = findViewById(R.id.tvRevenue4);
        tvRevenue3 = findViewById(R.id.tvRevenue3);
        tvRevenue2 = findViewById(R.id.tvRevenue2);
        tvRevenue1 = findViewById(R.id.tvRevenue1);

        tvCost4 = findViewById(R.id.tvCost4);
        tvCost3 = findViewById(R.id.tvCost3);
        tvCost2 = findViewById(R.id.tvCost2);
        tvCost1 = findViewById(R.id.tvCost1);

        tvProfit4 = findViewById(R.id.tvProfit4);
        tvProfit3 = findViewById(R.id.tvProfit3);
        tvProfit2 = findViewById(R.id.tvProfit2);
        tvProfit1 = findViewById(R.id.tvProfit1);

        // Analytics Views
        tvNewUsers = findViewById(R.id.tvNewUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvAvgOrderValue = findViewById(R.id.tvAvgOrderValue);
        tvReturnRate = findViewById(R.id.tvReturnRate);

        tvNewUsersChange = findViewById(R.id.tvNewUsersChange);
        tvActiveUsersChange = findViewById(R.id.tvActiveUsersChange);
        tvAvgOrderChange = findViewById(R.id.tvAvgOrderChange);
        tvReturnRateChange = findViewById(R.id.tvReturnRateChange);

        btnExport = findViewById(R.id.btnExport);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupFormatters() {
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        monthFormat = new SimpleDateFormat("MMM yyyy", new Locale("id", "ID"));
    }

    private void loadFinancialData() {
        // Set default data (in real app, this would come from Firebase)
        setDefaultFinancialData();

        // Load actual data from Firebase
        loadMonthlyRevenue();
    }

    private void setDefaultFinancialData() {
        Calendar calendar = Calendar.getInstance();

        // Set months
        tvMonth1.setText(monthFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -1);
        tvMonth2.setText(monthFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -1);
        tvMonth3.setText(monthFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -1);
        tvMonth4.setText(monthFormat.format(calendar.getTime()));

        // Set default financial data
        tvRevenue1.setText("Rp 85.430.000");
        tvRevenue2.setText("Rp 78.965.000");
        tvRevenue3.setText("Rp 72.540.000");
        tvRevenue4.setText("Rp 68.120.000");

        tvCost1.setText("Rp 32.150.000");
        tvCost2.setText("Rp 30.785.000");
        tvCost3.setText("Rp 29.320.000");
        tvCost4.setText("Rp 27.850.000");

        tvProfit1.setText("Rp 53.280.000");
        tvProfit2.setText("Rp 48.180.000");
        tvProfit3.setText("Rp 43.220.000");
        tvProfit4.setText("Rp 40.270.000");
    }

    private void loadMonthlyRevenue() {
        // Get current month orders
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startOfMonth)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalRevenue = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double amount = document.getDouble("totalAmount");
                            if (amount != null) {
                                totalRevenue += amount;
                            }
                        }
                        // Update current month revenue
                        tvRevenue1.setText(currencyFormat.format(totalRevenue));
                    }
                });
    }

    private void loadAnalyticsData() {
        // Set default analytics data
        setDefaultAnalyticsData();

        // Load actual analytics
        loadUserAnalytics();
    }

    private void setDefaultAnalyticsData() {
        tvNewUsers.setText("245");
        tvNewUsersChange.setText("+15%");

        tvActiveUsers.setText("1,835");
        tvActiveUsersChange.setText("+8%");

        tvAvgOrderValue.setText("Rp 425.000");
        tvAvgOrderChange.setText("+5%");

        tvReturnRate.setText("68%");
        tvReturnRateChange.setText("+3%");
    }

    private void loadUserAnalytics() {
        // Get current month users
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = calendar.getTimeInMillis();

        db.collection("users")
                .whereGreaterThanOrEqualTo("joinDate", startOfMonth)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int newUsers = task.getResult().size();
                        tvNewUsers.setText(String.valueOf(newUsers));
                    }
                });

        // Get active users (users with orders in last 30 days)
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", thirtyDaysAgo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int activeUsers = task.getResult().size();
                        tvActiveUsers.setText(String.valueOf(activeUsers));
                    }
                });
    }

    private void exportReport() {
        // In a real app, this would generate and export a PDF or Excel file
        // For now, we'll just show a toast
        android.widget.Toast.makeText(this, "Laporan berhasil diekspor", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}