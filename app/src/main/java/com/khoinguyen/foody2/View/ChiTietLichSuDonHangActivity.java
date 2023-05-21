package com.khoinguyen.foody2.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterChiTietLichSuDonHang;
import com.khoinguyen.foody2.Adapters.AdapterLichSuDonHang;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.Model.ChiNhanhQuanAnModel;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.Model.KhuyenMaiModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChiTietLichSuDonHangActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout noiMuaHang;
    TextView tvMaDonHang, tvMaDonHang2, tvTenQuanAn, tvDiaChiQuanAn, tvTenQuanAn2, tvDiaChiGiaoHang, tvTongTien;
    ImageView imgQuanAn;
    Button btnReview, btnDatLai;
    RecyclerView recyclerMonAn;
    Toolbar toolbar;
    DonHangModel donHangModel;
    QuanAnModel quanAnModel;
    Bitmap hinhquanan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chitiet_lichsudonhang);

        noiMuaHang = findViewById(R.id.noiMuaHang);
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

        btnDatLai.setOnClickListener(this);
        noiMuaHang.setOnClickListener(this);

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

        LayThongTinQuanAn();
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

    private void LayThongTinQuanAn() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshotQuanAn = dataSnapshot.child("quanans");
                // Lấy danh sách quán ăn
                for (DataSnapshot valueQuanAn : dataSnapshotQuanAn.getChildren()) {
                    if (valueQuanAn.getKey().compareTo(donHangModel.getMaquanan()) == 0) {
                        quanAnModel = valueQuanAn.getValue(QuanAnModel.class);
                        quanAnModel.setMaquanan(valueQuanAn.getKey());


                        // Lấy hình ảnh trong từng quán ăn
                        DataSnapshot dataSnapshotHinhQuanAn = dataSnapshot.child("hinhanhquanans").
                                child(valueQuanAn.getKey());
                        List<String> hinhanhList = new ArrayList<>();

                        for (DataSnapshot valueHinhQuanAn : dataSnapshotHinhQuanAn.getChildren()) {
                            hinhanhList.add(valueHinhQuanAn.getValue(String.class));
                        }
                        quanAnModel.setHinhanhquanan(hinhanhList);


                        // Lấy danh sách bình luận của quán ăn
                        DataSnapshot snapshotBinhLuan = dataSnapshot.child("binhluans").
                                child(valueQuanAn.getKey());
                        List<BinhLuanModel> binhLuanModels = new ArrayList<>();

                        for (DataSnapshot valueBinhLuan : snapshotBinhLuan.getChildren()) {
                            BinhLuanModel binhLuanModel = valueBinhLuan.getValue(BinhLuanModel.class);
                            binhLuanModel.setMabinhluan(valueBinhLuan.getKey());
                            // Lấy ra được thành viên thực hiện bình luận này
                            ThanhVienModel thanhVienModel = dataSnapshot.child("thanhviens").
                                    child(binhLuanModel.getMauser()).getValue(ThanhVienModel.class);
                            binhLuanModel.setThanhVienModel(thanhVienModel);

                            List<String> hinhanhBinhluanList = new ArrayList<>();
                            DataSnapshot nodeHinhAnhBinhLuan = dataSnapshot.child("hinhanhbinhluans").child(binhLuanModel.getMabinhluan());
                            for (DataSnapshot valueHinhAnhBinhLuan : nodeHinhAnhBinhLuan.getChildren()) {
                                hinhanhBinhluanList.add(valueHinhAnhBinhLuan.getValue(String.class));
                            }
                            binhLuanModel.setHinhanhBinhLuanList(hinhanhBinhluanList);

                            binhLuanModels.add(binhLuanModel);
                        }
                        quanAnModel.setBinhLuanModelList(binhLuanModels);


                        // Lấy chi nhánh quán ăn
                        DataSnapshot snapshotChiNhanhQuanAn = dataSnapshot.child("chinhanhquanans").child(quanAnModel.getMaquanan());
                        List<ChiNhanhQuanAnModel> chiNhanhQuanAnModels = new ArrayList<>();


                        for (DataSnapshot valueChiNhanhQuanAn : snapshotChiNhanhQuanAn.getChildren()) {
                            ChiNhanhQuanAnModel chiNhanhQuanAnModel = valueChiNhanhQuanAn.getValue(ChiNhanhQuanAnModel.class);

                            Location vitriquanan = new Location("");
                            vitriquanan.setLatitude(chiNhanhQuanAnModel.getLatitude());
                            vitriquanan.setLongitude(chiNhanhQuanAnModel.getLongitude());

                            SharedPreferences sharedPreferences = getSharedPreferences("toado", Context.MODE_PRIVATE);
                            Location vitrihientai = new Location("");
                            vitrihientai.setLatitude(Double.parseDouble(sharedPreferences.getString("latitude", "0")));
                            vitrihientai.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude", "0")));

                            double khoangcach = vitriquanan.distanceTo(vitrihientai) / 1000;
                            chiNhanhQuanAnModel.setKhoangcach(khoangcach);

                            chiNhanhQuanAnModels.add(chiNhanhQuanAnModel);
                        }
                        quanAnModel.setChiNhanhQuanAnModelList(chiNhanhQuanAnModels);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent iChiTietQuanAn = new Intent(this, ChiTietQuanAnActivity.class);

        switch (view.getId()) {
            case R.id.btnDatLai:
                iChiTietQuanAn.putExtra("datLaiQuanAn", quanAnModel);
                startActivity(iChiTietQuanAn);
                break;

            case R.id.noiMuaHang:
                iChiTietQuanAn.putExtra("datLaiQuanAn", quanAnModel);
                startActivity(iChiTietQuanAn);
                break;
        }
    }
}
