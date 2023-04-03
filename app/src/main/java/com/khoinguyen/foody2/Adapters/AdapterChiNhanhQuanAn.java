package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khoinguyen.foody2.Model.ChiNhanhQuanAnModel;
import com.khoinguyen.foody2.R;

import java.util.List;

public class AdapterChiNhanhQuanAn extends RecyclerView.Adapter<AdapterChiNhanhQuanAn.ViewHolderChiNhanhQuanAn> {

    Context context;
    int resource;
    List<ChiNhanhQuanAnModel> chiNhanhQuanAnModelList;
    String tenquanan;
    SupportMapFragment mapFragment;
    TextView txtDiaChiQuanAn;

    public AdapterChiNhanhQuanAn(Context context, int resource,
                                 List<ChiNhanhQuanAnModel> chiNhanhQuanAnModelList,
                                 String tenquanan, SupportMapFragment mapFragment, TextView txtDiaChiQuanAn
                                 ) {
        this.context = context;
        this.resource = resource;
        this.chiNhanhQuanAnModelList = chiNhanhQuanAnModelList;
        this.tenquanan = tenquanan;
        this.mapFragment = mapFragment;
        this.txtDiaChiQuanAn = txtDiaChiQuanAn;
    }

    public class ViewHolderChiNhanhQuanAn extends RecyclerView.ViewHolder {

        TextView tvDiaChiChiNhanhQuanAn;

        public ViewHolderChiNhanhQuanAn(@NonNull View itemView) {
            super(itemView);
            tvDiaChiChiNhanhQuanAn = itemView.findViewById(R.id.tvDiaChiChiNhanhQuanAn);
        }
    }

    @NonNull
    @Override
    public AdapterChiNhanhQuanAn.ViewHolderChiNhanhQuanAn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_chinhanhquanan, parent, false);
        AdapterChiNhanhQuanAn.ViewHolderChiNhanhQuanAn viewHolderChiNhanhQuanAn = new ViewHolderChiNhanhQuanAn(view);

        return viewHolderChiNhanhQuanAn;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChiNhanhQuanAn.ViewHolderChiNhanhQuanAn holder, int position) {
        ChiNhanhQuanAnModel chiNhanhQuanAnModel = chiNhanhQuanAnModelList.get(position);
        holder.tvDiaChiChiNhanhQuanAn.setText(chiNhanhQuanAnModel.getDiachi());

        holder.tvDiaChiChiNhanhQuanAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        LatLng latLng = new LatLng(chiNhanhQuanAnModel.getLatitude(), chiNhanhQuanAnModel.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(tenquanan);

                        googleMap.addMarker(markerOptions);

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                        googleMap.moveCamera(cameraUpdate);
                    }
                });

                txtDiaChiQuanAn.setText(chiNhanhQuanAnModel.getDiachi());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chiNhanhQuanAnModelList.size();
    }
}
