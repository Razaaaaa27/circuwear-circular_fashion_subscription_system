package com.smallacademy.userroles;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class KeranjangAdapter extends BaseAdapter {

    private Context context;
    private List<ClothingItem> clothingItems;
    private List<Boolean> selectedItems;

    public KeranjangAdapter(Context context, List<ClothingItem> clothingItems) {
        this.context = context;
        this.clothingItems = clothingItems;
        this.selectedItems = new ArrayList<>();

        // Initialize all items as unselected by default
        for (int i = 0; i < clothingItems.size(); i++) {
            selectedItems.add(false);
        }
    }

    @Override
    public int getCount() {
        return clothingItems.size();
    }

    @Override
    public Object getItem(int position) {
        return clothingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridItem;

        if (convertView == null) {
            gridItem = LayoutInflater.from(context).inflate(R.layout.grid_item_clothing, parent, false);
        } else {
            gridItem = convertView;
        }

        // Get views
        CardView cardView = gridItem.findViewById(R.id.cardView);
        ImageView imageView = gridItem.findViewById(R.id.blazerImageView);
        final RadioButton radioButton = gridItem.findViewById(R.id.radioSelect);
        TextView nameTextView = gridItem.findViewById(R.id.blazerNameTextView); // Tambahkan TextView untuk nama

        // Set data
        ClothingItem item = clothingItems.get(position);

        // Debug log untuk melihat imageResId
        Log.d("KeranjangAdapter", "Item: " + item.getName() + ", ImageResId: " + item.getImageResId());

        // Set image - HANYA menggunakan imageResId dari database, tidak ada placeholder
        setItemImage(imageView, item);

        // Set nama item dari database (BUKAN "Blazer Name")
        nameTextView.setText(item.getName());

        // Make sure to set the checked state without triggering the listener
        radioButton.setOnCheckedChangeListener(null);
        radioButton.setChecked(selectedItems.get(position));

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !selectedItems.get(position);
                selectedItems.set(position, newState);
                radioButton.setChecked(newState);

                // Notify the activity about the selection change
                if (context instanceof KeranjangActivity) {
                    ((KeranjangActivity) context).updateSelectedItemsCount();
                }
            }
        });

        // Handle item click untuk masuk ke detail pakaian
        gridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Masuk ke halaman detail pakaian
                Intent intent = new Intent(context, DetailPakaianActivity.class);
                intent.putExtra("itemId", item.getId());
                intent.putExtra("itemName", item.getName());
                intent.putExtra("itemCategory", item.getCategory());
                intent.putExtra("itemPrice", item.getPrice());
                intent.putExtra("itemCode", item.getCode());
                intent.putExtra("itemStatus", item.getStatus());
                intent.putExtra("itemType", item.getType());
                intent.putExtra("itemImageResId", item.getImageResId());
                context.startActivity(intent);
            }
        });

        return gridItem;
    }

    /**
     * Set image untuk item - HANYA menggunakan imageResId dari database
     * Tidak menggunakan placeholder sama sekali
     */
    private void setItemImage(ImageView imageView, ClothingItem item) {
        // Hanya gunakan imageResId yang valid dari database
        if (item.getImageResId() != 0) {
            try {
                imageView.setImageResource(item.getImageResId());
                Log.d("KeranjangAdapter", "✅ Berhasil set image: " + item.getImageResId());
            } catch (Exception e) {
                Log.e("KeranjangAdapter", "❌ Error loading image: " + item.getImageResId(), e);
                // Jika error loading, gunakan default image (baju1)
                imageView.setImageResource(R.drawable.baju1);
            }
        } else {
            Log.w("KeranjangAdapter", "⚠️ ImageResId = 0, using default for: " + item.getName());
            // Jika tidak ada imageResId, gunakan default image
            imageView.setImageResource(R.drawable.baju1);
        }
    }

    // Method to select or deselect all items
    public void selectAll(boolean select) {
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItems.set(i, select);
        }
        notifyDataSetChanged();
    }

    // Get number of selected items
    public int getSelectedItemsCount() {
        int count = 0;
        for (boolean selected : selectedItems) {
            if (selected) count++;
        }
        return count;
    }

    // Get list of selected items
    public List<ClothingItem> getSelectedItems() {
        List<ClothingItem> items = new ArrayList<>();
        for (int i = 0; i < clothingItems.size(); i++) {
            if (selectedItems.get(i)) {
                items.add(clothingItems.get(i));
            }
        }
        return items;
    }

    // Update clothingItems list and refresh adapter
    public void updateClothingItems(List<ClothingItem> newClothingItems) {
        this.clothingItems = newClothingItems;
        this.selectedItems.clear();

        // Initialize all items as unselected
        for (int i = 0; i < clothingItems.size(); i++) {
            selectedItems.add(false);
        }

        notifyDataSetChanged();
    }
}