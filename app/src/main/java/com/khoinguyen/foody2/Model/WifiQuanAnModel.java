package com.khoinguyen.foody2.Model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Controller.Interfaces.ChiTietQuanAnInterface;
import com.khoinguyen.foody2.R;

public class WifiQuanAnModel {

    private DatabaseReference nodeWifiQuanAn;
    String ten, matkhau, ngaydang;

    public WifiQuanAnModel () {}

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getNgaydang() {
        return ngaydang;
    }

    public void setNgaydang(String ngaydang) {
        this.ngaydang = ngaydang;
    }


    public void LayDanhSachWifiQuanAn (String maquan, ChiTietQuanAnInterface chiTietQuanAnInterface) {
        nodeWifiQuanAn = FirebaseDatabase.getInstance().getReference().child("wifiquanans").child(maquan);

        nodeWifiQuanAn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot valueWifi : dataSnapshot.getChildren()) {
                    WifiQuanAnModel wifiQuanAnModel = valueWifi.getValue(WifiQuanAnModel.class);

                    // Kích interface
                    chiTietQuanAnInterface.HienThiDanhSachWifi(wifiQuanAnModel);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ThemWifiQuanAn (Context context, WifiQuanAnModel wifiQuanAnModel, String maquanan) {
        DatabaseReference dataNodeWifiQuanAn = FirebaseDatabase.getInstance().
                getReference().child("wifiquanans").child(maquanan);
        dataNodeWifiQuanAn.push().setValue(wifiQuanAnModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(context, context.getString(R.string.themthanhcong), Toast.LENGTH_LONG).show();
            }
        });

    }
}
