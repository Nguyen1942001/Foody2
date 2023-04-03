package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.ChiTietLichSuDonHangActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterChiTietLichSuDonHang extends RecyclerView.Adapter<AdapterChiTietLichSuDonHang.ViewHolderChiTietLichSuDonHang> {

    Context context;
    int resource;
    List<DatMonModel> datMonModelList;

    public AdapterChiTietLichSuDonHang(Context context, int resource, List<DatMonModel> datMonModelList) {
        this.context = context;
        this.resource = resource;
        this.datMonModelList = datMonModelList;
    }

    public class ViewHolderChiTietLichSuDonHang extends RecyclerView.ViewHolder {

        TextView tvThongTinMonAn;

        public ViewHolderChiTietLichSuDonHang(@NonNull View itemView) {
            super(itemView);
            tvThongTinMonAn = itemView.findViewById(R.id.tvThongTinMonAn);
        }
    }

    @NonNull
    @Override
    public AdapterChiTietLichSuDonHang.ViewHolderChiTietLichSuDonHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_chitiet_lichsudonhang, parent, false);
        AdapterChiTietLichSuDonHang.ViewHolderChiTietLichSuDonHang viewHolderChiTietLichSuDonHang = new ViewHolderChiTietLichSuDonHang(view);

        return viewHolderChiTietLichSuDonHang;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChiTietLichSuDonHang.ViewHolderChiTietLichSuDonHang holder, int position) {
        DatMonModel datMonModel = datMonModelList.get(position);

        String thongtinmonan = datMonModel.getSoluong() + " món | " +datMonModel.getTenmonan() + " (x" + datMonModel.getSoluong() + ")";
        holder.tvThongTinMonAn.setText(thongtinmonan);
    }

    @Override
    public int getItemCount() {
        return datMonModelList.size();
    }


}
