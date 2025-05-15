package com.example.cardealership_cursovaya.admin;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.main.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminCarAdapter adapter;
    private FirebaseFirestore db;
    private FloatingActionButton fabAddCar;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_admin);
        fabAddCar = view.findViewById(R.id.fab_add_car);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();

        loadCars();

        fabAddCar.setOnClickListener(v -> showCarDialog(null));

        return view;
    }

    private void loadCars() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("cars")
                .orderBy("brand")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        List<Car> cars = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Car car = document.toObject(Car.class);
                            car.setId(document.getId());
                            cars.add(car);
                        }
                        setupAdapter(cars);
                    } else {
                        Log.e("AdminFragment", "Error loading cars", task.getException());
                    }
                });
    }

    private void setupAdapter(List<Car> cars) {
        adapter = new AdminCarAdapter(cars, new AdminCarAdapter.OnCarActionListener() {
            @Override
            public void onEditClick(Car car) {
                showCarDialog(car);
            }

            @Override
            public void onDeleteClick(Car car) {
                deleteCar(car);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showCarDialog(@Nullable Car car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_car_edit, null);

        // Инициализация всех полей
        EditText etBrand = dialogView.findViewById(R.id.et_brand);
        EditText etModel = dialogView.findViewById(R.id.et_model);
        EditText etPrice = dialogView.findViewById(R.id.et_price);
        EditText etYear = dialogView.findViewById(R.id.et_year);
        EditText etMileage = dialogView.findViewById(R.id.et_mileage);
        EditText etImageUrl = dialogView.findViewById(R.id.et_image_url);
        EditText etEngineVolume = dialogView.findViewById(R.id.et_engine_volume);
        EditText etEnginePower = dialogView.findViewById(R.id.et_engine_power);
        EditText etColor = dialogView.findViewById(R.id.et_color);
        EditText etOwnersCount = dialogView.findViewById(R.id.et_owners_count);
        EditText etDescription = dialogView.findViewById(R.id.et_description);

        AutoCompleteTextView actBodyType = dialogView.findViewById(R.id.act_body_type);
        AutoCompleteTextView actTransmission = dialogView.findViewById(R.id.act_transmission);
        AutoCompleteTextView actSteeringWheel = dialogView.findViewById(R.id.act_steering_wheel);
        AutoCompleteTextView actPtsType = dialogView.findViewById(R.id.act_pts_type);

        // Настройка выпадающих списков
        setupDropdown(actBodyType, R.array.body_types);
        setupDropdown(actTransmission, R.array.transmission_types);
        setupDropdown(actSteeringWheel, R.array.steering_wheel_positions);
        setupDropdown(actPtsType, R.array.pts_types);

        // Заполнение данных если редактируем
        if (car != null) {
            etBrand.setText(car.getBrand());
            etModel.setText(car.getModel());
            etPrice.setText(String.valueOf(car.getPrice()));
            etYear.setText(car.getYear());
            etMileage.setText(String.valueOf(car.getMileage()));
            etImageUrl.setText(car.getImageUrl());
            etEngineVolume.setText(car.getEngineVolume());
            etEnginePower.setText(String.valueOf(car.getEnginePower()));
            etColor.setText(car.getColor());
            etOwnersCount.setText(String.valueOf(car.getOwnersCount()));
            etDescription.setText(car.getDescription());

            if (car.getBodyType() != null) {
                actBodyType.setText(capitalizeFirstLetter(car.getBodyType()), false);
            }
            if (car.getTransmissionType() != null) {
                actTransmission.setText(car.getTransmissionType(), false);
            }
            if (car.getSteeringWheelPosition() != null) {
                actSteeringWheel.setText(car.getSteeringWheelPosition(), false);
            }
            if (car.getPtsType() != null) {
                actPtsType.setText(car.getPtsType(), false);
            }
        }

        builder.setView(dialogView)
                .setTitle(car == null ? "Добавить автомобиль" : "Редактировать автомобиль")
                .setPositiveButton("Сохранить", (dialog, id) -> {
                    if (validateAndSaveCar(
                            car,
                            etBrand.getText().toString(),
                            etModel.getText().toString(),
                            etPrice.getText().toString(),
                            etYear.getText().toString(),
                            etMileage.getText().toString(),
                            etImageUrl.getText().toString(),
                            actBodyType.getText().toString(),
                            etEngineVolume.getText().toString(),
                            etEnginePower.getText().toString(),
                            etColor.getText().toString(),
                            etOwnersCount.getText().toString(),
                            etDescription.getText().toString(),
                            actTransmission.getText().toString(),
                            actSteeringWheel.getText().toString(),
                            actPtsType.getText().toString()
                    )) {
                        // Данные сохранены в validateAndSaveCar
                    }
                })
                .setNegativeButton("Отмена", null);

        AlertDialog dialog = builder.create();

        // Настройка для корректного отображения клавиатуры
        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etBrand.requestFocus();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        dialog.show();
    }

    private void setupDropdown(AutoCompleteTextView autoCompleteTextView, int arrayResId) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_menu_item,
                getResources().getStringArray(arrayResId));
        autoCompleteTextView.setAdapter(adapter);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private boolean validateAndSaveCar(@Nullable Car existingCar,
                                       String brand, String model, String priceStr,
                                       String year, String mileageStr, String imageUrl,
                                       String bodyType, String engineVolumeStr,
                                       String enginePowerStr, String color,
                                       String ownersCountStr, String description,
                                       String transmission, String steeringWheel,
                                       String ptsType) {
        // Валидация обязательных полей
        if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty() ||
                year.isEmpty() || mileageStr.isEmpty()) {
            Toast.makeText(getContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            // Парсинг числовых значений
            double price = Double.parseDouble(priceStr);
            double mileage = Double.parseDouble(mileageStr);
            double enginePower = enginePowerStr.isEmpty() ? 0 : Double.parseDouble(enginePowerStr);
            double ownersCount = ownersCountStr.isEmpty() ? 1 : Double.parseDouble(ownersCountStr);

            // Создание/обновление объекта Car
            Car car = existingCar != null ? existingCar : new Car();
            car.setBrand(brand.trim());
            car.setModel(model.trim());
            car.setPrice(price);
            car.setYear(year.trim());
            car.setMileage(mileage);
            car.setImageUrl(imageUrl.trim());
            car.setBodyType(bodyType.isEmpty() ? "седан" : bodyType.toLowerCase());
            car.setEngineVolume(engineVolumeStr.trim());
            car.setEnginePower(enginePower);
            car.setColor(color.trim());
            car.setOwnersCount(ownersCount);
            car.setDescription(description.trim());
            car.setTransmissionType(transmission.trim());
            car.setSteeringWheelPosition(steeringWheel.trim());
            car.setPtsType(ptsType.trim());

            if (existingCar == null) {
                addCar(car);
            } else {
                updateCar(existingCar.getId(), car);
            }
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Некорректные числовые значения", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void addCar(Car car) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("cars")
                .add(car)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Автомобиль добавлен", Toast.LENGTH_SHORT).show();
                    loadCars();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCar(String carId, Car car) {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("brand", car.getBrand());
        updates.put("model", car.getModel());
        updates.put("price", car.getPrice());
        updates.put("year", car.getYear());
        updates.put("mileage", car.getMileage());
        updates.put("imageUrl", car.getImageUrl());
        updates.put("bodyType", car.getBodyType());
        updates.put("engineVolume", car.getEngineVolume());
        updates.put("enginePower", car.getEnginePower());
        updates.put("color", car.getColor());
        updates.put("ownersCount", car.getOwnersCount());
        updates.put("description", car.getDescription());
        updates.put("transmissionType", car.getTransmissionType());
        updates.put("steeringWheelPosition", car.getSteeringWheelPosition());
        updates.put("ptsType", car.getPtsType());

        db.collection("cars")
                .document(carId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show();
                    loadCars();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteCar(Car car) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удаление")
                .setMessage("Удалить " + car.getBrand() + " " + car.getModel() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    db.collection("cars")
                            .document(car.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                                loadCars();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}