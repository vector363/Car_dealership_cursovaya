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
import com.google.firebase.auth.FirebaseUser;
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

        recyclerView = view.findViewById(R.id.recycler_view_favorites);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadFavoriteCars();

        return view;
    }

    private void loadFavoriteCars() {
        if (!isAdded() || getContext() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        db.collection("favorites")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (!isAdded()) return;

                    if (task.isSuccessful()) {
                        List<String> carIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String carId = document.getString("carId");
                            if (carId != null) {
                                carIds.add(carId);
                            }
                        }
                        loadCarsByIds(carIds);
                    } else {
                        Log.e("FavoriteFragment", "Error loading favorites", task.getException());
                    }
                });
    }

    private void loadCarsByIds(List<String> carIds) {
        if (carIds.isEmpty() || !isAdded()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("cars")
                .whereIn(FieldPath.documentId(), carIds)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (!isAdded()) return;

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
        if (!isAdded() || getContext() == null) return;

        adapter = new FavoriteCarAdapter(cars, car -> {
            if (!isAdded()) return;

            CarDetailFragment detailFragment = CarDetailFragment.newInstance(car);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack("favorite_car_detail")
                    .commit();
        }, requireContext());

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