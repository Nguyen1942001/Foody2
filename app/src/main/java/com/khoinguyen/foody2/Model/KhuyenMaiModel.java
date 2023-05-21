package com.khoinguyen.foody2.Model;

public class KhuyenMaiModel {
    String makhuyenmai, ten, ngaybatdau, ngayketthuc;
    int luotsudung;
    int giamgia;

    public KhuyenMaiModel() {
    }

    public KhuyenMaiModel(String ten, String ngaybatdau, String ngayketthuc, int luotsudung, int giamgia) {
        this.ten = ten;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.luotsudung = luotsudung;
        this.giamgia = giamgia;
    }

    public String getMakhuyenmai() {
        return makhuyenmai;
    }

    public void setMakhuyenmai(String makhuyenmai) {
        this.makhuyenmai = makhuyenmai;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgaybatdau() {
        return ngaybatdau;
    }

    public void setNgaybatdau(String ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public String getNgayketthuc() {
        return ngayketthuc;
    }

    public void setNgayketthuc(String ngayketthuc) {
        this.ngayketthuc = ngayketthuc;
    }

    public int getLuotsudung() {
        return luotsudung;
    }

    public void setLuotsudung(int luotsudung) {
        this.luotsudung = luotsudung;
    }

    public int getGiamGia() {
        return giamgia;
    }

    public void setGiamGia(int giamgia) {
        this.giamgia = giamgia;
    }
}
