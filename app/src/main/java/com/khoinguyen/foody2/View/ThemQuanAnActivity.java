package com.khoinguyen.foody2.View;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.khoinguyen.foody2.Model.ChiNhanhQuanAnModel;
import com.khoinguyen.foody2.Model.MonAnModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThemThucDonModel;
import com.khoinguyen.foody2.Model.ThucDonModel;
import com.khoinguyen.foody2.Model.TienIchModel;
import com.khoinguyen.foody2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ThemQuanAnActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final int REQUEST_IMAGE1 = 111;
    private final int REQUEST_IMAGE2 = 112;
    private final int REQUEST_IMAGE3 = 113;
    private final int REQUEST_IMAGE4 = 114;
    private final int REQUEST_IMAGE5 = 115;
    private final int REQUEST_IMAGE6 = 116;
    private final int REQUEST_VIDEO_TRAILER = 2;

    private final int REQUEST_IMG_MONAN = 1;


    Button btnGioMoCua, btnGioDongCua, btnThemQuanAn;
    Spinner spinnerKhuVuc;
    LinearLayout khungTienIch, khungChuaChiNhanh, khungChuaThucDon;
    ImageView imgTam, imgHinhQuan1, imgHinhQuan2, imgHinhQuan3, imgHinhQuan4, imgHinhQuan5,
            imgHinhQuan6, imgVideoTrailer;
    VideoView videoView;
    RadioGroup rdgTrangThai;
    EditText edTenQuan, edGiaToiDa, edGiaToiThieu;


    List<ThucDonModel> thucDonModelList;
    List<String> khuVucList, thucDonList;
    List<String> selectedTienIchList;
    List<String> chinhanhList;
    List<ThemThucDonModel> themThucDonModelList;
    List<Bitmap> hinhDaChup;
    List<Uri> hinhquanan;


    String giomocua = "", giodongcua = "", khuvuc, maquanan;
    Uri videoSelected;
    ArrayAdapter<String> adapterKhuVuc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_themquanan);

        btnGioMoCua = findViewById(R.id.btnGioMoCua);
        btnGioDongCua = findViewById(R.id.btnGioDongCua);
        spinnerKhuVuc = findViewById(R.id.spinnerKhuVuc);
        khungTienIch = findViewById(R.id.khungTienIch);
        khungChuaChiNhanh = findViewById(R.id.khungChuaChiNhanh);
        khungChuaThucDon = findViewById(R.id.khungChuaThucDon);
        imgHinhQuan1 = findViewById(R.id.imgHinhQuan1);
        imgHinhQuan2 = findViewById(R.id.imgHinhQuan2);
        imgHinhQuan3 = findViewById(R.id.imgHinhQuan3);
        imgHinhQuan4 = findViewById(R.id.imgHinhQuan4);
        imgHinhQuan5 = findViewById(R.id.imgHinhQuan5);
        imgHinhQuan6 = findViewById(R.id.imgHinhQuan6);
        imgVideoTrailer = findViewById(R.id.imgVideoTrailer);
        videoView = findViewById(R.id.videoView);
        btnThemQuanAn = findViewById(R.id.btnThemQuanAn);
        rdgTrangThai = findViewById(R.id.rdgTrangThai);
        edTenQuan = findViewById(R.id.edTenQuanAn);
        edGiaToiDa = findViewById(R.id.edGiaToiDa);
        edGiaToiThieu = findViewById(R.id.edGiaToiThieu);

        btnGioMoCua.setOnClickListener(this);
        btnGioDongCua.setOnClickListener(this);
        imgHinhQuan1.setOnClickListener(this);
        imgHinhQuan2.setOnClickListener(this);
        imgHinhQuan3.setOnClickListener(this);
        imgHinhQuan4.setOnClickListener(this);
        imgHinhQuan5.setOnClickListener(this);
        imgHinhQuan6.setOnClickListener(this);
        imgVideoTrailer.setOnClickListener(this);
        btnThemQuanAn.setOnClickListener(this);

        thucDonModelList = new ArrayList<>();
        khuVucList = new ArrayList<>();
        thucDonList = new ArrayList<>();
        selectedTienIchList = new ArrayList<>();
        chinhanhList = new ArrayList<>();
        themThucDonModelList = new ArrayList<>();
        hinhDaChup = new ArrayList<>();
        hinhquanan = new ArrayList<>();

        // Thêm chi nhánh
        CloneChiNhanh();

        // Thêm thực đơn (món ăn)
        CloneThucDon();

        // Load khu vực
        adapterKhuVuc = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, khuVucList);
        spinnerKhuVuc.setAdapter(adapterKhuVuc);
        adapterKhuVuc.notifyDataSetChanged();
        LayDanhSachKhuVuc();

        spinnerKhuVuc.setOnItemSelectedListener(this);

        // Load danh sách tiện ích
        LayDanhSachTienIch();

    }

    private void LayDanhSachKhuVuc () {
        FirebaseDatabase.getInstance().getReference().child("khuvucs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tenkhuvuc = snapshot.getKey();
                    khuVucList.add(tenkhuvuc);
                }

                adapterKhuVuc.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LayDanhSachThucDon (ArrayAdapter<String> adapterThucDon) {
        FirebaseDatabase.getInstance().getReference().child("thucdons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String value = snapshot.getValue(String.class);

                    ThucDonModel thucDonModel = new ThucDonModel();
                    thucDonModel.setMathucdon(key);
                    thucDonModel.setTenthucdon(value);

                    thucDonModelList.add(thucDonModel);
                    thucDonList.add(value);

                }

                adapterThucDon.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LayDanhSachTienIch () {
        FirebaseDatabase.getInstance().getReference().child("quanlytienichs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String matienich = snapshot.getKey();
                    TienIchModel tienIchModel = snapshot.getValue(TienIchModel.class);
                    tienIchModel.setMatienich(matienich);

                    // Tạo 1 checkbox mỗi khi load được 1 tiện ích
                    CheckBox checkBox = new CheckBox(ThemQuanAnActivity.this);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorCheckBoxAndRadioButton));
                    checkBox.setButtonTintList(colorStateList);

                    checkBox.setText(tienIchModel.getTentienich());
                    checkBox.setTag(matienich);

                    // Lắng nghe sự kiện click chọn vào tiện ích
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String matienichduocchon = buttonView.getTag().toString();

                            if (isChecked) {
                                selectedTienIchList.add(matienichduocchon);
                            }
                            else {
                                selectedTienIchList.remove(matienichduocchon);
                            }
                        }
                    });
                    khungTienIch.addView(checkBox);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CloneChiNhanh () {
        View viewClone = LayoutInflater.from(ThemQuanAnActivity.this).inflate(R.layout.layout_clone_chinhanh, null);
        ImageButton btnThemChiNhanh = viewClone.findViewById(R.id.btnThemChiNhanh);
        ImageButton btnXoaChiNhanh = viewClone.findViewById(R.id.btnXoaChiNhanh);

        btnThemChiNhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edTenChiNhanh = viewClone.findViewById(R.id.edTenChiNhanh);
                String tenchinhanh = edTenChiNhanh.getText().toString();

                // Ẩn dấu +
                v.setVisibility(View.GONE);
                // Hiện dấu -
                btnXoaChiNhanh.setVisibility(View.VISIBLE);
                btnXoaChiNhanh.setTag(tenchinhanh);
                // Vô hiệu hóa EditText
                edTenChiNhanh.setFocusable(false);

                chinhanhList.add(tenchinhanh);

                CloneChiNhanh();
            }
        });

        btnXoaChiNhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenchinhanh = btnXoaChiNhanh.getTag().toString();
                chinhanhList.remove(tenchinhanh);
                khungChuaChiNhanh.removeView(viewClone);
            }
        });

        khungChuaChiNhanh.addView(viewClone);
    }

    private void CloneThucDon () {
        View viewClone = LayoutInflater.from(ThemQuanAnActivity.this).inflate(R.layout.layout_clone_thucdon, null);

        Spinner spinnerThucDon = viewClone.findViewById(R.id.spinnerThucDon);
        Button btnThemThucDon = viewClone.findViewById(R.id.btnThemThucDon);
        EditText edTenMon = viewClone.findViewById(R.id.edTenMon);
        EditText edGiaTien = viewClone.findViewById(R.id.edGiaTien);
        ImageView imgChupHinh = viewClone.findViewById(R.id.imgChupHinh);

        // Gán tạm để dùng ở onActivityResult
        imgTam = imgChupHinh;

        // Load thực đơn cho spinner
        ArrayAdapter<String> adapterThucDon = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, thucDonList);
        spinnerThucDon.setAdapter(adapterThucDon);
        adapterThucDon.notifyDataSetChanged();

        // Nếu thực đơn đã load rồi thì ko load nữa
        if (thucDonList.size() == 0) {
            LayDanhSachThucDon(adapterThucDon);
        }

        imgChupHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMG_MONAN);
            }
        });


        btnThemThucDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenhinh = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpeg";

                int position = spinnerThucDon.getSelectedItemPosition();
                String mathucton = thucDonModelList.get(position).getMathucdon();

                // Nhập tên món và giá tiền thì mới cho thêm món ăn
                if (edTenMon.getText().toString().length() == 0 || edGiaTien.getText().toString().length() == 0) {
                    Toast.makeText(ThemQuanAnActivity.this, getString(R.string.thongbaoloi), Toast.LENGTH_SHORT).show();
                    return;
                }

                String tenmon = edTenMon.getText().toString();
                Long giatien = Long.parseLong(edGiaTien.getText().toString());

                MonAnModel monAnModel = new MonAnModel();
                monAnModel.setTenmon(tenmon);
                monAnModel.setGiatien(giatien);
                monAnModel.setHinhanh(tenhinh);

                ThemThucDonModel themThucDonModel = new ThemThucDonModel();
                themThucDonModel.setMathucdon(mathucton);
                themThucDonModel.setMonAnModel(monAnModel);

                themThucDonModelList.add(themThucDonModel);

                CloneThucDon();

            }
        });

        khungChuaThucDon.addView(viewClone);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_IMG_MONAN:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imgTam.setImageBitmap(bitmap);
                    hinhDaChup.add(bitmap);
                }
                break;

            case REQUEST_IMAGE1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan1.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_IMAGE2:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan2.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_IMAGE3:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan3.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_IMAGE4:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan4.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_IMAGE5:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan5.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_IMAGE6:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgHinhQuan6.setImageURI(uri);
                    hinhquanan.add(uri);
                }
                break;

            case REQUEST_VIDEO_TRAILER:
                if (resultCode == RESULT_OK) {
                    imgVideoTrailer.setVisibility(View.GONE);

                    Uri uri = data.getData();
                    // Lưu vào biến toàn cục để đưa lên firebase
                    videoSelected = uri;
                    videoView.setVideoURI(uri);
                    videoView.start();
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {

        Calendar calendar = Calendar.getInstance();
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);

        switch (view.getId()) {
            case R.id.btnGioMoCua:
                TimePickerDialog moCuaTimePickerDialog = new TimePickerDialog(ThemQuanAnActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        giomocua = hour + ":" + minute;
                        btnGioMoCua.setText(giomocua);
                    }
                }, gio, phut, true);

                moCuaTimePickerDialog.show();
                break;


            case R.id.btnGioDongCua:
                TimePickerDialog dongCuaTimePickerDialog = new TimePickerDialog(ThemQuanAnActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        giodongcua = hour + ":" + minute;
                        btnGioDongCua.setText(giodongcua);
                    }
                }, gio, phut, true);

                dongCuaTimePickerDialog.show();
                break;

            case R.id.imgHinhQuan1:
                ChonHinhTuGallery(REQUEST_IMAGE1);
                break;

            case R.id.imgHinhQuan2:
                ChonHinhTuGallery(REQUEST_IMAGE2);
                break;

            case R.id.imgHinhQuan3:
                ChonHinhTuGallery(REQUEST_IMAGE3);
                break;

            case R.id.imgHinhQuan4:
                ChonHinhTuGallery(REQUEST_IMAGE4);
                break;

            case R.id.imgHinhQuan5:
                ChonHinhTuGallery(REQUEST_IMAGE5);
                break;

            case R.id.imgHinhQuan6:
                ChonHinhTuGallery(REQUEST_IMAGE6);
                break;

            case R.id.imgVideoTrailer:
                Intent intent = new Intent();
                intent.setType("video/*");
                // Mở ứng dụng có hỗ trợ định dạng "video"
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), REQUEST_VIDEO_TRAILER);
                break;

            case R.id.btnThemQuanAn:
                ThemQuanAn();
                finish();
                break;
        }
    }

    private void ThemQuanAn () {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        boolean kiemtra = false;

        // Kiểm tra tên quán ăn, giá tối đa, giá tối thiểu
        if (edTenQuan.getText().toString().length() == 0 || edGiaToiDa.getText().toString().length() == 0
                || edGiaToiThieu.getText().toString().length() == 0) {

            Toast.makeText(this, "Vui lòng nhập thông tin quán ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra giờ mở cửa, giờ đóng cửa
        if (giodongcua.length() == 0 || giomocua.length() == 0) {
            Toast.makeText(this, "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra địa chỉ quán ăn
        if (chinhanhList.size() == 0) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ quán ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra hình đại diện của quán ăn
        if (hinhquanan.size() == 0) {
            Toast.makeText(this, "Vui lòng chọn hình đại diện quán ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra món ăn và hình đại diện món ăn
        if (themThucDonModelList.size() == 0 || hinhDaChup.size() == 0) {
            Toast.makeText(this, "Vui lòng thêm món ăn và hình ảnh món ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tên quán ăn, giá tối đa, giá tối thiểu
        String tenquanan = edTenQuan.getText().toString();
        long giatoida = Long.parseLong(edGiaToiDa.getText().toString());
        long giatoithieu = Long.parseLong(edGiaToiThieu.getText().toString());


        // Trạng thái giao hàng
        boolean giaohang = false;
        int idRadioSelected = rdgTrangThai.getCheckedRadioButtonId();
        if (idRadioSelected == R.id.rdGiaoHang) {
            giaohang = true;
        }

        // Khởi tạo mã quán ăn (firebase tự tạo)
        DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nodeQuanAn = nodeRoot.child("quanans");
        maquanan = nodeQuanAn.push().getKey();

        QuanAnModel quanAnModel = new QuanAnModel();
        quanAnModel.setTenquanan(tenquanan);
        quanAnModel.setGiatoida(giatoida);
        quanAnModel.setGiatoithieu(giatoithieu);
        quanAnModel.setGiomocua(giomocua);
        quanAnModel.setGiodongcua(giodongcua);
        quanAnModel.setGiaohang(giaohang);
        quanAnModel.setNgaytao(currentDate);

        if (videoSelected != null) {
            quanAnModel.setVideogioithieu(videoSelected.getLastPathSegment());
        }
        else {
            quanAnModel.setVideogioithieu("");
        }

        quanAnModel.setTienich(selectedTienIchList);
        quanAnModel.setLuotthich(0);


        // Lưu khu vực
        nodeRoot.child("khuvucs").child(khuvuc).push().setValue(maquanan);


        // Lưu chi nhánh quán ăn
        // Sử dụng google map GeoCoding để tìm tọa độ từ địa chỉ nhập vào
        String urlGeoCoding = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String googleAPIKey = "&key=AIzaSyBscNLN_gr9suCZlvzdSmJQ1_ugYUQrjfY";

        for (String chinhanh : chinhanhList) {
            String toado = urlGeoCoding + chinhanh.replace(" ", "%20") + googleAPIKey;
            DownloadToaDo downloadToaDo = new DownloadToaDo();
            downloadToaDo.execute(toado);
        }


        // Lưu quán ăn mới vòa node "quanans"
        nodeQuanAn.child(maquanan).setValue(quanAnModel);

        if (videoSelected != null) {
            // Lưu video trailer vào firbase storage
            FirebaseStorage.getInstance().getReference().child("video/" +
                    videoSelected.getLastPathSegment()).putFile(videoSelected);
        }


        // Lưu hình ảnh quán ăn vào firbase storage và vào node hinhanhquanans
        for (Uri hinhquan : hinhquanan) {
            FirebaseStorage.getInstance().getReference().child("hinhanh/" +
                    hinhquan.getLastPathSegment()).putFile(hinhquan);

            nodeRoot.child("hinhanhquanans").child(maquanan).push().setValue(hinhquan.getLastPathSegment());
        }


        // Thêm món ăn
        for (int i = 0; i < themThucDonModelList.size(); i++) {
            nodeRoot.child("thucdonquanans").child(maquanan).child(themThucDonModelList.get(i).
                    getMathucdon()).push().setValue(themThucDonModelList.get(i).getMonAnModel());

            // Thêm hình ảnh cho từng món ăn
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = hinhDaChup.get(i);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            FirebaseStorage.getInstance().getReference().child("hinhanh/" +
                    themThucDonModelList.get(i).getMonAnModel().getHinhanh()).putBytes(data);
        }

        kiemtra = true;
        if (kiemtra) {
            Toast.makeText(this, "Thêm quán ăn mới thành công", Toast.LENGTH_LONG).show();
        }

    }


    // Dùng cho google map GEO
    class DownloadToaDo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Dùng để lưu lại dữ liệu đọc được từ google map GEO
            StringBuilder stringBuilder = new StringBuilder();

            try {
                // Mở kết nối tới đường dẫn của google map GEO
                URL url = new URL(strings[0]);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.connect();

                // Đọc dữ liệu từ kết nối trên
                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject object = results.getJSONObject(i);
                    String address = object.getString("formatted_address");
                    JSONObject geometry = object.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");

                    double latitude = (double) location.get("lat");
                    double longitude = (double) location.get("lng");


                    ChiNhanhQuanAnModel chiNhanhQuanAnModel = new ChiNhanhQuanAnModel();
                    chiNhanhQuanAnModel.setDiachi(address);
                    chiNhanhQuanAnModel.setLatitude(latitude);
                    chiNhanhQuanAnModel.setLongitude(longitude);

                    FirebaseDatabase.getInstance().getReference().child("chinhanhquanans").
                            child(maquanan).push().setValue(chiNhanhQuanAnModel);
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Log.d("kiemtra",s);
        }
    }

    private void ChonHinhTuGallery (int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        // Mở ứng dụng có hỗ trợ định dạng "image"
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), requestCode);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.spinnerKhuVuc:
                khuvuc = khuVucList.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
