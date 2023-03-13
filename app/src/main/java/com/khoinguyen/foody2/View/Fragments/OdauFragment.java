package com.khoinguyen.foody2.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Controller.OdauController;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.R;

public class OdauFragment extends Fragment {

    OdauController odauController;
    RecyclerView recyclerOdau;
    ProgressBar progressBarOdau;
    SharedPreferences sharedPreferences;
    NestedScrollView nestScrollViewOdau;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_odau, container, false);
        recyclerOdau = view.findViewById(R.id.recyclerOdau);
        progressBarOdau = view.findViewById(R.id.progressBarOdau);
        nestScrollViewOdau = view.findViewById(R.id.nestScrollViewOdau);

        sharedPreferences = getContext().getSharedPreferences("toado", Context.MODE_PRIVATE);
        Location vitrihientai = new Location("");
        vitrihientai.setLatitude(Double.parseDouble(sharedPreferences.getString("latitude", "0")));
        vitrihientai.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude", "0")));

        odauController = new OdauController(getContext());

        odauController.getDanhSachQuanAnController(getContext(), nestScrollViewOdau, recyclerOdau, progressBarOdau, vitrihientai);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
