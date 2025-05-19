package com.example.cardealership_cursovaya.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardealership_cursovaya.R;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
    private List<String> bodyTypes;
    private int selectedPosition = 0;
    private OnFilterClickListener listener;

    public interface OnFilterClickListener {
        void onFilterClick(String bodyType);
    }

    public FilterAdapter(List<String> bodyTypes, OnFilterClickListener listener) {
        this.bodyTypes = new ArrayList<>();
        this.bodyTypes.add("Все");
        this.bodyTypes.addAll(bodyTypes);
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String bodyType = bodyTypes.get(position);
        holder.filterButton.setText(bodyType);
        holder.filterButton.setSelected(position == selectedPosition);

        holder.filterButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            listener.onFilterClick(position == 0 ? null : bodyType);
        });
    }

    @Override
    public int getItemCount() {
        return bodyTypes.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        Button filterButton;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            filterButton = itemView.findViewById(R.id.filter_button);
        }
    }
}