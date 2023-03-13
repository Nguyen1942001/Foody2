package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Adapters.AdapterDatMon;
import com.khoinguyen.foody2.Adapters.AdapterMonAn;
import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DatMonActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editDiaChiGiaoHang;
    TextView txtTongSoLuongSanPham, txtTongTienTruoc, txtTongTienSau, txtTieuDeToolbar,
            txtTongTienMat, txtTongTienOnline;
    RecyclerView recyclerMonAnDuocDat;
    Toolbar toolbar;
    LinearLayout linearOnline, linearCash;
    Button btnThanhToan;
    List<DatMonModel> datMonModelList = AdapterMonAn.datMonModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_datmon);

        editDiaChiGiaoHang = findViewById(R.id.editDiaChiGiaoHang);
        txtTongSoLuongSanPham = findViewById(R.id.txtTongSoLuongSanPham);
        txtTongTienTruoc = findViewById(R.id.txtTongTienTruoc);
        txtTongTienSau = findViewById(R.id.txtTongTienSau);
        recyclerMonAnDuocDat = findViewById(R.id.recyclerMonAnDuocDat);
        toolbar = findViewById(R.id.toolbar);
        txtTieuDeToolbar = findViewById(R.id.txtTieuDeToolbar);
        linearOnline = findViewById(R.id.linearOnline);
        linearCash = findViewById(R.id.linearCash);
        txtTongTienMat = findViewById(R.id.txtTongTienMat);
        txtTongTienOnline = findViewById(R.id.txtTongTienOnline);
        btnThanhToan = findViewById(R.id.btnThanhToan);

        linearCash.setOnClickListener(this);
        linearOnline.setOnClickListener(this);
        btnThanhToan.setOnClickListener(this);

        // Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LayThongTinMonAnDuocDat();

        LayThongTinDonHang();

    }

    private void LayThongTinMonAnDuocDat () {
        recyclerMonAnDuocDat.setLayoutManager(new LinearLayoutManager(this));
        AdapterDatMon adapterDatMon = new AdapterDatMon(this, R.layout.layout_datmon, datMonModelList);
        recyclerMonAnDuocDat.setAdapter(adapterDatMon);
        adapterDatMon.notifyDataSetChanged();
    }

    private void LayThongTinDonHang () {
        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        String tenquan = getIntent().getStringExtra("tenquan");
        txtTieuDeToolbar.setText(tenquan);

        int tongSLSanPham = 0;
        long tongGiaTriSanPham = 0;
        for (DatMonModel datMonModel : datMonModelList) {
            tongSLSanPham = tongSLSanPham + datMonModel.getSoluong();
            tongGiaTriSanPham = tongGiaTriSanPham + (datMonModel.getSoluong() * datMonModel.getGiatien());
        }

        txtTongSoLuongSanPham.setText(tongSLSanPham + "");
        txtTongTienTruoc.setText(numberFormat.format(tongGiaTriSanPham));
        txtTongTienSau.setText(numberFormat.format((tongGiaTriSanPham + 3000)));

        txtTongTienMat.setText(numberFormat.format((tongGiaTriSanPham + 3000)));
        txtTongTienOnline.setText(numberFormat.format((tongGiaTriSanPham + 3000)));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearCash:
                linearCash.setBackgroundResource(R.drawable.custom_payment_check);
                txtTongTienMat.setTextColor(getColor(R.color.colorFacebook));

                linearOnline.setBackgroundResource(R.drawable.custom_payment_not_check);
                txtTongTienOnline.setTextColor(Color.parseColor("#bebebe"));
                break;

            case R.id.linearOnline:
                linearOnline.setBackgroundResource(R.drawable.custom_payment_check);
                txtTongTienOnline.setTextColor(getColor(R.color.colorFacebook));

                linearCash.setBackgroundResource(R.drawable.custom_payment_not_check);
                txtTongTienMat.setTextColor(Color.parseColor("#bebebe"));
                break;

            case R.id.btnThanhToan:
                if (datMonModelList.size() == 0) {
                    Toast.makeText(this, getString(R.string.thongbaochonmon), Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    String diachi = editDiaChiGiaoHang.getText().toString();
                    if (diachi.length() != 0) {
                        datMonModelList.clear();
                        Toast.makeText(this, getString(R.string.thongbaodatmonthanhcong) + " " + diachi, Toast.LENGTH_LONG).show();
                        Intent iTrangChu = new Intent(this, TrangChuActivity.class);
                        startActivity(iTrangChu);
                    }
                    else {
                        Toast.makeText(this, getString(R.string.thongbaoloidatmon), Toast.LENGTH_SHORT).show();
                    }

                }
        }

    }
}
