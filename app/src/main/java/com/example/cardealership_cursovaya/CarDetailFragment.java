package com.example.cardealership_cursovaya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardealership_cursovaya.main.Car;
import com.squareup.picasso.Picasso;

public class CarDetailFragment extends Fragment {
    private static final String ARG_CAR = "car";

    private Car car;

    public static CarDetailFragment newInstance(Car car) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CAR, car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            car = getArguments().getParcelable(ARG_CAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        ImageView carImage = view.findViewById(R.id.car_image_detail);
        TextView brandModel = view.findViewById(R.id.car_brand_model_detail);
        TextView price = view.findViewById(R.id.car_price_detail);
        TextView bodyType = view.findViewById(R.id.car_body_type_detail);
        TextView year = view.findViewById(R.id.car_year_detail);
        TextView mileage = view.findViewById(R.id.car_mileage_detail);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        // Заполняем данные
        if (car != null) {
            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                Picasso.get().load(car.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(carImage);
            }

            brandModel.setText(String.format("%s %s, %s", car.getBrand(), car.getModel(), car.getYear()));
            price.setText(String.format("%,d ₽", (int)car.getPrice()).replace(",", " "));
            bodyType.setText(String.format("Тип кузова: %s", car.getBodyType()));
            year.setText(String.format("Год выпуска: %s г.", car.getYear()));
            mileage.setText(String.format("Пробег: %,d км", (int)car.getMileage()));
        }

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}