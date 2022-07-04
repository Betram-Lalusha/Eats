package com.example.eats.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Models.City;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.util.List;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder>{

    Context mContext;
    List<City> mCities;

    public CitiesAdapter(Context context, List<City> cities) {
        this.mCities = cities;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_city_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = mCities.get(position);
        holder.bind(city);
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mCities.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<City> list) {
        mCities.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{

        TextView mCityName;
        ImageView mCityImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCityName = itemView.findViewById(R.id.cityName);
            mCityImage = itemView.findViewById(R.id.cityImage);

        }

        public void bind(City city) {
            mCityName.setText(city.getName());
            Glide.with(mContext).load(city.getImageUrl()).into(mCityImage);
        }
    }
}
