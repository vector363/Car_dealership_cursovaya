package com.example.cardealership_cursovaya;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final List<View> fragments = new ArrayList<>();
    private final Context context;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    public void addFragment(View fragment) {
        fragments.add(fragment);
    }

    public View getViewAt(int position) {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FrameLayout frameLayout = (FrameLayout) holder.itemView;
        frameLayout.removeAllViews();
        frameLayout.addView(fragments.get(position));
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}