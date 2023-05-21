package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
import com.khoinguyen.foody2.Model.DonHangModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.ChiTietLichSuDonHangActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterLichSuDonHang extends RecyclerView.Adapter<AdapterLichSuDonHang.ViewHolderLichSuDonHang> {

    Context context;
    int resource;
    List<DonHangModel> donHangModelList;

    public AdapterLichSuDonHang(Context context, int resource, List<DonHangModel> donHangModelList) {
        this.context = context;
        this.resource = resource;
        this.donHangModelList = donHangModelList;
    }

    public class ViewHolderLichSuDonHang extends RecyclerView.ViewHolder {

        LinearLayout khungDonHang;
        ImageView imgQuanAn;
        TextView tvTenQuanAn, tvTongTien;
        Button btnXemDonHang;

        public ViewHolderLichSuDonHang(@NonNull View itemView) {
            super(itemView);

            khungDonHang = itemView.findViewById(R.id.khungDonHang);
            imgQuanAn = itemView.findViewById(R.id.imgQuanAn);
            tvTenQuanAn = itemView.findViewById(R.id.tvTenQuanAn);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
            btnXemDonHang = itemView.findViewById(R.id.btnXemDonHang);
        }
    }

    @NonNull
    @Override
    public AdapterLichSuDonHang.ViewHolderLichSuDonHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_lichsudonhang, parent, false);
        AdapterLichSuDonHang.ViewHolderLichSuDonHang viewHolderLichSuDonHang = new ViewHolderLichSuDonHang(view);

        return viewHolderLichSuDonHang;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLichSuDonHang.ViewHolderLichSuDonHang holder, int position) {
        DonHangModel donHangModel = donHangModelList.get(position);

        String linkhinhquanan = donHangModel.getLinkhinhquanan();
        DownLoadHinhQuanAn(linkhinhquanan, holder);

        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        holder.tvTenQuanAn.setText(donHangModel.getTenquanan());
        holder.tvTongTien.setText(numberFormat.format(donHangModel.getTongtien()));

        holder.khungDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iChiTietLichSuDonHang = new Intent(context, ChiTietLichSuDonHangActivity.class);
                iChiTietLichSuDonHang.putExtra("donhang", donHangModel);
                context.startActivity(iChiTietLichSuDonHang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donHangModelList.size();
    }

    private void DownLoadHinhQuanAn(String linkhinhquanan, AdapterLichSuDonHang.ViewHolderLichSuDonHang holder) {
        // Dowload hình ảnh của món ăn
        StorageReference storageHinhMonAn = FirebaseStorage.getInstance().getReference().
                child("hinhanh").child(linkhinhquanan);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhMonAn.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap hinhquanan = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgQuanAn.setImageBitmap(hinhquanan);
            }
        });
    }
}
