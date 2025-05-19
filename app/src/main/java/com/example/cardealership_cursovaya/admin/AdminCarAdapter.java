package com.example.cardealership_cursovaya.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.main.Car;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminCarAdapter extends RecyclerView.Adapter<AdminCarAdapter.CarViewHolder> {
    private final List<Car> cars;
    private final OnCarActionListener listener;

    public interface OnCarActionListener {
        void onEditClick(Car car);
        void onDeleteClick(Car car);
    }

    public AdminCarAdapter(List<Car> cars, OnCarActionListener listener) {
        this.cars = cars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);

        holder.carBrand.setText(car.getBrand());
        holder.carModel.setText(car.getModel());
        holder.carYear.setText(car.getYear() + "г.");
        holder.carMileage.setText(String.format("%,d км", (int)car.getMileage()));
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()).replace(",", " "));
        holder.carBodyType.setText(car.getBodyType());

        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(car.getImageUrl())
                    .placeholder(R.drawable.bg_auth_switch)
                    .error(R.drawable.bg_auth_switch)
                    .into(holder.carImage);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(car));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(car));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carBrand, carModel, carPrice, carMileage, carYear, carBodyType;
        Button btnEdit, btnDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carBrand = itemView.findViewById(R.id.car_brand);
            carModel = itemView.findViewById(R.id.car_model);
            carPrice = itemView.findViewById(R.id.car_price);
            carMileage = itemView.findViewById(R.id.car_mileage);
            carYear = itemView.findViewById(R.id.car_year);
            carBodyType = itemView.findViewById(R.id.car_bodyType);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}