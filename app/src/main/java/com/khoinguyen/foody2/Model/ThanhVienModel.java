package com.khoinguyen.foody2.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ThanhVienModel implements Parcelable {

    // Dùng private để ngăn chặn firebase quét thuộc tính trong quá trình update dữ liệu, những thuộc tính
    // nào ko có private sẽ được firebase quét và update
    private DatabaseReference dataNodeThanhVien;
    String hinhanh;
    String hoten;
    String sodienthoai;
    String diachi;
    String email;
    String ngaytao;
    List<String> danhsachdonhang;

    public ThanhVienModel() {
        dataNodeThanhVien = FirebaseDatabase.getInstance().getReference().child("thanhviens");
        danhsachdonhang = new ArrayList<>();
    }

    protected ThanhVienModel(Parcel in) {
        hinhanh = in.readString();
        hoten = in.readString();
        email = in.readString();
        sodienthoai = in.readString();
        diachi = in.readString();
        ngaytao = in.readString();
        danhsachdonhang = in.createStringArrayList();
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

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }

    public List<String> getDanhsachdonhang() {
        return danhsachdonhang;
    }

    public void setDanhsachdonhang(List<String> danhsachdonhang) {
        this.danhsachdonhang = danhsachdonhang;
    }

    @Override
    public String toString() {
        return "ThanhVienModel{" +
                "hinhanh='" + hinhanh + '\'' +
                ", hoten='" + hoten + '\'' +
                ", sodienthoai='" + sodienthoai + '\'' +
                ", diachi='" + diachi + '\'' +
                ", email='" + email + '\'' +
                ", ngaytao='" + ngaytao + '\'' +
                ", danhsachdonhang=" + danhsachdonhang +
                '}';
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
        parcel.writeString(email);
        parcel.writeString(sodienthoai);
        parcel.writeString(diachi);
        parcel.writeString(ngaytao);
        parcel.writeStringList(danhsachdonhang);
    }
}
