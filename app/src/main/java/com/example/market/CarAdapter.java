package com.example.market;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private List<Car> carList;
    private Context context;

    public CarAdapter(List<Car> carList, Context context) {
        this.carList = carList;
        this.context = context;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        private ImageView carImage;
        private TextView brandModelText, yearText, mileageText, priceText;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.carImage);
            brandModelText = itemView.findViewById(R.id.brandModelText);
            yearText = itemView.findViewById(R.id.yearText);
            mileageText = itemView.findViewById(R.id.mileageText);
            priceText = itemView.findViewById(R.id.priceText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Car car = carList.get(position);
                    Intent intent = new Intent(context, CarDetailActivity.class);
                    intent.putExtra("car_id", car.getId());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Car car) {
            brandModelText.setText(car.getBrand() + " " + car.getModel());
            yearText.setText(String.valueOf(car.getYear()));
            mileageText.setText(car.getMileage() + " км");
            priceText.setText(String.format("%.0f руб.", car.getPrice()));

            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(car.getImageUrl())
                        .placeholder(R.drawable.ic_car_placeholder)
                        .into(carImage);
            }
        }
    }
}