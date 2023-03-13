package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Adapters.AdapterHienThiHinhBinhLuanDuocChon;
import com.khoinguyen.foody2.Controller.BinhLuanController;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

public class BinhLuanActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtTenQuan, txtDiaChi, txtDangBinhLuan;
    EditText edTieuDeBinhLuan, edNoiDungBinhLuan;
    Toolbar toolbar;
    ImageButton btnChonHinh;
    RatingBar ratingBar;
    RecyclerView recyclerChonHinhBinhLuan;
    AdapterHienThiHinhBinhLuanDuocChon adapterHienThiHinhBinhLuanDuocChon;
    List<String> listHinhDuocChon;
    final int REQUEST_CHONHINH_BINHLUAN = 11;
    String maquanan;
    SharedPreferences sharedPreferences;
    BinhLuanController binhLuanController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_binhluan);

        txtTenQuan = findViewById(R.id.txtTenQuan);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        toolbar = findViewById(R.id.toolbar);
        ratingBar = findViewById(R.id.ratingBar);
        btnChonHinh = findViewById(R.id.btnChonHinh);
        recyclerChonHinhBinhLuan = findViewById(R.id.recyclerChonHinhBinhLuan);
        txtDangBinhLuan = findViewById(R.id.txtDangBinhLuan);
        edTieuDeBinhLuan = findViewById(R.id.edTieuDeBinhLuan);
        edNoiDungBinhLuan = findViewById(R.id.edNoiDungBinhLuan);

        binhLuanController = new BinhLuanController();
        listHinhDuocChon = new ArrayList<>();

        // Lấy uid của người dùng đăng nhập
        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        maquanan = getIntent().getStringExtra("maquanan");
        String tenquan = getIntent().getStringExtra("tenquan");
        String diachi = getIntent().getStringExtra("diachi");

        txtTenQuan.setText(tenquan);
        txtDiaChi.setText(diachi);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnChonHinh.setOnClickListener(this);
        txtDangBinhLuan.setOnClickListener(this);

        // Hiển thị các hình bình luận được chọn
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.HORIZONTAL, false);
        recyclerChonHinhBinhLuan.setLayoutManager(layoutManager);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChonHinh:
                Intent iChonHinhBinhLuan = new Intent(this, ChonHinhBinhLuanActivity.class);
                startActivityForResult(iChonHinhBinhLuan, REQUEST_CHONHINH_BINHLUAN);
                break;
            case R.id.txtDangBinhLuan:
                BinhLuanModel binhLuanModel = new BinhLuanModel();
                String tieude = edTieuDeBinhLuan.getText().toString();
                String noidung = edNoiDungBinhLuan.getText().toString();
                String mauser = sharedPreferences.getString("mauser", "");
                double chamdiem = ratingBar.getRating() * 2;

                binhLuanModel.setTieude(tieude);
                binhLuanModel.setNoidung(noidung);
                binhLuanModel.setChamdiem(chamdiem);
                binhLuanModel.setLuotthich(0);
                binhLuanModel.setMauser(mauser);

                binhLuanController.ThemBinhLuan(maquanan, binhLuanModel, listHinhDuocChon);
                Toast.makeText(this, getString(R.string.binhluanthanhcong), Toast.LENGTH_LONG).show();

                Intent iTrangChu = new Intent(this, TrangChuActivity.class);
                startActivity(iTrangChu);
                finish();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHONHINH_BINHLUAN) {
            if (resultCode == RESULT_OK) {
                listHinhDuocChon = data.getStringArrayListExtra("listHinhDuocChon");
                adapterHienThiHinhBinhLuanDuocChon = new AdapterHienThiHinhBinhLuanDuocChon
                        (this, R.layout.custom_layout_hienthihinhbinhluanduocchon, listHinhDuocChon);
                recyclerChonHinhBinhLuan.setAdapter(adapterHienThiHinhBinhLuanDuocChon);
                adapterHienThiHinhBinhLuanDuocChon.notifyDataSetChanged();

            }
        }

    }
}
