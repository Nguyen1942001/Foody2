package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.khoinguyen.foody2.Adapters.AdapterViewPagerTrangChu;
import com.khoinguyen.foody2.R;

public class TrangChuActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener {

    ViewPager viewPagerTrangChu;
    RadioButton rd_odau, rd_angi;
    RadioGroup group_odau_angi;
    ImageView imgLogo, imgThemQuanAn;

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
}
