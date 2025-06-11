package com.smallacademy.userroles;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddOrderActivity extends AppCompatActivity {

    private TextInputEditText etCustomerName, etItemName;
    private Button btnSaveOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        initViews();
        setupToolbar();
        setupButtons();
    }

    private void initViews() {
        etCustomerName = findViewById(R.id.etCustomerName);
        etItemName = findViewById(R.id.etItemName);
        btnSaveOrder = findViewById(R.id.btnSaveOrder);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tambah Pesanan");
        }
    }

    private void setupButtons() {
        btnSaveOrder.setOnClickListener(v -> {
            // TODO: Implement save order logic
            Toast.makeText(this, "Save order functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}