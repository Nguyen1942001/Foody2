package com.khoinguyen.foody2.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterRecyclerHinhBinhLuan;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HienThiChiTietBinhLuanActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView txtTieuDeBinhLuan, txtNoiDungBinhLuan, txtSoDiem;
    RecyclerView recyclerHinhBinhLuan;
    ProgressBar progressChiTietBinhLuan;

    List<Bitmap> bitmapList;
    BinhLuanModel binhLuanModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_layout_binhluan);

        bitmapList = new ArrayList<>();

        circleImageView = findViewById(R.id.circleImageUser);
        txtTieuDeBinhLuan = findViewById(R.id.txtTieuDeBinhLuan);
        txtNoiDungBinhLuan = findViewById(R.id.txtNoiDungBinhLuan);
        txtSoDiem = findViewById(R.id.txtChamDiemBinhLuan);
        recyclerHinhBinhLuan = findViewById(R.id.recyclerHinhBinhLuan);

        binhLuanModel = getIntent().getParcelableExtra("binhluanmodel");

        txtTieuDeBinhLuan.setText(binhLuanModel.getTieude());
        txtNoiDungBinhLuan.setText(binhLuanModel.getNoidung());
        txtSoDiem.setText(binhLuanModel.getChamdiem() + "");
        setHinhAnhBinhLuan(circleImageView, binhLuanModel.getThanhVienModel().getHinhanh());

        // Download các hình có trong mỗi bình luận
        for (String linkhinh : binhLuanModel.getHinhanhBinhLuanList()) {
            StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().
                    child("hinhanh").child(linkhinh);

            long ONE_MEGABYTE = 1024 * 1024 * 5;
            storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmapList.add(bitmap);

                    // Kiểm tra xem đã download đủ hình ảnh trong từng bình luận hay chưa
                    if (bitmapList.size() == binhLuanModel.getHinhanhBinhLuanList().size()) {

                        // Load hình ảnh trong mỗi bình luận
                        AdapterRecyclerHinhBinhLuan adapterRecyclerHinhBinhLuan = new AdapterRecyclerHinhBinhLuan(HienThiChiTietBinhLuanActivity.this,
                                R.layout.custom_layout_hinhbinhluan, bitmapList, binhLuanModel, true);

                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HienThiChiTietBinhLuanActivity.this, 2);
                        recyclerHinhBinhLuan.setLayoutManager(layoutManager);
                        recyclerHinhBinhLuan.setAdapter(adapterRecyclerHinhBinhLuan);
                        adapterRecyclerHinhBinhLuan.notifyDataSetChanged();
                    }
                }
            });
        }

    }

    private void setHinhAnhBinhLuan(CircleImageView circleImageView, String linkhinh) {
        StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().
                child("thanhvien").child(linkhinh);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImageView.setImageBitmap(bitmap);
            }
        });
    }
}
