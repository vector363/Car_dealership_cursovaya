package com.example.cardealership_cursovaya.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cardealership_cursovaya.CarDetailFragment;
import com.example.cardealership_cursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteCarAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Инициализация элементов UI
        recyclerView = view.findViewById(R.id.recycler_view_favorites);
        progressBar = view.findViewById(R.id.progress_bar);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadFavoriteCars();

        return view;
    }

    private void loadFavoriteCars() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            // Пользователь не авторизован
            progressBar.setVisibility(View.GONE);
            return;
        }

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> carIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            carIds.add(document.getString("carId"));
                        }
                        loadCarsByIds(carIds);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.e("FavoriteFragment", "Error loading favorites", task.getException());
                    }
                });
    }

    private void loadCarsByIds(List<String> carIds) {
        if (carIds.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        db.collection("cars")
                .whereIn(FieldPath.documentId(), carIds)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        List<Car> favoriteCars = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Car car = document.toObject(Car.class);
                            car.setId(document.getId());
                            favoriteCars.add(car);
                        }
                        setupAdapter(favoriteCars);
                    } else {
                        Log.e("FavoriteFragment", "Error loading cars", task.getException());
                    }
                });
    }

    private void setupAdapter(List<Car> cars) {
        adapter = new FavoriteCarAdapter(cars, car -> {
            CarDetailFragment detailFragment = CarDetailFragment.newInstance(car);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // вход нового фрагмента
                            R.anim.slide_out_left,  // выход текущего
                            R.anim.slide_in_left,   // вход при возврате
                            R.anim.slide_out_right) // выход при возврате
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack("favorite_car_detail") // Уникальное имя для back stack
                    .commit();
        });
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            loadFavoriteCars();
        }
    }
}