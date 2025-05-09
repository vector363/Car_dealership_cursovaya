package com.example.cardealership_cursovaya.main;

import com.google.firebase.firestore.PropertyName;

public class Car {
    private String id;
    private String brand;
    private String model;
    private double price;
    private String imageUrl;
    @PropertyName("bodyType")
    private String bodyType;
    private boolean isFavorite = false;

    public Car() {} // Обязательный пустой конструктор для Firestore

    public Car(String brand, String model, double price, String imageUrl, String bodyType) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
    }


    // Геттеры и сеттеры
    @PropertyName("bodyType")
    public String getBodyType() { return bodyType; }

    @PropertyName("bodyType")
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
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