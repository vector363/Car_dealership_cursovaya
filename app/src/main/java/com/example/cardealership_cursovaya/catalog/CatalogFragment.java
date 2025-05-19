package com.example.cardealership_cursovaya.catalog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.main.Car;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class CatalogFragment extends Fragment {
    private CarAdapter adapter;
    private FirebaseFirestore db;
    private RecyclerView carsRecyclerView, filterRecyclerView;
    private String selectedBodyType = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        db = FirebaseFirestore.getInstance();

        // Инициализация RecyclerView
        carsRecyclerView = view.findViewById(R.id.recycler_view_cars);
        filterRecyclerView = view.findViewById(R.id.filter_recycler_view); // Используем правильный ID

        setupFilterRecyclerView();
        setupCarRecyclerView();

        return view;
    }

    private void setupFilterRecyclerView() {
        // Создаем изменяемый список
        List<String> bodyTypes = new ArrayList<>();
        bodyTypes.add("Седан");
        bodyTypes.add("Хэтчбек");
        bodyTypes.add("Универсал");
        bodyTypes.add("Внедорожник");
        bodyTypes.add("Купе");

        FilterAdapter filterAdapter = new FilterAdapter(bodyTypes, bodyType -> {
            selectedBodyType = bodyType;
            setupCarRecyclerView(); // Перезагружаем список с новым фильтром
        });

        // Устанавливаем горизонтальный LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        filterRecyclerView.setLayoutManager(layoutManager);
        filterRecyclerView.setAdapter(filterAdapter);
    }

    private void setupCarRecyclerView() {
        Query query = db.collection("cars");

        if (selectedBodyType != null && !selectedBodyType.equals("Все")) {
            // Приводим к нижнему регистру и удаляем пробелы
            String filterValue = selectedBodyType.toLowerCase().trim();
            Log.d("FilterDebug", "Filtering by: " + filterValue); // Добавьте лог
            query = query.whereEqualTo("bodyType", filterValue);
        }

        query = query.orderBy("price", Query.Direction.ASCENDING);

        // Добавьте обработку ошибок
        try {
            FirestoreRecyclerOptions<Car> options = new FirestoreRecyclerOptions.Builder<Car>()
                    .setQuery(query, snapshot -> {
                        Car car = snapshot.toObject(Car.class);
                        if (car != null) {
                            car.setId(snapshot.getId());
                            Log.d("CarDebug", "Loaded car: " + car.getBrand() + " | " + car.getBodyType());
                        }
                        return car;
                    })
                    .build();

            if (adapter != null) {
                adapter.stopListening();
            }

            adapter = new CarAdapter(options);
            adapter.setOnItemClickListener(car -> {
                CarDetailFragment detailFragment = CarDetailFragment.newInstance(car);
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack("car_detail")
                        .commit();
            });

            carsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            carsRecyclerView.setAdapter(adapter);
            adapter.startListening();

        } catch (Exception e) {
            Log.e("FirestoreError", "Query failed", e);
            Toast.makeText(getContext(), "Filter error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}