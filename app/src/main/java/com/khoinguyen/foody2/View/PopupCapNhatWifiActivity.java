package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.khoinguyen.foody2.Controller.CapNhatWifiController;
import com.khoinguyen.foody2.Model.WifiQuanAnModel;
import com.khoinguyen.foody2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PopupCapNhatWifiActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edTenWifi, edMatKhauWifi;
    Button btnDongY;
    CapNhatWifiController capNhatWifiController;
    String maquanan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_capnhatwifi);

        edTenWifi = findViewById(R.id.edTenWifi);
        edMatKhauWifi = findViewById(R.id.edMatKhauWifi);
        btnDongY = findViewById(R.id.btnDongY);

        btnDongY.setOnClickListener(this);

        maquanan = getIntent().getStringExtra("maquanan");

        capNhatWifiController = new CapNhatWifiController(this);

    }

    @Override
    public void onClick(View view) {
        String tenwifi = edTenWifi.getText().toString();
        String matkhauwifi = edMatKhauWifi.getText().toString();

        if (tenwifi.trim().length() > 0 && matkhauwifi.trim().length() > 0) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            String ngaydang = simpleDateFormat.format(calendar.getTime());

            WifiQuanAnModel wifiQuanAnModel = new WifiQuanAnModel();
            wifiQuanAnModel.setTen(tenwifi);
            wifiQuanAnModel.setMatkhau(matkhauwifi);
            wifiQuanAnModel.setNgaydang(ngaydang);

            capNhatWifiController.ThemWifi(this, wifiQuanAnModel, maquanan);

            // Dùng đẻ cập nhật lại danh sách wifi sau khi thêm mới
            Intent iCapNhatDanhSachWifi = new Intent(this, CapNhatDanhSachWifiActivity.class);
            iCapNhatDanhSachWifi.putExtra("maquananPopup", maquanan);
            startActivity(iCapNhatDanhSachWifi);
            finish();


        } 
        else {
            Toast.makeText(this, getString(R.string.loithemwifi), Toast.LENGTH_SHORT).show();
        }
    }
}
