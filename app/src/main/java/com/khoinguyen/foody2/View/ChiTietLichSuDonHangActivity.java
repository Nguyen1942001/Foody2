package com.khoinguyen.foody2.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterChiTietLichSuDonHang;
import com.khoinguyen.foody2.Adapters.AdapterLichSuDonHang;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ChiTietLichSuDonHangActivity extends AppCompatActivity {

    TextView tvMaDonHang, tvMaDonHang2, tvTenQuanAn, tvDiaChiQuanAn, tvTenQuanAn2, tvDiaChiGiaoHang, tvTongTien;
    ImageView imgQuanAn;
    Button btnReview, btnDatLai;
    RecyclerView recyclerMonAn;
    Toolbar toolbar;
    DonHangModel donHangModel;
    Bitmap hinhquanan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chitiet_lichsudonhang);

        tvMaDonHang = findViewById(R.id.tvMaDonHang);
        tvMaDonHang2 = findViewById(R.id.tvMaDonHang2);
        tvTenQuanAn = findViewById(R.id.tvTenQuanAn);
        tvDiaChiQuanAn = findViewById(R.id.tvDiaChiQuanAn);
        imgQuanAn = findViewById(R.id.imgQuanAn);
        tvTenQuanAn2 = findViewById(R.id.tvTenQuanAn2);
        tvDiaChiGiaoHang = findViewById(R.id.tvDiaChiGiaoHang);
        tvTongTien = findViewById(R.id.tvTongTien);
        btnReview = findViewById(R.id.btnReview);
        btnDatLai = findViewById(R.id.btnDatLai);
        recyclerMonAn = findViewById(R.id.recyclerMonAn);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        donHangModel = getIntent().getParcelableExtra("donhang");

        ThongTinDonHang();

        recyclerMonAn.setLayoutManager(new LinearLayoutManager(ChiTietLichSuDonHangActivity.this));
        AdapterChiTietLichSuDonHang adapterChiTietLichSuDonHang = new AdapterChiTietLichSuDonHang
                (ChiTietLichSuDonHangActivity.this, R.layout.layout_chitiet_lichsudonhang,
                        donHangModel.getDanhsachmonan());

        recyclerMonAn.setAdapter(adapterChiTietLichSuDonHang);
        adapterChiTietLichSuDonHang.notifyDataSetChanged();
    }

    private void ThongTinDonHang() {
        DownLoadHinhMonAn(donHangModel.getLinkhinhquanan(), imgQuanAn);

        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        tvMaDonHang.setText(getString(R.string.donhang) + " " + donHangModel.getMadonhang());
        tvMaDonHang2.setText(getString(R.string.madon) + " " + donHangModel.getMadonhang());
        tvTenQuanAn.setText(donHangModel.getTenquanan());
        tvDiaChiQuanAn.setText(donHangModel.getDiachiquanan());
        tvTenQuanAn2.setText(donHangModel.getTenquanan());
        tvDiaChiGiaoHang.setText(donHangModel.getDiachi());
        tvTongTien.setText(numberFormat.format(donHangModel.getTongtien()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void DownLoadHinhMonAn(String linkhinhquanan, ImageView imgQuanAn) {
        // Dowload hình ảnh của món ăn
        StorageReference storageHinhMonAn = FirebaseStorage.getInstance().getReference().
                child("hinhanh").child(linkhinhquanan);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhMonAn.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap hinhquanan = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgQuanAn.setImageBitmap(hinhquanan);
            }
        });
    }
}
