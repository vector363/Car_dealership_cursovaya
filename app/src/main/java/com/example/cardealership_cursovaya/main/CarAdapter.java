package com.example.cardealership_cursovaya.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CarAdapter extends FirestoreRecyclerAdapter<Car, CarAdapter.CarViewHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public CarAdapter(@NonNull FirestoreRecyclerOptions<Car> options) {
        super(options);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CarViewHolder holder, int position, @NonNull Car car) {
        String carId = getSnapshots().getSnapshot(position).getId();

        holder.carBrand.setText(car.getBrand());
        holder.carModel.setText(car.getModel());
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()));

        checkIfFavorite(carId, holder.favoriteButton);

        holder.favoriteButton.setOnClickListener(v -> {
            toggleFavorite(carId, holder.favoriteButton);
        });
    }

    private void checkIfFavorite(String carId, ImageButton favoriteButton) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (userId.isEmpty()) return;

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", carId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isFavorite = !task.getResult().isEmpty();
                        favoriteButton.setImageResource(isFavorite ?
                                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    }
                });
    }

    private void toggleFavorite(String carId, ImageButton favoriteButton) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (userId.isEmpty()) return;

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", carId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            addToFavorites(userId, carId, favoriteButton);
                        } else {
                            removeFromFavorites(task.getResult().getDocuments().get(0).getId(), favoriteButton);
                        }
                    }
                });
    }

    private void addToFavorites(String userId, String carId, ImageButton favoriteButton) {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("userId", userId);
        favorite.put("carId", carId);
        favorite.put("timestamp", FieldValue.serverTimestamp());

        db.collection("favorites")
                .add(favorite)
                .addOnSuccessListener(documentReference -> {
                    favoriteButton.setImageResource(R.drawable.ic_favorite);
                });
    }

    private void removeFromFavorites(String favoriteId, ImageButton favoriteButton) {
        db.collection("favorites")
                .document(favoriteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                });
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