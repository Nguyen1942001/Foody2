package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khoinguyen.foody2.Adapters.AdapterHienThiHinhBinhLuanDuocChon;
import com.khoinguyen.foody2.Controller.DangKyController;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CapNhatThongTinUserActivity extends AppCompatActivity {
    private static final int REQUEST_CHON_AVATAR = 111;


    EditText edEmail, edHoTen, edSDT, edDiaChiGiaoHang;
    Button btnCapNhatThongTin;
    ImageView imgAvatar;
    List<String> listHinhDuocChon;
    SharedPreferences sharedPreferences;
    ThanhVienModel thanhVienModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thongtinuser);

        edEmail = findViewById(R.id.edEmail);
        edHoTen = findViewById(R.id.edHoTen);
        edSDT = findViewById(R.id.edSDT);
        edDiaChiGiaoHang = findViewById(R.id.edDiaChiGiaoHang);
        btnCapNhatThongTin = findViewById(R.id.btnCapNhatThongTin);
        imgAvatar = findViewById(R.id.imgAvatar);

        thanhVienModel = getIntent().getParcelableExtra("thanhvien");

        listHinhDuocChon = new ArrayList<>();

        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iChonAvatar = new Intent(CapNhatThongTinUserActivity.this, ChonHinhBinhLuanActivity.class);
                startActivityForResult(iChonAvatar, REQUEST_CHON_AVATAR);
            }
        });

        btnCapNhatThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInfo();
            }
        });

        SetDefaultInfo();
    }

    private void SetDefaultInfo() {
        edEmail.setText(thanhVienModel.getEmail());
        edHoTen.setText(thanhVienModel.getHoten());
        edSDT.setText(thanhVienModel.getSodienthoai());
        edDiaChiGiaoHang.setText(thanhVienModel.getDiachi());
        setAvatar(imgAvatar, thanhVienModel.getHinhanh());
    }

    private void UpdateInfo() {
        String userID = sharedPreferences.getString("mauser", "");
        String hoten = edHoTen.getText().toString();
        String diachigiaohang = edDiaChiGiaoHang.getText().toString();

        if (hoten.length() == 0 && diachigiaohang.length() == 0) {
            Toast.makeText(this, getString(R.string.khongnhapduthongtin), Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu thay đổi avatar thì mới upload lên hình mới
        if (listHinhDuocChon.size() > 0) {
            Uri avatarURI = Uri.fromFile(new File(listHinhDuocChon.get(listHinhDuocChon.size() - 1)));

            // Upload hình lên firebase
            StorageReference storageReference = FirebaseStorage.getInstance().
                    getReference().child("thanhvien/" + avatarURI.getLastPathSegment());
            storageReference.putFile(avatarURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        UpdateInfoToFirebase(userID, hoten, diachigiaohang, avatarURI);
                    }
                }
            });
        }
        else {
            UpdateInfoToFirebase(userID, hoten, diachigiaohang, null);
        }
    }

    private void UpdateInfoToFirebase (String userID, String hoten, String diachigiaohang, Uri avatarURI) {
        UserProfileChangeRequest userProfileChangeRequest;

        if (avatarURI != null) {
            Uri photoUri = Uri.parse(avatarURI.getLastPathSegment());
            userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(thanhVienModel.getHoten())
                    .setPhotoUri(photoUri)
                    .build();
            thanhVienModel.setHinhanh(avatarURI.getLastPathSegment());
        }
        else {
            userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(thanhVienModel.getHoten())
                    .build();
        }

        thanhVienModel.setHoten(hoten);
        thanhVienModel.setDiachi(diachigiaohang);
        FirebaseDatabase.getInstance().getReference().child("thanhviens").child(userID).setValue(thanhVienModel);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CapNhatThongTinUserActivity.this, getString(R.string.capnhatthanhcong), Toast.LENGTH_LONG).show();

                Intent data = getIntent();
                setResult(RESULT_OK, data);
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHON_AVATAR) {
            if (resultCode == RESULT_OK) {
                listHinhDuocChon = data.getStringArrayListExtra("listHinhDuocChon");
                if (listHinhDuocChon.size() > 0) {
                    Uri avatarURI = Uri.parse(listHinhDuocChon.get(0));
                    imgAvatar.setImageURI(avatarURI);
                }
            }
        }

    }

}
