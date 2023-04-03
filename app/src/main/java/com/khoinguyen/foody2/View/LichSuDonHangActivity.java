package com.khoinguyen.foody2.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Adapters.AdapterDatMon;
import com.khoinguyen.foody2.Adapters.AdapterLichSuDonHang;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

public class LichSuDonHangActivity extends AppCompatActivity {

    RecyclerView recyclerLichSuDonHang;
    List<DonHangModel> donHangModelList;
    ThanhVienModel thanhVienModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lichsudonhang);

        recyclerLichSuDonHang = findViewById(R.id.recyclerLichSuDonHang);

        donHangModelList = new ArrayList<>();

        thanhVienModel = getIntent().getParcelableExtra("thanhvien");

        LayThongTinDonHang();
    }

    private void LayThongTinDonHang() {
        if (thanhVienModel.getDanhsachdonhang().size() > 0) {
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot dataDonHang = dataSnapshot.child("donhangs");

                    for (String madonhang : thanhVienModel.getDanhsachdonhang()) {
                        for (DataSnapshot valueDonHang : dataDonHang.getChildren()) {
                            if (madonhang.compareTo(valueDonHang.getKey()) == 0) {
                                DonHangModel donHangModel = valueDonHang.getValue(DonHangModel.class);
                                donHangModelList.add(donHangModel);

                                recyclerLichSuDonHang.setLayoutManager(new LinearLayoutManager(LichSuDonHangActivity.this));
                                AdapterLichSuDonHang adapterLichSuDonHang = new AdapterLichSuDonHang(LichSuDonHangActivity.this, R.layout.layout_lichsudonhang, donHangModelList);
                                recyclerLichSuDonHang.setAdapter(adapterLichSuDonHang);
                                adapterLichSuDonHang.notifyDataSetChanged();
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
