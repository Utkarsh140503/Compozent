package com.utkarsh.weatherwise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<CityItem> cities;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String city);
    }

    public CityAdapter(List<CityItem> cities) {
        this.cities = cities;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CityItem cityItem = cities.get(position);
        holder.cityNameTextView.setText(cityItem.getCityName());
        holder.cityImageView.setImageResource(cityItem.getImageResource());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(cityItem.getCityName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cityNameTextView;
        public ImageView cityImageView;

        public ViewHolder(View view) {
            super(view);
            cityNameTextView = view.findViewById(R.id.cityNameTextView);
            cityImageView = view.findViewById(R.id.cityImageView);
        }
    }
}

