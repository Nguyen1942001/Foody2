package com.khoinguyen.foody2.View;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterBinhLuan;
import com.khoinguyen.foody2.Adapters.AdapterMonAn;
import com.khoinguyen.foody2.Adapters.AdapterRecyclerHinhBinhLuan;
import com.khoinguyen.foody2.Controller.ChiTietQuanAnController;
import com.khoinguyen.foody2.Controller.ThucDonController;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.TienIchModel;
import com.khoinguyen.foody2.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChiTietQuanAnActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    TextView txtTenQuanAn, txtDiaChiQuanAn, txtThoiGianHoatDong, txtTrangThaiHoatDong,
            txtTongSoHinhAnh, txtTongSoBinhLuan, txtTongSoCheckIn, txtTongSoLuuLai, txtTieuDeToolbar,
            txtGioiHanGia, txtTenWifi, txtMatKhauWifi, txtNgayDangWifi;
    Button btnBinhLuan, btnDatMon;
    RecyclerView recyclerViewBinhLuan, recyclerThucDon;
    Toolbar toolbar;
    QuanAnModel quanAnModel;
    AdapterBinhLuan adapterBinhLuan;
    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    LinearLayout khungTienIch, khungWifi;
    ViewFlipper viewFlipper;
    ImageView imPre, imNext;
    float toadox1, toadox2;

    ChiTietQuanAnController chiTietQuanAnController;
    ThucDonController thucDonController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_chitietquanan);

        txtTenQuanAn = findViewById(R.id.txtTenQuanAn);
        txtDiaChiQuanAn = findViewById(R.id.txtDiaChiQuanAn);
        txtThoiGianHoatDong = findViewById(R.id.txtThoiGianHoatDong);
        txtTrangThaiHoatDong = findViewById(R.id.txtTrangThaiHoatDong);
        txtTongSoHinhAnh = findViewById(R.id.tongSoHinhAnh);
        txtTongSoBinhLuan = findViewById(R.id.tongSoBinhLuan);
        txtTongSoCheckIn = findViewById(R.id.tongSoCheckin);
        txtTongSoLuuLai = findViewById(R.id.tongSoLuuLai);
        txtTieuDeToolbar = findViewById(R.id.txtTieuDeToolbar);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewBinhLuan = findViewById(R.id.recyclerBinhLuanChiTietQuanAn);
        txtGioiHanGia = findViewById(R.id.txtGioiHanGia);
        khungTienIch = findViewById(R.id.khungTienIch);
        txtTenWifi = findViewById(R.id.txtTenWifi);
        txtMatKhauWifi = findViewById(R.id.txtMatKhauWifi);
        khungWifi = findViewById(R.id.khungWifi);
        txtNgayDangWifi = findViewById(R.id.txtNgayDangWifi);
        btnBinhLuan = findViewById(R.id.btnBinhLuan);
        viewFlipper = findViewById(R.id.viewFlipper);
        imNext = findViewById(R.id.imNext);
        imPre = findViewById(R.id.imPrev);
        recyclerThucDon = findViewById(R.id.recyclerThucDon);
        btnDatMon = findViewById(R.id.btnDatMon);

        btnBinhLuan.setOnClickListener(this);
        btnDatMon.setOnClickListener(this);
        imNext.setOnClickListener(this);
        imPre.setOnClickListener(this);

        // Hiệu ứng chuyển ảnh
        viewFlipper.setInAnimation(this, android.R.anim.fade_in);
        viewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        // Google map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Lấy dữ liệu intent gửi qua từ trang chủ (gửi object QuanAnModel)
        quanAnModel = getIntent().getParcelableExtra("quanan");

        chiTietQuanAnController = new ChiTietQuanAnController();
        thucDonController = new ThucDonController();

        HienThiChiTietQuanAn();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void HienThiChiTietQuanAn () {

        // Get the current time
        Calendar currentTime = Calendar.getInstance();

        String[] giomocua = splitStringHour(quanAnModel.getGiomocua());
        String[] giodongcua = splitStringHour(quanAnModel.getGiodongcua());

        // Set the open time and closed time
        Calendar openTime = Calendar.getInstance();
        openTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(giomocua[0]));
        openTime.set(Calendar.MINUTE, Integer.parseInt(giomocua[1]));

        Calendar closedTime = Calendar.getInstance();
        closedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(giodongcua[0]));
        closedTime.set(Calendar.MINUTE, Integer.parseInt(giodongcua[1]));

        // Compare the current time to the open and closed times
        if (currentTime.after(openTime) && currentTime.before(closedTime)) {
            txtTrangThaiHoatDong.setText(getString(R.string.dangmocua));
        } else {
            txtTrangThaiHoatDong.setText(getString(R.string.dadongcua));
        }

        txtTieuDeToolbar.setText(quanAnModel.getTenquanan());

        txtTenQuanAn.setText(quanAnModel.getTenquanan());
        txtDiaChiQuanAn.setText(quanAnModel.getChiNhanhQuanAnModelList().get(0).getDiachi());
        txtThoiGianHoatDong.setText(quanAnModel.getGiomocua() + " - " + quanAnModel.getGiodongcua());
        txtTongSoHinhAnh.setText(quanAnModel.getHinhanhquanan().size() + "");
        txtTongSoBinhLuan.setText(quanAnModel.getBinhLuanModelList().size() + "");

        // Downoad hình tiện ích của mỗi quán ăn
        DownloadHinhTienIch();

        if (quanAnModel.getGiatoida() != 0 && quanAnModel.getGiatoithieu() != 0) {
            NumberFormat numberFormat = new DecimalFormat("###,###");
            String giatoithieu = numberFormat.format(quanAnModel.getGiatoithieu()) + "đ";
            String giatoida = numberFormat.format(quanAnModel.getGiatoida()) + "đ";
            txtGioiHanGia.setText(giatoithieu + " - " + giatoida);
        }
        else {
            txtGioiHanGia.setVisibility(View.INVISIBLE);
        }


        // Download video trailer của quán ăn
        if (quanAnModel.getVideogioithieu().compareTo("") != 0) {
            FrameLayout khungVideoTrailer = new FrameLayout(this);
            khungVideoTrailer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            VideoView videoTrailer = new VideoView(this);
            videoTrailer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));

            ImageView playImageView = new ImageView(this);
            playImageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            playImageView.setImageResource(android.R.drawable.ic_media_play);

            khungVideoTrailer.addView(videoTrailer);
            khungVideoTrailer.addView(playImageView);

            StorageReference storageVideoTrailer = FirebaseStorage.getInstance().getReference().
                    child("video").child(quanAnModel.getVideogioithieu());
            storageVideoTrailer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    videoTrailer.setVideoURI(uri);
                    videoTrailer.seekTo(1);
                    viewFlipper.addView(khungVideoTrailer);
                }
            });

            // Click vào nút play mới phát video
            playImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoTrailer.start();

                    // Công cụ cho phép stop/start video, tua nhanh video
                    MediaController mediaController = new MediaController(ChiTietQuanAnActivity.this);
                    videoTrailer.setMediaController(mediaController);
                    playImageView.setVisibility(View.GONE);
                }
            });
        }

        // Dowload hình ảnh của quán ăn
        List<Bitmap> bitmapList = new ArrayList<>();
        for (String linkhinh : quanAnModel.getHinhanhquanan()) {
            StorageReference storageHinhQuanAn = FirebaseStorage.getInstance().getReference().
                    child("hinhanh").child(linkhinh);

            long ONE_MEGABYTE = 1024 * 1024 * 5;
            storageHinhQuanAn.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmapList.add(bitmap);

                    if (bitmapList.size() == quanAnModel.getHinhanhquanan().size()) {
                        for (Bitmap hinhquanan : bitmapList) {
                            ImageView imageView = new ImageView(ChiTietQuanAnActivity.this);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            imageView.setImageBitmap(hinhquanan);
                            viewFlipper.addView(imageView);
                        }
                    }
                }
            });
        }


        // Load danh sách bình luận của quán ăn
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewBinhLuan.setLayoutManager(layoutManager);
        adapterBinhLuan = new AdapterBinhLuan(this, R.layout.custom_layout_binhluan, quanAnModel.getBinhLuanModelList());
        recyclerViewBinhLuan.setAdapter(adapterBinhLuan);
        adapterBinhLuan.notifyDataSetChanged();

        NestedScrollView nestedScrollviewChiTiet = findViewById(R.id.nestedScrollviewChiTiet);
        nestedScrollviewChiTiet.smoothScrollTo(0,0);

        // Load danh sách wifi của quán
        chiTietQuanAnController.HienThiDanhSachQuanAn(quanAnModel.getMaquanan(), txtTenWifi, txtMatKhauWifi, txtNgayDangWifi);
        khungWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDanhSachWifi = new Intent(ChiTietQuanAnActivity.this, CapNhatDanhSachWifiActivity.class);
                iDanhSachWifi.putExtra("maquanan", quanAnModel.getMaquanan());
                startActivity(iDanhSachWifi);
            }
        });


        // Lấy danh sách thực đơn và món ăn của quán
        thucDonController.getDanhSachThucDonQuanAnTheoMa(this, quanAnModel.getMaquanan(), recyclerThucDon);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String[] splitStringHour (String time) {
        String[] timeParts = time.split(":");
        return timeParts;
    }


    // Load google map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        double latitude = quanAnModel.getChiNhanhQuanAnModelList().get(0).getLatitude();
        double longitude = quanAnModel.getChiNhanhQuanAnModelList().get(0).getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(quanAnModel.getTenquanan());

        googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.moveCamera(cameraUpdate);

    }

    private void DownloadHinhTienIch () {
        for (String matienich : quanAnModel.getTienich()) {
            DatabaseReference nodeTienIch = FirebaseDatabase.getInstance().getReference().
                    child("quanlytienichs").child(matienich);
            nodeTienIch.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TienIchModel tienIchModel = dataSnapshot.getValue(TienIchModel.class);

                    StorageReference storageHinhQuanAn = FirebaseStorage.getInstance().getReference().
                            child("hinhtienich").child(tienIchModel.getHinhtienich());
                    long ONE_MEGABYTE = 1024 * 1024 * 5;
                    storageHinhQuanAn.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            ImageView imageTienIch = new ImageView(ChiTietQuanAnActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
                            layoutParams.setMargins(10,10,10,10);
                            imageTienIch.setLayoutParams(layoutParams);
                            imageTienIch.setPadding(5,5,5,5);
                            imageTienIch.setScaleType(ImageView.ScaleType.FIT_XY);

                            imageTienIch.setImageBitmap(bitmap);
                            khungTienIch.addView(imageTienIch);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBinhLuan:
                Intent iBinhLuan = new Intent(this, BinhLuanActivity.class);
                iBinhLuan.putExtra("maquanan", quanAnModel.getMaquanan());
                iBinhLuan.putExtra("tenquan", quanAnModel.getTenquanan());
                iBinhLuan.putExtra("diachi", quanAnModel.getChiNhanhQuanAnModelList().get(0).getDiachi());
                startActivity(iBinhLuan);
                break;

            case R.id.btnDatMon:
                Intent iDatMon = new Intent(this, DatMonActivity.class);
                iDatMon.putExtra("tenquan", quanAnModel.getTenquanan());
                startActivity(iDatMon);
                break;

            case R.id.imNext:
                viewFlipper.showNext();
                break;

            case R.id.imPrev:
                viewFlipper.showPrevious();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                toadox1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                toadox2 = event.getX();
                if (toadox2 > toadox1) {
                    viewFlipper.showPrevious();
                }
                else {
                    viewFlipper.showNext();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
