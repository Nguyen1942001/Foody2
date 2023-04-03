package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Adapters.AdapterDatMon;
import com.khoinguyen.foody2.Adapters.AdapterMonAn;
import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatMonActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editDiaChiGiaoHang, editSDTNhanHang;
    TextView txtTongSoLuongSanPham, txtTongTienTruoc, txtTongTienSau, txtTieuDeToolbar,
            txtTongTienMat, txtTongTienOnline;
    RecyclerView recyclerMonAnDuocDat;
    Toolbar toolbar;
    LinearLayout linearOnline, linearCash;
    Button btnThanhToan;
    List<DatMonModel> datMonModelList = AdapterMonAn.datMonModelList;
    SharedPreferences sharedPreferences;
    ThanhVienModel thanhVienModel;
    QuanAnModel quanAnModel;  // Bên ChiTietQuanAnActivity gửi qua
    String diachiquanan;  // Địa chỉ quán ăn mà khách chọn để đặt đơn hàng

    int phuongthucthanhtoan = 0; // thanh toán bằng tiền mặt
    int tongSLSanPham = 0;
    long tongGiaTriSanPham = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_datmon);

        editDiaChiGiaoHang = findViewById(R.id.editDiaChiGiaoHang);
        editSDTNhanHang = findViewById(R.id.editSDTNhanHang);
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

        thanhVienModel = new ThanhVienModel();

        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        quanAnModel = getIntent().getParcelableExtra("quanan");
        diachiquanan = getIntent().getStringExtra("diachiquanan");

        LayThongTinUser();

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

        String tenquan = quanAnModel.getTenquanan();
        txtTieuDeToolbar.setText(tenquan);


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

    private void LuuThongTinDonHang () {
        DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        String mauser = sharedPreferences.getString("mauser", "");
        String diachi = editDiaChiGiaoHang.getText().toString();
        String sodienthoai = editSDTNhanHang.getText().toString();
        long tongtien = tongGiaTriSanPham;
        List<DatMonModel> danhsachmonan = new ArrayList<>();

        if (datMonModelList.size() == 0) {
            Toast.makeText(this, getString(R.string.thongbaochonmon), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (diachi.length() == 0 || sodienthoai.length() == 0) {
            Toast.makeText(this, getString(R.string.thongbaoloidatmon), Toast.LENGTH_SHORT).show();
            return;
        }

        // ********************   Khởi tạo thông tin đơn hàng  ********************
        DonHangModel donHangModel = new DonHangModel();

        // Thông tin quán ăn của đơn hàng được mua
        donHangModel.setMaquanan(quanAnModel.getMaquanan());
        donHangModel.setTenquanan(quanAnModel.getTenquanan());
        donHangModel.setLinkhinhquanan(quanAnModel.getHinhanhquanan().get(0));
        donHangModel.setDiachiquanan(diachiquanan);

        // Thông tin khách hàng
        donHangModel.setMauser(mauser);
        donHangModel.setDiachi(diachi);
        donHangModel.setSodienthoai(sodienthoai);
        donHangModel.setPhuongthucthanhtoan(phuongthucthanhtoan);
        donHangModel.setTongtien(tongtien);
        donHangModel.setDanhsachmonan(datMonModelList);
        donHangModel.setNgaydathang(currentDate);

        // ********************     ********************


        // Lưu thông tin đơn hàng vào firebase
        DatabaseReference nodeDonHang = nodeRoot.child("donhangs");
        String madonhang = nodeDonHang.push().getKey();
        donHangModel.setMadonhang(madonhang);
        nodeDonHang.child(madonhang).setValue(donHangModel);

        // Lưu mã đơn hàng vào node thanhviens
        if (thanhVienModel.getDanhsachdonhang().size() > 0) {
            thanhVienModel.getDanhsachdonhang().add(madonhang);
            thanhVienModel.setDanhsachdonhang(thanhVienModel.getDanhsachdonhang());
        }
        else {
            thanhVienModel.getDanhsachdonhang().add(madonhang);
        }
        nodeRoot.child("thanhviens").child(mauser).setValue(thanhVienModel);

        // Clear dữ liệu
        datMonModelList.clear();
        Toast.makeText(this, getString(R.string.thongbaodatmonthanhcong) + " " + diachi, Toast.LENGTH_LONG).show();
        finish();
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

                phuongthucthanhtoan = 0;
                break;

            case R.id.linearOnline:
                linearOnline.setBackgroundResource(R.drawable.custom_payment_check);
                txtTongTienOnline.setTextColor(getColor(R.color.colorFacebook));

                linearCash.setBackgroundResource(R.drawable.custom_payment_not_check);
                txtTongTienMat.setTextColor(Color.parseColor("#bebebe"));

                phuongthucthanhtoan = 1;
                break;

            case R.id.btnThanhToan:
                LuuThongTinDonHang();
                break;
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
