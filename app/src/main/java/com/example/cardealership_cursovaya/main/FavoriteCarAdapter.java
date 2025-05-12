package com.example.cardealership_cursovaya.main;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class FavoriteCarAdapter extends RecyclerView.Adapter<FavoriteCarAdapter.CarViewHolder> {
    private List<Car> cars;
    private final OnCarClickListener listener;
    private final Drawable favoriteSel, favoriteUnsel;

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    public FavoriteCarAdapter(List<Car> cars, OnCarClickListener listener, Context context) {
        this.cars = new ArrayList<>(cars); // Защитная копия
        this.listener = listener;
        this.favoriteSel = ContextCompat.getDrawable(requireNonNull(context), R.drawable.ic_favorite_sel);
        this.favoriteUnsel = ContextCompat.getDrawable(context, R.drawable.ic_favorite_unsel);
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

        holder.favoriteButton.setImageDrawable(favoriteSel);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCarClick(car);
        });

        holder.favoriteButton.setOnClickListener(v -> {
            holder.favoriteButton.setImageDrawable(favoriteUnsel);
            v.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() -> v.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(100)
                            .start())
                    .start();

            removeFromFavorites(car.getId(), holder.getAdapterPosition());
        });

        holder.carBrand.setText(car.getBrand());
        holder.carModel.setText(car.getModel());
        holder.carPrice.setText(String.format("%,d ₽", (int)car.getPrice()));
        holder.carYear.setText(String.format("%sг.,", car.getYear()));
        holder.carMileage.setText(String.format("%,d км", (int)car.getMileage()));

        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(car.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.carImage);
        }
    }

    private void removeFromFavorites(String carId, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || position == RecyclerView.NO_POSITION) return;

        FirebaseFirestore.getInstance()
                .collection("favorites")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("carId", carId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        doc.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    if (position < cars.size()) {
                                        cars.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        final ImageView carImage;
        final TextView carBrand, carModel, carPrice, carYear, carMileage;
        final ImageButton favoriteButton;

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