package com.smallacademy.userroles;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String id;

    @PropertyName("fullName")
    private String name;

    @PropertyName("UserEmail")
    private String email;

    @PropertyName("PhoneNumber")
    private String phone;

    private String status;
    private long joinDate;

    @PropertyName("isUser")
    private String isUser;

    public User() {}

    public User(String name, String email, String phone, String status, long joinDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.joinDate = joinDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("fullName")
    public String getName() {
        return name;
    }

    @PropertyName("fullName")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("UserEmail")
    public String getEmail() {
        return email;
    }

    @PropertyName("UserEmail")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("PhoneNumber")
    public String getPhone() {
        return phone;
    }

    @PropertyName("PhoneNumber")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    @PropertyName("isUser")
    public String getIsUser() {
        return isUser;
    }

    @PropertyName("isUser")
    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }
}