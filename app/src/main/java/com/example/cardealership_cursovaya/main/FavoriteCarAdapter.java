package com.example.cardealership_cursovaya.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
        Car car = cars.get(position);

        holder.carBrand.setText(car.getBrand());
        holder.carModel.setText(car.getModel());
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()));

        // Устанавливаем заполненную иконку для избранного
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite);

        // Обработчик клика по кнопке избранного
        holder.favoriteButton.setOnClickListener(v -> {
            removeFromFavorites(car.getId(), position);
        });

        // Обработчик клика по всему элементу
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCarClick(car);
            }
        });
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
        TextView carBrand, carModel, carPrice;
        ImageButton favoriteButton;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carBrand = itemView.findViewById(R.id.car_brand);
            carModel = itemView.findViewById(R.id.car_model);
            carPrice = itemView.findViewById(R.id.car_price);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}