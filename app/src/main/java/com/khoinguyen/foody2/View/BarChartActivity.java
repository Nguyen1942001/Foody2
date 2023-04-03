package com.khoinguyen.foody2.View;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarChartActivity extends AppCompatActivity {

    BarChart barChart;
    Toolbar toolbar;
    ArrayList<BarEntry> newEntries;
    ArrayList<DonHangModel> donHangModelList;
    ArrayList<QuanAnModel> quanAnModelList;
    ArrayList<ThanhVienModel> thanhVienModelList;
    String thoigian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_barchart);

        toolbar = findViewById(R.id.toolbar);
        barChart = findViewById(R.id.barChart);

        // Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        donHangModelList = new ArrayList<>();
        quanAnModelList = new ArrayList<>();
        thanhVienModelList = new ArrayList<>();

        thoigian = getIntent().getStringExtra("nam");

        quanAnModelList = getIntent().getParcelableArrayListExtra("danhSachQuanAn");
        if (quanAnModelList != null) {
            ThongKeSoLuongQuanAn();
        }

        String maquanan = getIntent().getStringExtra("maquanan");
        boolean check = getIntent().getBooleanExtra("donHangCuaQuanAn", false);
        if (check) {
//            Log.d("kiemtramaquanan", maquanan);
            LaySoLuongDonHangCuaQuanAn(maquanan);
        }

        thanhVienModelList = getIntent().getParcelableArrayListExtra("danhSachThanhVien");
        if (thanhVienModelList != null) {
            ThongKeSoLuongThanhVien();
        }

        donHangModelList = getIntent().getParcelableArrayListExtra("danhSachDonHang");
        if (donHangModelList != null) {
            ThongKeSoLuongDonHang(donHangModelList);
        }
    }

    private void DuLieuBieuDo () {
        BarDataSet dataSet = new BarDataSet(newEntries, "Sample Data");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.getDescription().setText("Sample Bar Chart");
        barChart.animateY(2000);
        barChart.invalidate();
    }

    private void ThongKeSoLuongQuanAn () {
        Map<Integer, List<QuanAnModel>> danhSachQuanAnTheoThang = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            danhSachQuanAnTheoThang.put(i, new ArrayList<>());
        }

        // Thêm quán ăn theo từng tháng vào Map danhSachQuanAnTheoThang
        for (QuanAnModel quanAnModel1 : quanAnModelList) {
            String year = quanAnModel1.getNgaytao().substring(6);
            int month = Integer.valueOf(quanAnModel1.getNgaytao().substring(3, 5));

            if (thoigian.compareTo(year) == 0) {
                danhSachQuanAnTheoThang.get(month).add(quanAnModel1);
            }
        }

        // Chuyển đổi Map<Integer, List<QuanAnModel>> sang HashMap<Integer, Integer>
        HashMap<Integer, Integer> newMap = new HashMap<>();
        for (Map.Entry<Integer, List<QuanAnModel>> entry : danhSachQuanAnTheoThang.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().size());
        }

        newEntries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : newMap.entrySet()) {
            newEntries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        if (newEntries != null) {
            DuLieuBieuDo();
        }
    }

    private void ThongKeSoLuongThanhVien () {
        Map<Integer, List<ThanhVienModel>> danhSachThanhVienTheoThang = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            danhSachThanhVienTheoThang.put(i, new ArrayList<>());
        }

        // Thêm thành viên theo từng tháng vào Map danhSachQuanAnTheoThang
        for (ThanhVienModel thanhVienModel : thanhVienModelList) {
            String year = thanhVienModel.getNgaytao().substring(6);
            int month = Integer.valueOf(thanhVienModel.getNgaytao().substring(3, 5));

            if (thoigian.compareTo(year) == 0) {
                danhSachThanhVienTheoThang.get(month).add(thanhVienModel);
            }
        }

        // Chuyển đổi Map<Integer, List<QuanAnModel>> sang HashMap<Integer, Integer>
        HashMap<Integer, Integer> newMap = new HashMap<>();
        for (Map.Entry<Integer, List<ThanhVienModel>> entry : danhSachThanhVienTheoThang.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().size());
        }

        newEntries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : newMap.entrySet()) {
            newEntries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        if (newEntries != null) {
            DuLieuBieuDo();
        }

    }

    private void ThongKeSoLuongDonHang (ArrayList<DonHangModel> donHangModelList) {
        Map<Integer, List<DonHangModel>> danhSachDonHangTheoThang = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            danhSachDonHangTheoThang.put(i, new ArrayList<>());
        }

        // Thêm thành viên theo từng tháng vào Map danhSachQuanAnTheoThang
        for (DonHangModel donHangModel : donHangModelList) {
            String year = donHangModel.getNgaydathang().substring(6);
            int month = Integer.valueOf(donHangModel.getNgaydathang().substring(3, 5));

            if (thoigian.compareTo(year) == 0) {
                danhSachDonHangTheoThang.get(month).add(donHangModel);
            }
        }

        // Chuyển đổi Map<Integer, List<QuanAnModel>> sang HashMap<Integer, Integer>
        HashMap<Integer, Integer> newMap = new HashMap<>();
        for (Map.Entry<Integer, List<DonHangModel>> entry : danhSachDonHangTheoThang.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().size());
        }

        newEntries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : newMap.entrySet()) {
            newEntries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        if (newEntries != null) {
            DuLieuBieuDo();
        }

    }

    private void LaySoLuongDonHangCuaQuanAn(String maquanan) {
        ArrayList<DonHangModel> danhSachDonHangCuaQuanAn = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataDonHang = dataSnapshot.child("donhangs");

                for (DataSnapshot valueDonHang : dataDonHang.getChildren()) {
                    DonHangModel donHangModel = valueDonHang.getValue(DonHangModel.class);
                    if (donHangModel.getMaquanan().compareTo(maquanan) == 0) {
                        danhSachDonHangCuaQuanAn.add(donHangModel);
                    }
                }

                ThongKeSoLuongDonHang(danhSachDonHangCuaQuanAn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
