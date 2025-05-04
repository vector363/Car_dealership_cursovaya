package com.example.cardealership_cursovaya.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardealership_cursovaya.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class CatalogFragment extends Fragment {
    private CarAdapter adapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_cars);

        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        Query query = db.collection("cars")
                .orderBy("price", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Car> options = new FirestoreRecyclerOptions.Builder<Car>()
                .setQuery(query, Car.class)
                .build();

        adapter = new CarAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}