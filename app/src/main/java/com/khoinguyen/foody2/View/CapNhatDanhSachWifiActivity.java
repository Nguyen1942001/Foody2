package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Controller.CapNhatWifiController;
import com.khoinguyen.foody2.R;

public class CapNhatDanhSachWifiActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerDanhSachWifi;
    Button btnCapNhatWifi;
    ProgressBar progressCapNhatWifi;
    CapNhatWifiController capNhatWifiController;
    String maquanan;
    String maquananPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_capnhatdanhsachwifi);

        recyclerDanhSachWifi = findViewById(R.id.recyclerDanhSachWifi);
        btnCapNhatWifi = findViewById(R.id.btnCapNhatWifi);
        progressCapNhatWifi = findViewById(R.id.progressCapNhatWifi);

        btnCapNhatWifi.setOnClickListener(this);

        // Từ ChiTietQuanAnActivity gửi qua
        maquanan = getIntent().getStringExtra("maquanan");

        maquananPopup = getIntent().getStringExtra("maquananPopup");

        if (maquanan == null) {
            maquanan = maquananPopup;
        }
        else {
            maquananPopup = maquanan;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerDanhSachWifi.setLayoutManager(layoutManager);

        capNhatWifiController = new CapNhatWifiController(this);
        capNhatWifiController.HienThiDanhSachWifi(maquanan, recyclerDanhSachWifi, progressCapNhatWifi);
    }

    @Override
    public void onClick(View view) {
        Intent iPopup = new Intent(this, PopupCapNhatWifiActivity.class);
        iPopup.putExtra("maquanan", maquanan);
        startActivity(iPopup);
    }
}
