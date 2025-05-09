package com.example.cardealership_cursovaya.admin;

import android.util.Log;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminCarAdapter extends RecyclerView.Adapter<AdminCarAdapter.CarViewHolder> {
    private List<Car> cars;
    private OnCarActionListener listener;

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
        try {
            Car car = cars.get(position);

            holder.carBrand.setText(car.getBrand());
            holder.carModel.setText(car.getModel());
            holder.carBodyType.setText(car.getBodyType());
            holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()));

            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(car.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.carImage, new Callback() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onError(Exception e) {
                                Log.e("Picasso", "Error loading image: " + e.getMessage());
                            }
                        });
            }

            holder.btnEdit.setOnClickListener(v -> {
                try {
                    listener.onEditClick(car);
                } catch (Exception e) {
                    Log.e("AdminAdapter", "Edit error", e);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                try {
                    listener.onDeleteClick(car);
                } catch (Exception e) {
                    Log.e("AdminAdapter", "Delete error", e);
                }
            });

        } catch (Exception e) {
            Log.e("AdminAdapter", "Binding error", e);
        }
    }


    @Override
    public int getItemCount() {
        return cars.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carBrand, carModel, carPrice, carBodyType;
        Button btnEdit, btnDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carBrand = itemView.findViewById(R.id.car_brand);
            carModel = itemView.findViewById(R.id.car_model);
            carPrice = itemView.findViewById(R.id.car_price);
            carBodyType = itemView.findViewById(R.id.car_bodyType);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}