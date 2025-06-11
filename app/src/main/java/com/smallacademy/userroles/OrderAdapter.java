package com.smallacademy.userroles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.userroles.R;
// Import Order class dari package yang benar
import com.example.clothrental.models.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId, tvCustomerName, tvOrderDate, tvAmount, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(orderList.get(position));
                }
            });
        }

        public void bind(Order order) {
            tvOrderId.setText(order.getOrderId());
            tvCustomerName.setText(order.getCustomerName());
            tvOrderDate.setText(dateFormat.format(new Date(order.getOrderDate())));
            tvAmount.setText(currencyFormat.format(order.getTotalAmount()));
            tvStatus.setText(order.getStatus());

            // Set status color
            int statusColor;
            switch (order.getStatus().toLowerCase()) {
                case "pending":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark);
                    break;
                case "confirmed":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark);
                    break;
                case "completed":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
                    break;
                default:
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);
                    break;
            }
            tvStatus.setTextColor(statusColor);
        }
    }
}