package com.smallacademy.userroles;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clothrental.models.Order;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvCustomerName, tvOrderDate, tvStatus;
    private String orderId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get orderId from intent
        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupFirebase();
        loadOrderDetails();
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Pesanan");
        }
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadOrderDetails() {
        DocumentReference docRef = db.collection("orders").document(orderId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Order order = documentSnapshot.toObject(Order.class);
                if (order != null) {
                    displayOrderDetails(order);
                }
            } else {
                Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading order details", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void displayOrderDetails(Order order) {
        tvOrderId.setText("Order ID: " + orderId);
        // Sesuaikan dengan field yang ada di model Order Anda
        // tvCustomerName.setText("Customer: " + order.getCustomerName());
        // tvOrderDate.setText("Date: " + order.getOrderDate());
        // tvStatus.setText("Status: " + order.getStatus());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}