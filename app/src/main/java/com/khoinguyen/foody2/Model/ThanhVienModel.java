package com.khoinguyen.foody2.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThanhVienModel implements Parcelable {

    // Dùng private để ngăn chặn firebase quét thuộc tính trong quá trình update dữ liệu, những thuộc tính
    // nào ko có private sẽ được firebase quét và update
    private DatabaseReference dataNodeThanhVien;
    String hinhanh;
    String hoten;
    String mathanhvien;
    String sodienthoai;

    public ThanhVienModel() {
        dataNodeThanhVien = FirebaseDatabase.getInstance().getReference().child("thanhviens");
    }

    protected ThanhVienModel(Parcel in) {
        hinhanh = in.readString();
        hoten = in.readString();
        mathanhvien = in.readString();
        sodienthoai = in.readString();
    }

    public static final Creator<ThanhVienModel> CREATOR = new Creator<ThanhVienModel>() {
        @Override
        public ThanhVienModel createFromParcel(Parcel in) {
            return new ThanhVienModel(in);
        }

        @Override
        public ThanhVienModel[] newArray(int size) {
            return new ThanhVienModel[size];
        }
    };

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getMathanhvien() {
        return mathanhvien;
    }

    public void setMathanhvien(String mathanhvien) {
        this.mathanhvien = mathanhvien;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public void ThemThongTinThanhVien(ThanhVienModel thanhVienModel, String uid) {
        dataNodeThanhVien.child(uid).setValue(thanhVienModel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(hinhanh);
        parcel.writeString(hoten);
        parcel.writeString(mathanhvien);
        parcel.writeString(sodienthoai);
    }
}
