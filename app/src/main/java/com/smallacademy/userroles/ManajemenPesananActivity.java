package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.userroles.R;
import com.smallacademy.userroles.OrderAdapter;

import com.example.clothrental.models.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManajemenPesananActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FloatingActionButton fabAddOrder;
    private Button btnAllStatus, btnAllDate;

    private FirebaseFirestore db;
    private CollectionReference ordersRef;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manajemen_pesanan);

        initViews();
        setupFirebase();
        setupRecyclerView();
        setupButtons();
        loadOrders();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manajemen Pesanan");

        recyclerView = findViewById(R.id.recyclerViewOrders);
        fabAddOrder = findViewById(R.id.fabAddOrder);
        btnAllStatus = findViewById(R.id.btnAllStatus);
        btnAllDate = findViewById(R.id.btnAllDate);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        ordersRef = db.collection("orders");
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this::showOrderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);
    }

    private void setupButtons() {
        fabAddOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddOrderActivity.class);
            startActivity(intent);
        });

        btnAllStatus.setOnClickListener(v -> {
            currentFilter = "all";
            loadOrders();
        });

        btnAllDate.setOnClickListener(v -> {
            currentFilter = "date";
            loadOrdersByDate();
        });
    }

    private void loadOrders() {
        Query query = ordersRef.orderBy("orderDate", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                orderList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Order order = document.toObject(Order.class);
                    order.setId(document.getId());
                    orderList.add(order);
                }
                orderAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrdersByDate() {
        // Filter orders by current date
        long startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000));
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000);

        ordersRef.whereGreaterThanOrEqualTo("orderDate", startOfDay)
                .whereLessThan("orderDate", endOfDay)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId());
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading today's orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOrderDetails(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("orderId", order.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(); // Refresh data when returning from other activities
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}