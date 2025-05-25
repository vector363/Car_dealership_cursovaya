package com.example.cardealership_cursovaya.catalog;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.main.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CarDetailFragment extends Fragment {
    private static final String ARG_CAR = "car";
    private Car car;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageButton favoriteButton;

    public static CarDetailFragment newInstance(Car car) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CAR, car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            car = getArguments().getParcelable(ARG_CAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        ImageView carImage = view.findViewById(R.id.car_image_detail);
        TextView brandModel = view.findViewById(R.id.car_brand_model_detail);
        TextView price = view.findViewById(R.id.car_price_detail);
        TextView bodyType = view.findViewById(R.id.car_body_type_detail);
        TextView year = view.findViewById(R.id.car_year_detail);
        TextView mileage = view.findViewById(R.id.car_mileage_detail);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        TextView engineVolume = view.findViewById(R.id.car_enginevolume_detail);
        TextView enginePower = view.findViewById(R.id.car_engine_power_detail);
        TextView color = view.findViewById(R.id.car_color_detail);
        TextView transmissionType = view.findViewById(R.id.car_transimissionType_detail);
        TextView steeringWheelPosition = view.findViewById(R.id.car_steering_wheel_pos_detail);
        TextView ownersCount = view.findViewById(R.id.car_owners_detail);
        TextView ptsType = view.findViewById(R.id.car_pts_type_detail);

        TextView description = view.findViewById(R.id.car_description_detail);

        favoriteButton = view.findViewById(R.id.favorite_button); // Добавляем кнопку избранного

        // Заполняем данные
        if (car != null) {
            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                Picasso.get().load(car.getImageUrl())
                        .placeholder(R.drawable.bg_auth_switch)
                        .error(R.drawable.bg_auth_switch)
                        .into(carImage);
            }

            brandModel.setText(String.format("%s %s, %s", car.getBrand(), car.getModel(), car.getYear()));
            price.setText(String.format("%,d ₽", (int)car.getPrice()).replace(",", " "));
            bodyType.setText(String.format("Тип кузова: %s", car.getBodyType()));
            year.setText(String.format("Год выпуска: %s г.", car.getYear()));
            mileage.setText(String.format("Пробег: %,d км", (int)car.getMileage()));

            engineVolume.setText(String.format("Объем двигателя: %s л.", car.getEngineVolume()));
            enginePower.setText(String.format("Мощность двигателя: %,d л.с.", (int)car.getEnginePower()));
            color.setText(String.format("Цвет авто: %s", car.getColor()));
            transmissionType.setText(String.format("Тип трансмиссии: %s", car.getTransmissionType()));
            steeringWheelPosition.setText(String.format("Положение рулевого колеса: %s", car.getSteeringWheelPosition()));
            ownersCount.setText(String.format("Кол-во владельцев: %, d", (int)car.getOwnersCount()));
            ptsType.setText(String.format("Тип птс: %s", car.getPtsType()));

            description.setText(car.getDescription());



            checkIfFavorite();
        }

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        favoriteButton.setOnClickListener(v -> {
            toggleFavorite();
        });

        return view;
    }

    private void checkIfFavorite() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (userId.isEmpty() || car == null) return;

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", car.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isFavorite = !task.getResult().isEmpty();
                        updateFavoriteButton(isFavorite);
                    }
                });
    }

    private void toggleFavorite() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (userId.isEmpty() || car == null) return;

        // Мгновенное обновление UI
        boolean isCurrentlyFavorite = favoriteButton.getDrawable().getConstantState()
                .equals(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_sel).getConstantState());

        updateFavoriteButton(!isCurrentlyFavorite);

        // Анимация
        favoriteButton.animate()
                .scaleX(0.8f).scaleY(0.8f)
                .setDuration(80)
                .withEndAction(() -> favoriteButton.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(80)
                        .start())
                .start();

        // Синхронизация с сервером
        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", car.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (isCurrentlyFavorite) {
                            // Удаляем из избранного
                            if (!task.getResult().isEmpty()) {
                                db.collection("favorites").document(task.getResult().getDocuments().get(0).getId()).delete();
                            }
                        } else {
                            // Добавляем в избранное
                            if (task.getResult().isEmpty()) {
                                Map<String, Object> favorite = new HashMap<>();
                                favorite.put("userId", userId);
                                favorite.put("carId", car.getId());
                                favorite.put("timestamp", FieldValue.serverTimestamp());
                                db.collection("favorites").add(favorite);
                            }
                        }
                    }
                });
    }

    private void updateFavoriteButton(boolean isFavorite) {
        favoriteButton.setImageResource(isFavorite ?
                R.drawable.ic_favorite_sel :
                R.drawable.ic_favorite_unsel);
    }
}