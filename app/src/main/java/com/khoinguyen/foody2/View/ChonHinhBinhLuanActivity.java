package com.khoinguyen.foody2.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Adapters.AdapterChonHinhBinhLuan;
import com.khoinguyen.foody2.Model.ChonHinhBinhLuanModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

public class ChonHinhBinhLuanActivity extends AppCompatActivity implements View.OnClickListener {

    List<ChonHinhBinhLuanModel> listDuongDan;
    List<String> listHinhDuocChon;
    RecyclerView recyclerChonHinhBinhLuan;
    AdapterChonHinhBinhLuan adapterChonHinhBinhLuan;
    TextView txtXong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chonhinh_binhluan);

        txtXong = findViewById(R.id.txtXong);

        listDuongDan = new ArrayList<>();
        listHinhDuocChon = new ArrayList<>();

        recyclerChonHinhBinhLuan = findViewById(R.id.recyclerChonHinhBinhLuan);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        adapterChonHinhBinhLuan = new AdapterChonHinhBinhLuan
                (this, R.layout.custom_layout_chonhinhbinhluan, listDuongDan);
        recyclerChonHinhBinhLuan.setLayoutManager(layoutManager);
        recyclerChonHinhBinhLuan.setAdapter(adapterChonHinhBinhLuan);

        int checkReadExternalStorage = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            getAllHinhAnhTrongTheNho();
        }

        txtXong.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllHinhAnhTrongTheNho();
            }
        }
    }

    // Lấy toàn bộ hình trong thẻ nhớ
    public void getAllHinhAnhTrongTheNho () {
        String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            @SuppressLint("Range") String duongdan = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            ChonHinhBinhLuanModel chonHinhBinhLuanModel = new ChonHinhBinhLuanModel(duongdan, false);
            listDuongDan.add(chonHinhBinhLuanModel);
            adapterChonHinhBinhLuan.notifyDataSetChanged();
            cursor.moveToNext();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtXong:
                for (ChonHinhBinhLuanModel value : listDuongDan) {
                    if (value.isCheck()) {
                        listHinhDuocChon.add(value.getDuongdan());
                    }
                }

                Intent data = getIntent();
                data.putStringArrayListExtra("listHinhDuocChon", (ArrayList<String>) listHinhDuocChon);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }
}
