package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterViewPagerTrangChu;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrangChuActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_UPDATE_INFO = 112;

    ViewPager viewPagerTrangChu;
    RadioButton rd_odau, rd_angi;
    RadioGroup group_odau_angi;
    ImageView imgLogo, imgThemQuanAn;
    CircleImageView imgAvatar;
    TextView tvHoTen, tvSDT;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SharedPreferences sharedPreferences;
    ThanhVienModel thanhVienModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trangchu);

        viewPagerTrangChu = findViewById(R.id.viewpager_trangchu);
        rd_odau = findViewById(R.id.rd_odau);
        rd_angi = findViewById(R.id.rd_angi);
        group_odau_angi = findViewById(R.id.group_odau_angi);
        imgLogo = findViewById(R.id.imgLogo);
        imgThemQuanAn = findViewById(R.id.imgThemQuanAn);

        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        // Load thông tin user từ firebase
        LayThongTinUser();

        // Lằng nghe sự kiện chuyển trang trong ViewPager
        viewPagerTrangChu.addOnPageChangeListener(this);
        group_odau_angi.setOnCheckedChangeListener(this);

        // Load lại giao diện khi nhấn vào logo
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // Chuyển qua giao diện thêm quán ăn
        imgThemQuanAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iThemQuanAn = new Intent(TrangChuActivity.this, ThemQuanAnActivity.class);
                startActivity(iThemQuanAn);
            }
        });

        // Set 2 fragment cho ViewPager trang chủ
        AdapterViewPagerTrangChu adapterViewPagerTrangChu = new AdapterViewPagerTrangChu(getSupportFragmentManager());
        viewPagerTrangChu.setAdapter(adapterViewPagerTrangChu);
    }

    private void NavigationView () {
        // Ánh xạ NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);

        View headerView = navigationView.getHeaderView(0);

        imgAvatar = headerView.findViewById(R.id.imgAvatar);
        tvHoTen = headerView.findViewById(R.id.tvHoTen);
        tvSDT = headerView.findViewById(R.id.tvSDT);

        // Vô hiệu hóa màu icon mặc định của NavigationView
        navigationView.setItemIconTintList(null);

        // Lấy thông tin user
        String photoUri = thanhVienModel.getHinhanh();
        String hoten = thanhVienModel.getHoten();
        String sdt = thanhVienModel.getSodienthoai();

        // Đăng nhập bằng số điện thoại mới
        if (hoten.isEmpty() && !sdt.isEmpty()) {
            tvHoTen.setText(getString(R.string.khongcoten));
            tvSDT.setText(sdt);
        }

        // Đăng nhập bằng google hoặc facebook
        else if (!hoten.isEmpty() && sdt.isEmpty()) {
            tvHoTen.setText(hoten);
            tvSDT.setText(getString(R.string.khongcosdt));
        }

        else {
            tvHoTen.setText(hoten);
            tvSDT.setText(sdt);
        }

        setAvatar(imgAvatar, photoUri);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_info:
                        Intent iCapNhatThongTin = new Intent(TrangChuActivity.this, CapNhatThongTinUserActivity.class);
                        iCapNhatThongTin.putExtra("thanhvien", thanhVienModel);
                        startActivityForResult(iCapNhatThongTin, REQUEST_UPDATE_INFO);
                        break;

                    case R.id.mn_history_orders:
                        Intent iLichSuDonHang = new Intent(TrangChuActivity.this, LichSuDonHangActivity.class);
                        iLichSuDonHang.putExtra("thanhvien", thanhVienModel);
                        startActivity(iLichSuDonHang);
                        break;

                    case R.id.mn_language:
                        Intent iNgonNgu = new Intent(TrangChuActivity.this, ThayDoiNgonNguActivity.class);
                        startActivity(iNgonNgu);
                        break;

                    case R.id.mn_log_out:
                        FirebaseAuth.getInstance().signOut();
                        Intent iDangNhap = new Intent(TrangChuActivity.this, DangNhapActivity.class);
                        startActivity(iDangNhap);
                        break;
                }
                return false;
            }
        });
    }

    private void setAvatar(ImageView imgAvatar, String linkhinh) {
        StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().
                child("thanhvien").child(linkhinh);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgAvatar.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rd_odau.setChecked(true);
                break;
            case 1:
                rd_angi.setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rd_angi:
                viewPagerTrangChu.setCurrentItem(1);
                Toast.makeText(this, getString(R.string.thongbaoangi), Toast.LENGTH_SHORT).show();
                break;
            case R.id.rd_odau:
                viewPagerTrangChu.setCurrentItem(0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE_INFO) {
            if (resultCode == RESULT_OK) {

                // Reload lại trang chủ khi update info user thành công
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    private void LayThongTinUser () {
        String uid = sharedPreferences.getString("mauser", "");
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");
                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    if (uid.compareTo(valueThanhVien.getKey()) == 0) {
                        thanhVienModel = valueThanhVien.getValue(ThanhVienModel.class);
                        NavigationView();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
