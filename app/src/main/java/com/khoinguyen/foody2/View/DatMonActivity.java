package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.khoinguyen.foody2.Model.KhuyenMaiModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
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

public class DatMonActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editDiaChiGiaoHang, editSDTNhanHang;
    TextView txtTongSoLuongSanPham, txtTongTienTruoc, txtTongTienSau, txtTieuDeToolbar,
            txtTongTienMat, txtTongTienOnline;
    Spinner spinnerKhuyenMai;
    RecyclerView recyclerMonAnDuocDat;
    Toolbar toolbar;
    LinearLayout linearOnline, linearCash;
    Button btnThanhToan;
    List<DatMonModel> datMonModelList = AdapterMonAn.datMonModelList;
    SharedPreferences sharedPreferences;
    ThanhVienModel thanhVienModel;
    QuanAnModel quanAnModel;  // Bên ChiTietQuanAnActivity gửi qua
    String diachiquanan;  // Địa chỉ quán ăn mà khách chọn để đặt đơn hàng
    List<KhuyenMaiModel> listKhuyenMai;

    int phuongthucthanhtoan = 0; // thanh toán bằng tiền mặt
    int tongSLSanPham = 0;
    long tongGiaTriSanPham = 0;
    long tongGiaSauKhuyenMai = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_datmon);

        editDiaChiGiaoHang = findViewById(R.id.editDiaChiGiaoHang);
        editSDTNhanHang = findViewById(R.id.editSDTNhanHang);
        txtTongSoLuongSanPham = findViewById(R.id.txtTongSoLuongSanPham);
        txtTongTienTruoc = findViewById(R.id.txtTongTienTruoc);
        txtTongTienSau = findViewById(R.id.txtTongTienSau);
        spinnerKhuyenMai = findViewById(R.id.spinnerKhuyenMai);
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
        listKhuyenMai = new ArrayList<>();

        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        quanAnModel = getIntent().getParcelableExtra("quanan");
        diachiquanan = getIntent().getStringExtra("diachiquanan");

        LayThongTinUser();
        LayDanhSachKhuyenMai();

        // Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LayThongTinDonHang();
        LayThongTinMonAnDuocDat();

        spinnerKhuyenMai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Định dạng tiền Việt Nam
            final Locale locale = new Locale("vi", "VN");
            final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tongTienTruocCleaned = txtTongTienTruoc.getText().toString().replaceAll("[^\\d.]", "");
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                try {
                    // Chuyển đổi chuỗi thành số sử dụng DecimalFormat
                    Number tongTienTruocNumber = decimalFormat.parse(tongTienTruocCleaned);

                    // Lấy giá trị kiểu long từ số đã chuyển đổi
                    tongGiaTriSanPham = tongTienTruocNumber.longValue();
                } catch (ParseException e) {
                    // Xử lý ngoại lệ nếu có lỗi khi chuyển đổi
                    e.printStackTrace();
                }

                if (i != 0) {
                    int giamgia = listKhuyenMai.get(i - 1).getGiamGia();

                    if (giamgia < 100) {
                        tongGiaSauKhuyenMai = tongGiaTriSanPham - (tongGiaTriSanPham * giamgia / 100) + 3000;
                        txtTongTienSau.setText(numberFormat.format(tongGiaSauKhuyenMai));

                        txtTongTienMat.setText(numberFormat.format(tongGiaSauKhuyenMai));
                        txtTongTienOnline.setText(numberFormat.format(tongGiaSauKhuyenMai));
                    } else {
                        tongGiaSauKhuyenMai = tongGiaTriSanPham - giamgia + 3000;
                        txtTongTienSau.setText(numberFormat.format(tongGiaSauKhuyenMai));

                        txtTongTienMat.setText(numberFormat.format(tongGiaSauKhuyenMai));
                        txtTongTienOnline.setText(numberFormat.format(tongGiaSauKhuyenMai));
                    }
                } else {
                    txtTongTienSau.setText(numberFormat.format((tongGiaTriSanPham + 3000)));
                    tongGiaSauKhuyenMai = 0;

                    txtTongTienMat.setText(numberFormat.format((tongGiaTriSanPham + 3000)));
                    txtTongTienOnline.setText(numberFormat.format((tongGiaTriSanPham + 3000)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void LayThongTinMonAnDuocDat() {
        recyclerMonAnDuocDat.setLayoutManager(new LinearLayoutManager(this));
        AdapterDatMon adapterDatMon = new AdapterDatMon(this, R.layout.layout_datmon, datMonModelList, tongGiaTriSanPham);
        recyclerMonAnDuocDat.setAdapter(adapterDatMon);
        adapterDatMon.notifyDataSetChanged();
    }

    private void LayThongTinDonHang() {
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

    private void LuuThongTinDonHang() {
        DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();

        String tongTienTruocCleaned = txtTongTienTruoc.getText().toString().replaceAll("[^\\d.]", "");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        try {
            // Chuyển đổi chuỗi thành số sử dụng DecimalFormat
            Number tongTienTruocNumber = decimalFormat.parse(tongTienTruocCleaned);

            // Lấy giá trị kiểu long từ số đã chuyển đổi
            tongGiaTriSanPham = tongTienTruocNumber.longValue();
        } catch (ParseException e) {
            // Xử lý ngoại lệ nếu có lỗi khi chuyển đổi
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        String mauser = sharedPreferences.getString("mauser", "");
        String diachi = editDiaChiGiaoHang.getText().toString();
        String sodienthoai = editSDTNhanHang.getText().toString();
        long tongtien = tongGiaSauKhuyenMai == 0 ? tongGiaTriSanPham : tongGiaSauKhuyenMai;

        if (datMonModelList.size() == 0) {
            Toast.makeText(this, getString(R.string.thongbaochonmon), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (diachi.length() == 0 || sodienthoai.length() == 0 || sodienthoai.length() < 10) {
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
        } else {
            thanhVienModel.getDanhsachdonhang().add(madonhang);
        }
        nodeRoot.child("thanhviens").child(mauser).setValue(thanhVienModel);


        // Cập nhật lại số lượt sử dụng mã khuyến mãi (Trừ 1 lượt sử dụng)
        if (spinnerKhuyenMai.getSelectedItemPosition() > 0) {
            KhuyenMaiModel khuyenMaiModel = listKhuyenMai.get(spinnerKhuyenMai.getSelectedItemPosition() - 1);
            khuyenMaiModel.setLuotsudung(khuyenMaiModel.getLuotsudung() - 1);
            nodeRoot.child("khuyenmais").child(khuyenMaiModel.getMakhuyenmai()).setValue(khuyenMaiModel);
        }

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

    private void LayThongTinUser() {
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

    private void LayDanhSachKhuyenMai() {
        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataKhuyenMai = dataSnapshot.child("khuyenmais");
                for (DataSnapshot valueKhuyenMai : dataKhuyenMai.getChildren()) {
                    KhuyenMaiModel khuyenMaiModel = valueKhuyenMai.getValue(KhuyenMaiModel.class);
                    khuyenMaiModel.setMakhuyenmai(valueKhuyenMai.getKey());

                    if (KiemTraThoiHanKhuyenMai(khuyenMaiModel.getNgayketthuc()) && khuyenMaiModel.getLuotsudung() > 0) {
                        listKhuyenMai.add(khuyenMaiModel);
                    }
                }

                List<String> tenkhuyenmai = new ArrayList<>();
                tenkhuyenmai.add("Không");
                for (KhuyenMaiModel khuyenMaiModel : listKhuyenMai) {
                    int giamgia = khuyenMaiModel.getGiamGia();
                    if (giamgia < 100) {
                        tenkhuyenmai.add("Giảm " + giamgia + "%");
                    } else {
                        tenkhuyenmai.add("Giảm " + numberFormat.format(giamgia));
                    }
                }

                ArrayAdapter<String> adapterKhuyenMai = new ArrayAdapter<String>(DatMonActivity.this, android.R.layout.simple_list_item_1, tenkhuyenmai);
                spinnerKhuyenMai.setAdapter(adapterKhuyenMai);
                adapterKhuyenMai.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean KiemTraThoiHanKhuyenMai(String ngayketthuc) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = format.parse(ngayketthuc);
            Date today = new Date();

            // Còn hạn khuyến mãi
            if (date.equals(today) || date.after(today)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }
}
