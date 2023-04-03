package com.khoinguyen.foody2.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DonHangModel implements Parcelable {
    String madonhang, diachi, sodienthoai, mauser, ngaydathang, maquanan, tenquanan, linkhinhquanan, diachiquanan;
    int phuongthucthanhtoan;
    long tongtien;
    List<DatMonModel> danhsachmonan;

    public DonHangModel () {};

    protected DonHangModel(Parcel in) {
        madonhang = in.readString();
        diachi = in.readString();
        sodienthoai = in.readString();
        mauser = in.readString();
        ngaydathang = in.readString();
        maquanan = in.readString();
        tenquanan = in.readString();
        linkhinhquanan = in.readString();
        diachiquanan = in.readString();
        phuongthucthanhtoan = in.readInt();
        tongtien = in.readLong();

        danhsachmonan = new ArrayList<>();
        in.readTypedList(danhsachmonan, DatMonModel.CREATOR);
    }

    public static final Creator<DonHangModel> CREATOR = new Creator<DonHangModel>() {
        @Override
        public DonHangModel createFromParcel(Parcel in) {
            return new DonHangModel(in);
        }

        @Override
        public DonHangModel[] newArray(int size) {
            return new DonHangModel[size];
        }
    };

    public String getMadonhang() {
        return madonhang;
    }

    public void setMadonhang(String madonhang) {
        this.madonhang = madonhang;
    }

    public String getDiachiquanan() {
        return diachiquanan;
    }

    public void setDiachiquanan(String diachiquanan) {
        this.diachiquanan = diachiquanan;
    }

    public String getTenquanan() {
        return tenquanan;
    }

    public void setTenquanan(String tenquanan) {
        this.tenquanan = tenquanan;
    }

    public String getLinkhinhquanan() {
        return linkhinhquanan;
    }

    public void setLinkhinhquanan(String linkhinhquanan) {
        this.linkhinhquanan = linkhinhquanan;
    }

    public String getMaquanan() {
        return maquanan;
    }

    public void setMaquanan(String maquanan) {
        this.maquanan = maquanan;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getMauser() {
        return mauser;
    }

    public void setMauser(String mauser) {
        this.mauser = mauser;
    }

    public String getNgaydathang() {
        return ngaydathang;
    }

    public void setNgaydathang(String ngaydathang) {
        this.ngaydathang = ngaydathang;
    }

    public int getPhuongthucthanhtoan() {
        return phuongthucthanhtoan;
    }

    public void setPhuongthucthanhtoan(int phuongthucthanhtoan) {
        this.phuongthucthanhtoan = phuongthucthanhtoan;
    }

    public long getTongtien() {
        return tongtien;
    }

    public void setTongtien(long tongtien) {
        this.tongtien = tongtien;
    }

    public List<DatMonModel> getDanhsachmonan() {
        return danhsachmonan;
    }

    public void setDanhsachmonan(List<DatMonModel> danhsachmonan) {
        this.danhsachmonan = danhsachmonan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(madonhang);
        parcel.writeString(diachi);
        parcel.writeString(sodienthoai);
        parcel.writeString(mauser);
        parcel.writeString(ngaydathang);
        parcel.writeString(maquanan);
        parcel.writeString(tenquanan);
        parcel.writeString(linkhinhquanan);
        parcel.writeString(diachiquanan);
        parcel.writeInt(phuongthucthanhtoan);
        parcel.writeLong(tongtien);
        parcel.writeTypedList(danhsachmonan);
    }
}
