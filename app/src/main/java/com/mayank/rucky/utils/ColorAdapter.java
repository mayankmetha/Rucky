package com.mayank.rucky.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mayank.rucky.R;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    private final int[] colorList;
    Context adapterContext;
    Config config;
    public static int selectedPosition;
    private static ClickListener clickListener;
    ArrayList<ViewHolder> views;

    public ColorAdapter(int[] colorSet, Context context) {
        colorList = colorSet;
        adapterContext = context;
        config = new Config(adapterContext);
        views = new ArrayList<>();
    }

    public void onItemClicked(ClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        views.add(position,holder);
        if (position == config.getAccentTheme()) {
            selectedPosition = position;
            holder.getButton().setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.color_button_selected));
        } else {
            holder.getButton().setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.color_button));
        }
        holder.getButton().setColorFilter(colorList[position]);
        holder.getButton().setOnClickListener(v -> clickListener.onItemClick(position, v));
    }

    public void updateSelection(int position) {
        views.get(selectedPosition).getButton().setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.color_button));
        views.get(selectedPosition).getButton().setColorFilter(colorList[selectedPosition]);
        selectedPosition = position;
    }

    @Override
    public int getItemCount() {
        return colorList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (ImageView) itemView.findViewById(R.id.color_button);
            button.setOnClickListener(v -> clickListener.onItemClick(getAdapterPosition(),v));
        }

        public ImageView getButton() {
            return button;
        }
    }

}
