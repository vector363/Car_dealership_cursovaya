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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CarAdapter extends FirestoreRecyclerAdapter<Car, CarAdapter.CarViewHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Car car);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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

        holder.carYear.setText(car.getYear() + "г.,");

        holder.carMileage.setText(String.format("%,d км", (int)car.getMileage()));
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()).replace(",", " "));

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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(car);
            }
        });

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
                                R.drawable.ic_favorite_sel : R.drawable.ic_favorite_unsel);
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
                    favoriteButton.setImageResource(R.drawable.ic_favorite_sel);
                });
    }

    private void removeFromFavorites(String favoriteId, ImageButton favoriteButton) {
        db.collection("favorites")
                .document(favoriteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_unsel);
                });
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carBrand, carModel, carPrice, carMileage, carYear;
        ImageButton favoriteButton;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carBrand = itemView.findViewById(R.id.car_brand);
            carModel = itemView.findViewById(R.id.car_model);
            carPrice = itemView.findViewById(R.id.car_price);
            carMileage = itemView.findViewById(R.id.car_mileage);
            carYear = itemView.findViewById(R.id.car_year);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}