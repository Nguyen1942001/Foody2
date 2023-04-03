package com.khoinguyen.foody2.Model;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Controller.Interfaces.OdauInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuanAnModel implements Parcelable {

    DatabaseReference nodeRoot;
    boolean giaohang;
    String maquanan;
    String giodongcua;
    String giomocua;
    String tenquanan;
    String videogioithieu;
    String ngaytao;
    long luotthich, giatoida, giatoithieu;
    List<String> tienich;
    List<String> hinhanhquanan;
    List<BinhLuanModel> binhLuanModelList;
    List<ChiNhanhQuanAnModel> chiNhanhQuanAnModelList;
    List<Bitmap> bitmapList;
    List<ThucDonModel> thucdons;

    protected QuanAnModel(Parcel in) {
        giaohang = in.readByte() != 0;
        maquanan = in.readString();
        giodongcua = in.readString();
        giomocua = in.readString();
        tenquanan = in.readString();
        ngaytao = in.readString();
        videogioithieu = in.readString();
        luotthich = in.readLong();
        giatoida = in.readLong();
        giatoithieu = in.readLong();
        tienich = in.createStringArrayList();
        hinhanhquanan = in.createStringArrayList();
//        bitmapList = in.createTypedArrayList(Bitmap.CREATOR);

        chiNhanhQuanAnModelList = new ArrayList<>();
        in.readTypedList(chiNhanhQuanAnModelList, ChiNhanhQuanAnModel.CREATOR);

        binhLuanModelList = new ArrayList<>();
        in.readTypedList(binhLuanModelList, BinhLuanModel.CREATOR);
    }

    public static final Creator<QuanAnModel> CREATOR = new Creator<QuanAnModel>() {
        @Override
        public QuanAnModel createFromParcel(Parcel in) {
            return new QuanAnModel(in);
        }

        @Override
        public QuanAnModel[] newArray(int size) {
            return new QuanAnModel[size];
        }
    };

    public QuanAnModel () {
        // Lấy dữ liệu của node root (node cha đầu tiên)
        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }

    public String getMaquanan() {
        return maquanan;
    }

    public void setMaquanan(String maquanan) {
        this.maquanan = maquanan;
    }

    public long getLuotthich() {
        return luotthich;
    }

    public void setLuotthich(long luotthich) {
        this.luotthich = luotthich;
    }

    public long getGiatoida() {
        return giatoida;
    }

    public void setGiatoida(long giatoida) {
        this.giatoida = giatoida;
    }

    public long getGiatoithieu() {
        return giatoithieu;
    }

    public void setGiatoithieu(long giatoithieu) {
        this.giatoithieu = giatoithieu;
    }

    public boolean isGiaohang() {
        return giaohang;
    }

    public void setGiaohang(boolean giaohang) {
        this.giaohang = giaohang;
    }

    public String getGiodongcua() {
        return giodongcua;
    }

    public void setGiodongcua(String giodongcua) {
        this.giodongcua = giodongcua;
    }

    public String getGiomocua() {
        return giomocua;
    }

    public void setGiomocua(String giomocua) {
        this.giomocua = giomocua;
    }

    public String getTenquanan() {
        return tenquanan;
    }

    public void setTenquanan(String tenquanan) {
        this.tenquanan = tenquanan;
    }

    public String getVideogioithieu() {
        return videogioithieu;
    }

    public void setVideogioithieu(String videogioithieu) {
        this.videogioithieu = videogioithieu;
    }

    public List<String> getTienich() {
        return tienich;
    }

    public void setTienich(List<String> tienich) {
        this.tienich = tienich;
    }

    public List<String> getHinhanhquanan() {
        return hinhanhquanan;
    }

    public void setHinhanhquanan(List<String> hinhanhquanan) {
        this.hinhanhquanan = hinhanhquanan;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }

    public List<BinhLuanModel> getBinhLuanModelList() {
        return binhLuanModelList;
    }

    public void setBinhLuanModelList(List<BinhLuanModel> binhLuanModelList) {
        this.binhLuanModelList = binhLuanModelList;
    }

    public List<ChiNhanhQuanAnModel> getChiNhanhQuanAnModelList() {
        return chiNhanhQuanAnModelList;
    }

    public void setChiNhanhQuanAnModelList(List<ChiNhanhQuanAnModel> chiNhanhQuanAnModelList) {
        this.chiNhanhQuanAnModelList = chiNhanhQuanAnModelList;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public List<ThucDonModel> getThucdons() {
        return thucdons;
    }

    public void setThucdons(List<ThucDonModel> thucdons) {
        this.thucdons = thucdons;
    }

    private DataSnapshot dataRoot;
    public void getDanhSachQuanAn (OdauInterface odauInterface, Location vitrihientai,
                                   int itemtieptheo, int itemdaco) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataRoot = dataSnapshot;
                LayDanhSachQuanAn(dataSnapshot, odauInterface, vitrihientai, itemtieptheo, itemdaco);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        if (dataRoot != null) {
            LayDanhSachQuanAn(dataRoot, odauInterface, vitrihientai, itemtieptheo, itemdaco);
        }
        else {
            nodeRoot.addListenerForSingleValueEvent(valueEventListener);
        }


    }

    private void LayDanhSachQuanAn (DataSnapshot dataSnapshot, OdauInterface odauInterface,
                                    Location vitrihientai, int itemtieptheo, int itemdaco) {

        // Mặc định ban đầu: itemtieptheo = 3, itembandau = 0

        DataSnapshot dataSnapshotQuanAn = dataSnapshot.child("quanans");
        int i = 0;

        // Lấy danh sách quán ăn
        for (DataSnapshot valueQuanAn : dataSnapshotQuanAn.getChildren()) {

            if (i == itemtieptheo) {
                break;
            }
            if (i < itemdaco) {
                i++;
                continue;
            }
            i++;

            QuanAnModel quanAnModel = valueQuanAn.getValue(QuanAnModel.class);
            quanAnModel.setMaquanan(valueQuanAn.getKey());


            // Lấy hình ảnh trong từng quán ăn
            DataSnapshot dataSnapshotHinhQuanAn = dataSnapshot.child("hinhanhquanans").
                    child(valueQuanAn.getKey());
            List<String> hinhanhList = new ArrayList<>();

            for (DataSnapshot valueHinhQuanAn : dataSnapshotHinhQuanAn.getChildren()) {
                hinhanhList.add(valueHinhQuanAn.getValue(String.class));
            }
            quanAnModel.setHinhanhquanan(hinhanhList);


            // Lấy danh sách bình luận của quán ăn
            DataSnapshot snapshotBinhLuan = dataSnapshot.child("binhluans").
                    child(valueQuanAn.getKey());
            List<BinhLuanModel> binhLuanModels = new ArrayList<>();

            for (DataSnapshot valueBinhLuan : snapshotBinhLuan.getChildren()) {
                BinhLuanModel binhLuanModel = valueBinhLuan.getValue(BinhLuanModel.class);
                binhLuanModel.setMabinhluan(valueBinhLuan.getKey());
                // Lấy ra được thành viên thực hiện bình luận này
                ThanhVienModel thanhVienModel = dataSnapshot.child("thanhviens").
                        child(binhLuanModel.getMauser()).getValue(ThanhVienModel.class);
                binhLuanModel.setThanhVienModel(thanhVienModel);

                List<String> hinhanhBinhluanList = new ArrayList<>();
                DataSnapshot nodeHinhAnhBinhLuan = dataSnapshot.child("hinhanhbinhluans").child(binhLuanModel.getMabinhluan());
                for (DataSnapshot valueHinhAnhBinhLuan : nodeHinhAnhBinhLuan.getChildren()) {
                    hinhanhBinhluanList.add(valueHinhAnhBinhLuan.getValue(String.class));
                }
                binhLuanModel.setHinhanhBinhLuanList(hinhanhBinhluanList);

                binhLuanModels.add(binhLuanModel);
            }
            quanAnModel.setBinhLuanModelList(binhLuanModels);


            // Lấy chi nhánh quán ăn
            DataSnapshot snapshotChiNhanhQuanAn = dataSnapshot.child("chinhanhquanans").child(quanAnModel.getMaquanan());
            List<ChiNhanhQuanAnModel> chiNhanhQuanAnModels = new ArrayList<>();


            for (DataSnapshot valueChiNhanhQuanAn : snapshotChiNhanhQuanAn.getChildren()) {
                ChiNhanhQuanAnModel chiNhanhQuanAnModel = valueChiNhanhQuanAn.getValue(ChiNhanhQuanAnModel.class);

                Location vitriquanan = new Location("");
                vitriquanan.setLatitude(chiNhanhQuanAnModel.getLatitude());
                vitriquanan.setLongitude(chiNhanhQuanAnModel.getLongitude());
                double khoangcach = vitriquanan.distanceTo(vitrihientai) / 1000;
                chiNhanhQuanAnModel.setKhoangcach(khoangcach);

                chiNhanhQuanAnModels.add(chiNhanhQuanAnModel);
            }
            quanAnModel.setChiNhanhQuanAnModelList(chiNhanhQuanAnModels);



            // Tạo interface với mục đích lấy ra được danh sách quán ăn (vì nếu return ra
            // list quán ăn sẽ ko được vì bị vướng tiến trình - onDataChange sẽ tạo ra tiến trình
            // mới độc lập)
            odauInterface.getDanhSachQuanAnModel(quanAnModel);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeByte((byte) (giaohang ? 1 : 0));
        parcel.writeString(maquanan);
        parcel.writeString(giodongcua);
        parcel.writeString(giomocua);
        parcel.writeString(tenquanan);
        parcel.writeString(ngaytao);
        parcel.writeString(videogioithieu);
        parcel.writeLong(luotthich);
        parcel.writeLong(giatoida);
        parcel.writeLong(giatoithieu);
        parcel.writeStringList(tienich);
        parcel.writeStringList(hinhanhquanan);

        parcel.writeTypedList(chiNhanhQuanAnModelList);
        parcel.writeTypedList(binhLuanModelList);

        // Không gửi bitmap qua chi tiết quán ăn vì bitmap khá nặng, làm treo app
//        parcel.writeTypedList(bitmapList);
    }
}
