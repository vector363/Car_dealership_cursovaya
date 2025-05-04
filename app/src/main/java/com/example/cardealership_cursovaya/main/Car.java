package com.example.cardealership_cursovaya.main;

public class Car {
    private String id;
    private String brand;
    private String model;
    private double price;
    private String imageUrl;
    private boolean isFavorite = false;

    // Обязательный пустой конструктор для Firestore
    public Car() {}

    public Car(String brand, String model, double price, String imageUrl) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
    }


    // Геттеры и сеттеры
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}