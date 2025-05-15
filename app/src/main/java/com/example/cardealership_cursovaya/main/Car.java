package com.example.cardealership_cursovaya.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

public class Car implements Parcelable {
    private String year;
    private double mileage;
    private String id;
    private String brand;
    private String model;
    private double price;
    private String imageUrl;
    @PropertyName("bodyType")
    private String bodyType;
    private boolean isFavorite = false;

    private String engineVolume;
    private double enginePower;
    private String color;
    private String transmissionType;
    private String steeringWheelPosition;
    private double ownersCount;
    private String ptsType;
    private String description;

    public Car() {} // Обязательный пустой конструктор для Firestore

    public Car(String brand, String model, double price, String imageUrl, String bodyType, String year, double mileage) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.year = year;
        this.mileage = mileage;
    }

    // Геттеры и сеттеры

    public void setColor(String color) {this.color = color;}
    public void setDescription(String description) {this.description = description;}

    public void setEnginePower(double enginePower) {this.enginePower = enginePower;}

    public void setEngineVolume(String engineVolume) {this.engineVolume = engineVolume;}

    public void setOwnersCount(double ownersCount) {this.ownersCount = ownersCount;}

    public void setPtsType(String ptsType) {this.ptsType = ptsType;}

    public void setSteeringWheelPosition(String steeringWheelPosition) {this.steeringWheelPosition = steeringWheelPosition;}

    public void setTransmissionType(String transmissionType) {this.transmissionType = transmissionType;}

    public double getEnginePower() { return enginePower; }

    public double getOwnersCount() { return ownersCount; }

    public String getColor() { return color; }

    public String getDescription() {return description;}

    public String getEngineVolume() {return engineVolume;}

    public String getPtsType() {return ptsType;}

    public String getSteeringWheelPosition() {return steeringWheelPosition;}

    public String getTransmissionType() {return transmissionType;}

    public String getYear() {return year;}

    public void setYear(String year) {this.year = year;}

    public double getMileage() {return mileage;}

    public void setMileage(double mileage) {this.mileage = mileage;}

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

    protected Car(Parcel in) {
        year = in.readString();
        mileage = in.readDouble();
        id = in.readString();
        brand = in.readString();
        model = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        bodyType = in.readString();
        isFavorite = in.readByte() != 0;

        engineVolume = in.readString();
        enginePower = in.readDouble();
        color = in.readString();
        transmissionType = in.readString();
        steeringWheelPosition = in.readString();
        ownersCount = in.readDouble();
        ptsType = in.readString();
        description = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(year);
        dest.writeDouble(mileage);
        dest.writeString(id);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeString(bodyType);
        dest.writeByte((byte) (isFavorite ? 1 : 0));

        dest.writeString(engineVolume);
        dest.writeDouble(enginePower);
        dest.writeString(color);
        dest.writeString(transmissionType);
        dest.writeString(steeringWheelPosition);
        dest.writeDouble(ownersCount);
        dest.writeString(ptsType);
        dest.writeString(description);
    }

}