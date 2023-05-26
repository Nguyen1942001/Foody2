package com.khoinguyen.foody2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.Model.MonAnModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.CapNhatThongTinUserActivity;
import com.khoinguyen.foody2.View.ChonHinhBinhLuanActivity;
import com.khoinguyen.foody2.View.DatMonActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterDatMon extends RecyclerView.Adapter<AdapterDatMon.ViewHolderDatMon> {

    Context context;
    int resource;
    List<DatMonModel> datMonModelList;
    long tongGiaTriSanPham;

    public AdapterDatMon (Context context, int resource, List<DatMonModel> datMonModelList, long tongGiaTriSanPham) {
        this.context = context;
        this.resource = resource;
        this.datMonModelList = datMonModelList;
        this.tongGiaTriSanPham = tongGiaTriSanPham;
    }

    public class ViewHolderDatMon extends RecyclerView.ViewHolder {

        TextView txtTenMonAn, txtSoLuongMonAn, txtGiaTien;
        ImageView imgGiamSoLuong, imgTangSoLuong;

        public ViewHolderDatMon(@NonNull View itemView) {
            super(itemView);

            txtTenMonAn = itemView.findViewById(R.id.txtTenMonAn);
            txtSoLuongMonAn = itemView.findViewById(R.id.txtSoLuongMonAn);
            txtGiaTien = itemView.findViewById(R.id.txtGiaTien);

            imgGiamSoLuong = itemView.findViewById(R.id.imgGiamSoLuong);
            imgTangSoLuong = itemView.findViewById(R.id.imgTangSoLuong);
        }
    }

    @NonNull
    @Override
    public AdapterDatMon.ViewHolderDatMon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_datmon, parent, false);
        AdapterDatMon.ViewHolderDatMon viewHolderDatMon = new ViewHolderDatMon(view);

        return viewHolderDatMon;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDatMon.ViewHolderDatMon holder, int position) {
        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        DatMonModel datMonModel = datMonModelList.get(position);
        long giatong = datMonModel.getGiatien() * datMonModel.getSoluong();

        holder.txtTenMonAn.setText(datMonModel.getTenmonan());
        holder.txtSoLuongMonAn.setText(datMonModel.getSoluong() + "");
        holder.txtGiaTien.setText(numberFormat.format(giatong));

        // Tăng số lượng món ăn
        holder.txtSoLuongMonAn.setTag(0);
        holder.imgTangSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datMonModel.setSoluong(datMonModel.getSoluong() + 1);

                long tongtien = datMonModel.getGiatien() * datMonModel.getSoluong();
                holder.txtGiaTien.setText(numberFormat.format(tongtien));
                holder.txtSoLuongMonAn.setText(datMonModel.getSoluong() + "");

                tongGiaTriSanPham = tongGiaTriSanPham + datMonModel.getGiatien();

                TextView txtTongTienTruoc = ((Activity) context).findViewById(R.id.txtTongTienTruoc);
                TextView txtTongTienSau = ((Activity) context).findViewById(R.id.txtTongTienSau);
                TextView txtTongTienOnline = ((Activity) context).findViewById(R.id.txtTongTienOnline);
                TextView txtTongTienMat = ((Activity) context).findViewById(R.id.txtTongTienMat);
                Spinner spinnerKhuyenMai = ((Activity) context).findViewById(R.id.spinnerKhuyenMai);
                spinnerKhuyenMai.setSelection(0);


                txtTongTienTruoc.setText(numberFormat.format(tongGiaTriSanPham));
                txtTongTienSau.setText(numberFormat.format(tongGiaTriSanPham + 3000));
                txtTongTienOnline.setText(numberFormat.format(tongGiaTriSanPham + 3000));
                txtTongTienMat.setText(numberFormat.format(tongGiaTriSanPham + 3000));
            }
        });

        // Giảm số lượng món ăn
        holder.imgGiamSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datMonModel.setSoluong(datMonModel.getSoluong() - 1);

                long tongtien = datMonModel.getGiatien() * datMonModel.getSoluong();
                holder.txtGiaTien.setText(numberFormat.format(tongtien));
                holder.txtSoLuongMonAn.setText(datMonModel.getSoluong() + "");

                tongGiaTriSanPham = tongGiaTriSanPham - datMonModel.getGiatien();

                TextView txtTongTienTruoc = ((Activity) context).findViewById(R.id.txtTongTienTruoc);
                TextView txtTongTienSau = ((Activity) context).findViewById(R.id.txtTongTienSau);
                TextView txtTongTienOnline = ((Activity) context).findViewById(R.id.txtTongTienOnline);
                TextView txtTongTienMat = ((Activity) context).findViewById(R.id.txtTongTienMat);
                Spinner spinnerKhuyenMai = ((Activity) context).findViewById(R.id.spinnerKhuyenMai);
                spinnerKhuyenMai.setSelection(0);

                txtTongTienTruoc.setText(numberFormat.format(tongGiaTriSanPham));
                txtTongTienSau.setText(numberFormat.format(tongGiaTriSanPham + 3000));
                txtTongTienOnline.setText(numberFormat.format(tongGiaTriSanPham + 3000));
                txtTongTienMat.setText(numberFormat.format(tongGiaTriSanPham + 3000));

            }
        });
    }

    @Override
    public int getItemCount() {
        return datMonModelList.size();
    }



}
