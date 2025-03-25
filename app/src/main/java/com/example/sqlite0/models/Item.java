package com.example.sqlite0.models;

public class Item {
    private int id;        // ID của mục chi tiêu
    private int userId;    // ID của người dùng sở hữu mục chi tiêu
    private String title;
    private String category;
    private String price;
    private String date;

    // Constructor đầy đủ (bao gồm cả userId)
    public Item(int id, int userId, String title, String category, String price, String date) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    // Constructor không có id (dùng khi thêm mới, cần userId)
    public Item(int userId, String title, String category, String price, String date) {
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    // Constructor không có id và userId (giữ lại cho tương thích cũ, nếu cần)
    public Item(String title, String category, String price, String date) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }
}