package com.example.clothrental.models;

import java.util.List;

public class Order {
    private String id;
    private String orderId;
    private String userId;
    private String customerName;
    private List<String> itemIds;
    private double totalAmount;
    private String status;
    private long orderDate;
    private long startDate;
    private long endDate;

    public Order() {}

    public Order(String orderId, String userId, String customerName, List<String> itemIds,
                 double totalAmount, String status, long orderDate, long startDate, long endDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.itemIds = itemIds;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<String> getItemIds() { return itemIds; }
    public void setItemIds(List<String> itemIds) { this.itemIds = itemIds; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
}
