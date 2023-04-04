package com.khoinguyen.foody2.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Adapters.AdapterLichSuDonHang;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThongKeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBarChart, btnPieChart, btnLineChart;
    Spinner spinnerThoiGian, spinnerThanhVien, spinnerDonHang, spinnerQuanAn;
    Toolbar toolbar;
    ArrayList<DonHangModel> donHangModelList;
    ArrayList<QuanAnModel> quanAnModelList;
    ArrayList<ThanhVienModel> thanhVienModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thongke);

        toolbar = findViewById(R.id.toolbar);
        btnBarChart = findViewById(R.id.btnBarChart);
        btnPieChart = findViewById(R.id.btnPieChart);
        btnLineChart = findViewById(R.id.btnLineChart);
        spinnerDonHang = findViewById(R.id.spinnerDonHang);
        spinnerQuanAn = findViewById(R.id.spinnerQuanAn);
        spinnerThanhVien = findViewById(R.id.spinnerThanhVien);
        spinnerThoiGian = findViewById(R.id.spinnerThoiGian);

        btnBarChart.setOnClickListener(this);
        btnPieChart.setOnClickListener(this);
        btnLineChart.setOnClickListener(this);

        // Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinnerQuanAn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    spinnerThanhVien.setEnabled(true);
                    spinnerDonHang.setEnabled(true);

                    spinnerThanhVien.setSelection(0);
                    spinnerDonHang.setSelection(0);
                }
                else if (i == 1) {
                    spinnerThanhVien.setEnabled(false);
                    spinnerDonHang.setEnabled(false);
                }
                else {
                    spinnerThanhVien.setEnabled(false);
                    spinnerDonHang.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDonHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    spinnerQuanAn.setEnabled(true);
                    spinnerThanhVien.setEnabled(true);

                    spinnerThanhVien.setSelection(0);
                    spinnerQuanAn.setSelection(0);
                }
                else {
                    spinnerThanhVien.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerThanhVien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    spinnerQuanAn.setEnabled(true);
                    spinnerDonHang.setEnabled(true);

                    spinnerDonHang.setSelection(0);
                    spinnerQuanAn.setSelection(0);
                }
                else {
                    spinnerQuanAn.setEnabled(false);
                    spinnerDonHang.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        donHangModelList = new ArrayList<>();
        quanAnModelList = new ArrayList<>();
        thanhVienModelList = new ArrayList<>();

        setDataForSpinnerTime();

        LayThongTinQuanAn();
        LayThongTinThanhVien();
        LayThongTinDonHang();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBarChart:
                if (spinnerQuanAn.getSelectedItemPosition() == 0 && spinnerDonHang.getSelectedItemPosition() == 0
                        && spinnerThanhVien.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Hãy lọc dữ liệu muốn thống kê", Toast.LENGTH_SHORT).show();
                    return;
                }

                KiemTraFilter(BarChartActivity.class);
                break;

            case R.id.btnPieChart:
                if (spinnerQuanAn.getSelectedItemPosition() == 0 && spinnerDonHang.getSelectedItemPosition() == 0
                        && spinnerThanhVien.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Hãy lọc dữ liệu muốn thống kê", Toast.LENGTH_SHORT).show();
                    return;
                }

                KiemTraFilter(PieChartActivity.class);

                break;

            case R.id.btnLineChart:
                if (spinnerQuanAn.getSelectedItemPosition() == 0 && spinnerDonHang.getSelectedItemPosition() == 0
                        && spinnerThanhVien.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Hãy lọc dữ liệu muốn thống kê", Toast.LENGTH_SHORT).show();
                    return;
                }

                KiemTraFilter(LineChartActivity.class);

                break;
        }
    }

    private void setDataForSpinnerTime() {
        String[] nam = {"2020", "2021", "2022", "2023", "2024"};
        ArrayAdapter<String> adapterNam = new ArrayAdapter<String>(ThongKeActivity.this, R.layout.spinner_item, nam);
        spinnerThoiGian.setAdapter(adapterNam);
        adapterNam.notifyDataSetChanged();
    }

    private void LayThongTinDonHang() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataDonHang = dataSnapshot.child("donhangs");

                for (DataSnapshot valueDonHang : dataDonHang.getChildren()) {
                    DonHangModel donHangModel = valueDonHang.getValue(DonHangModel.class);
                    donHangModelList.add(donHangModel);
                }

                String[] items = {"Không", "SL đơn hàng"};
                ArrayAdapter<String> adapterDonHang = new ArrayAdapter<String>(ThongKeActivity.this, android.R.layout.simple_list_item_1, items);
                spinnerDonHang.setAdapter(adapterDonHang);
                adapterDonHang.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LayThongTinThanhVien() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");

                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    ThanhVienModel thanhVienModel = valueThanhVien.getValue(ThanhVienModel.class);
                    thanhVienModelList.add(thanhVienModel);
                }

                String[] items = {"Không", "SL thành viên"};
                ArrayAdapter<String> adapterThanhVien = new ArrayAdapter<String>(ThongKeActivity.this, android.R.layout.simple_list_item_1, items);
                spinnerThanhVien.setAdapter(adapterThanhVien);
                adapterThanhVien.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LayThongTinQuanAn() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataQuanAn = dataSnapshot.child("quanans");

                List<String> danhSachTenQuanAn = new ArrayList<>();
                danhSachTenQuanAn.add("Không");
                danhSachTenQuanAn.add("Số lượng quán ăn");
                for (DataSnapshot valueQuanAn : dataQuanAn.getChildren()) {
                    QuanAnModel quanAnModel = valueQuanAn.getValue(QuanAnModel.class);
                    quanAnModel.setMaquanan(valueQuanAn.getKey());
                    quanAnModelList.add(quanAnModel);

                    danhSachTenQuanAn.add(quanAnModel.getTenquanan());
                }

                ArrayAdapter<String> adapterQuanAn = new ArrayAdapter<String>(ThongKeActivity.this, android.R.layout.simple_list_item_1, danhSachTenQuanAn);
                spinnerQuanAn.setAdapter(adapterQuanAn);
                adapterQuanAn.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void KiemTraFilter(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);

        int positionNam = spinnerThoiGian.getSelectedItemPosition();
        String nam = (String) spinnerThoiGian.getItemAtPosition(positionNam);
        intent.putExtra("nam", nam);

        int positionQuanAn = spinnerQuanAn.getSelectedItemPosition();
        int positionThanhVien = spinnerThanhVien.getSelectedItemPosition();
        int positionDonHang = spinnerDonHang.getSelectedItemPosition();

        // Thống kê số lượng quán ăn
        if (spinnerQuanAn.isEnabled()) {
            if (positionQuanAn == 1) {
                intent.putParcelableArrayListExtra("danhSachQuanAn", quanAnModelList);
            }
            else if (positionQuanAn != 0) {
                String maquanan = quanAnModelList.get(positionQuanAn - 2).getMaquanan();
                intent.putExtra("maquanan", maquanan);

                int position = spinnerDonHang.getSelectedItemPosition();

                // Thống kê số lượng đơn hàng theo từng quán ăn
                if (position == 1) {
                    intent.putExtra("donHangCuaQuanAn", true);
                }
            }
        }

        if (spinnerThanhVien.isEnabled()) {
            // Thống kê theo số lượng thành viên
            if (positionThanhVien != 0) {
                intent.putParcelableArrayListExtra("danhSachThanhVien", thanhVienModelList);
            }
        }

        if (spinnerDonHang.isEnabled()) {
            // Thống kê theo số lượng đơn hàng
            if (positionDonHang != 0 && positionQuanAn == 0) {
                intent.putParcelableArrayListExtra("danhSachDonHang", donHangModelList);
            }
        }

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
