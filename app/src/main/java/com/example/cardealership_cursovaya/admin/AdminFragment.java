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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.main.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
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

        // Инициализация элементов
        recyclerView = view.findViewById(R.id.recycler_view_admin);
        fabAddCar = view.findViewById(R.id.fab_add_car);
        progressBar = view.findViewById(R.id.progress_bar);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация Firestore
        db = FirebaseFirestore.getInstance();

        // Загрузка данных
        loadCars();

        // Обработчик добавления нового авто
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

        // Инициализация полей
        EditText etBrand = dialogView.findViewById(R.id.et_brand);
        EditText etModel = dialogView.findViewById(R.id.et_model);
        EditText etPrice = dialogView.findViewById(R.id.et_price);
        EditText etImageUrl = dialogView.findViewById(R.id.et_image_url);

        TextInputLayout bodyTypeLayout = dialogView.findViewById(R.id.body_type_layout);
        AutoCompleteTextView actBodyType = dialogView.findViewById(R.id.act_body_type);

        // Настройка адаптера для выпадающего списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_menu_item,
                getResources().getStringArray(R.array.body_types));
        actBodyType.setAdapter(adapter);

        if (car != null && car.getBodyType() != null) {
            // Приводим первую букву к верхнему регистру для отображения
            String currentBodyType = car.getBodyType().substring(0, 1).toUpperCase()
                    + car.getBodyType().substring(1);
            actBodyType.setText(currentBodyType, false); // false - не фильтровать
        }

        // Включаем выпадающий список при клике
        actBodyType.setOnClickListener(v -> {
            actBodyType.showDropDown();

        });

        // Заполнение данных если редактируем
        if (car != null) {
            etBrand.setText(car.getBrand());
            etModel.setText(car.getModel());
            etPrice.setText(String.valueOf(car.getPrice()));
            etImageUrl.setText(car.getImageUrl());
        }

        builder.setView(dialogView)
                .setTitle(car == null ? "Добавить автомобиль" : "Редактировать автомобиль")
                .setPositiveButton("Сохранить", (dialog, id) -> {
                    // Валидация данных
                    String brand = etBrand.getText().toString().trim();
                    String model = etModel.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String imageUrl = etImageUrl.getText().toString().trim();
                    String bodyType = actBodyType.getText().toString().trim().toLowerCase();

                    if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty() || bodyType.isEmpty()) {
                        Toast.makeText(getContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Некорректная цена", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Создаем/обновляем авто
                    Car newCar = new Car(brand, model, price, imageUrl, bodyType);
                    if (car == null) {
                        addCar(newCar);
                    } else {
                        updateCar(car.getId(), newCar);
                    }
                })
                .setNegativeButton("Отмена", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addCar(Car car) {
        progressBar.setVisibility(View.VISIBLE);

        // Добавляем проверку bodyType
        if (car.getBodyType() == null || car.getBodyType().isEmpty()) {
            car.setBodyType("седан"); // Значение по умолчанию
        }

        db.collection("cars")
                .add(car)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Автомобиль добавлен", Toast.LENGTH_SHORT).show();
                    loadCars();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCar(String carId, Car car) {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("brand", car.getBrand());
        updates.put("model", car.getModel());
        updates.put("price", car.getPrice());
        updates.put("imageUrl", car.getImageUrl());
        updates.put("bodyType", car.getBodyType().toLowerCase());

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
                    Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}