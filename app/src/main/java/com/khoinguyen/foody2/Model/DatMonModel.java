package com.khoinguyen.foody2.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DatMonModel implements Parcelable {
    String tenmonan;
    int soluong;
    long giatien;

    public DatMonModel () {}

    protected DatMonModel(Parcel in) {
        tenmonan = in.readString();
        soluong = in.readInt();
        giatien = in.readLong();
    }

    public static final Creator<DatMonModel> CREATOR = new Creator<DatMonModel>() {
        @Override
        public DatMonModel createFromParcel(Parcel in) {
            return new DatMonModel(in);
        }

        @Override
        public DatMonModel[] newArray(int size) {
            return new DatMonModel[size];
        }
    };

    public String getTenmonan() {
        return tenmonan;
    }

    public void setTenmonan(String tenmonan) {
        this.tenmonan = tenmonan;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public long getGiatien() {
        return giatien;
    }

    public void setGiatien(long giatien) {
        this.giatien = giatien;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(tenmonan);
        parcel.writeInt(soluong);
        parcel.writeLong(giatien);
    }
}
