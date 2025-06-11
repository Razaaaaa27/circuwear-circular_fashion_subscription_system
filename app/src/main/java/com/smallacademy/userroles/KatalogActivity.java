
package com.smallacademy.userroles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class KatalogActivity extends AppCompatActivity {
    private GridView blazerGridView;
    private BlazerAdapter adapter;
    private ArrayList<Blazer> blazerList;
    private Button newArrivalsButton, trendingButton;

    private ImageView navHome, navSaved, navCart, navHistory, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        navHome = findViewById(R.id.navHome);
        navSaved = findViewById(R.id.navSaved);
        navCart = findViewById(R.id.navCart);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);
        blazerGridView = findViewById(R.id.blazerGridView);
        newArrivalsButton = findViewById(R.id.newArrivalsButton);
        trendingButton = findViewById(R.id.trendingButton);

        setupButtonListeners();
        initializeBlazerList();

        adapter = new BlazerAdapter(this, blazerList);
        blazerGridView.setAdapter(adapter);

        blazerGridView.setOnItemClickListener((parent, view, position, id) -> {
            Blazer selectedBlazer = blazerList.get(position);
            Toast.makeText(KatalogActivity.this,
                    "Selected: " + selectedBlazer.getName(),
                    Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void setupButtonListeners() {
        newArrivalsButton.setOnClickListener(v ->
                Toast.makeText(this, "New Arrivals Selected", Toast.LENGTH_SHORT).show());

        trendingButton.setOnClickListener(v ->
                Toast.makeText(this, "Trending Selected", Toast.LENGTH_SHORT).show());
    }

    private void initializeBlazerList() {
        blazerList = new ArrayList<>();
        blazerList.add(new Blazer("Black Roger Suit", R.drawable.baju1));
        blazerList.add(new Blazer("Green Formal Blazer", R.drawable.baju02));
        blazerList.add(new Blazer("Red Formal Blazer", R.drawable.baju03));
        blazerList.add(new Blazer("White Classic Blazer", R.drawable.baju04));
        blazerList.add(new Blazer("Gold Sequin Blazer", R.drawable.baju05));
        blazerList.add(new Blazer("Black Satin Blazer", R.drawable.baju06));
        blazerList.add(new Blazer("White Patterned Blazer", R.drawable.baju07));
        blazerList.add(new Blazer("Green Velvet Blazer", R.drawable.baju08));
    }

    private void setupBottomNavigation() {
        View.OnClickListener navListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNavIcons();
                v.setAlpha(1.0f);
                int id = v.getId();
                if (id == R.id.navHome) {
                    startActivity(new Intent(KatalogActivity.this, HomepageActivity.class));
                } else if (id == R.id.navSaved) {
                    Toast.makeText(KatalogActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navCart) {
                    startActivity(new Intent(KatalogActivity.this, KeranjangActivity.class));
                } else if (id == R.id.navHistory) {
                    startActivity(new Intent(KatalogActivity.this, RiwayatTransaksiActivity.class));
                } else if (id == R.id.navProfile) {
                    startActivity(new Intent(KatalogActivity.this, ProfileActivity.class));
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

    private void resetNavIcons() {
        navHome.setAlpha(0.7f);
        navSaved.setAlpha(0.7f);
        navCart.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }

    public class Blazer {
        private final String name;
        private final int imageResource;

        public Blazer(String name, int imageResource) {
            this.name = name;
            this.imageResource = imageResource;
        }

        public String getName() {
            return name;
        }

        public int getImageResource() {
            return imageResource;
        }
    }

    public class BlazerAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<Blazer> blazerList;

        public BlazerAdapter(Context context, ArrayList<Blazer> blazerList) {
            this.context = context;
            this.blazerList = blazerList;
        }

        @Override
        public int getCount() {
            return blazerList.size();
        }

        @Override
        public Object getItem(int position) {
            return blazerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_blazzer, parent, false);
                holder = new ViewHolder();
                holder.blazerImageView = convertView.findViewById(R.id.blazerImageView);
                holder.blazerNameTextView = convertView.findViewById(R.id.blazerNameTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Blazer blazer = blazerList.get(position);
            holder.blazerImageView.setImageResource(blazer.getImageResource());
            holder.blazerNameTextView.setText(blazer.getName());

            return convertView;
        }

        class ViewHolder {
            ImageView blazerImageView;
            TextView blazerNameTextView;
        }
    }
}
