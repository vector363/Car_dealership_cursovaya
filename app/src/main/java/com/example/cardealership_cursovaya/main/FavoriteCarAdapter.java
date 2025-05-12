package com.example.cardealership_cursovaya.main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.logging.Handler;

public class FavoriteCarAdapter extends RecyclerView.Adapter<FavoriteCarAdapter.CarViewHolder> {
    private List<Car> cars;
    private OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    public FavoriteCarAdapter(List<Car> cars, OnCarClickListener listener) {
        this.cars = cars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        // Устанавливаем заполненную иконку для избранного
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_sel);

        Car car = cars.get(position);

        // Обработчик клика по всему элементу
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onCarClick(car);
            }
        });

        holder.carBrand.setText(car.getBrand());
        holder.carModel.setText(car.getModel());
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()));

        holder.carYear.setText(car.getYear() + "г.,");
        holder.carMileage.setText(String.format("%,d км", (int)car.getMileage()));


        // Обработчик клика по кнопке избранного
        holder.favoriteButton.setOnClickListener(v -> {
            removeFromFavorites(car.getId(), position);
        });
        //Загрузка картинки авто
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
    }

    private void removeFromFavorites(String carId, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", carId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String favoriteId = task.getResult().getDocuments().get(0).getId();
                        db.collection("favorites").document(favoriteId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Удаляем из списка
                                    cars.remove(position);
                                    notifyItemRemoved(position);
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carBrand, carModel, carPrice, carYear, carMileage;
        ImageButton favoriteButton;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carBrand = itemView.findViewById(R.id.car_brand);
            carModel = itemView.findViewById(R.id.car_model);
            carPrice = itemView.findViewById(R.id.car_price);

            carYear = itemView.findViewById(R.id.car_year);
            carMileage = itemView.findViewById(R.id.car_mileage);

            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}