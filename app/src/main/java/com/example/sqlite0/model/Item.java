package com.example.sqlite0.model;

public class Item {
    private int id; // Thêm id để xác định mục chi tiêu
    private String title;
    private String category;
    private String price;
    private String date;

    // Constructor
    public Item(int id, String title, String category, String price, String date) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    // Constructor không có id (dùng khi thêm mới)
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