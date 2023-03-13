package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Model.ChonHinhBinhLuanModel;
import com.khoinguyen.foody2.R;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class AdapterChonHinhBinhLuan extends RecyclerView.Adapter<AdapterChonHinhBinhLuan.ViewHolderChonHinh>{

    Context context;
    int resource;
    List<ChonHinhBinhLuanModel> listDuongDan;

    public AdapterChonHinhBinhLuan (Context context, int resource, List<ChonHinhBinhLuanModel> listDuongDan) {
        this.context = context;
        this.resource = resource;
        this.listDuongDan = listDuongDan;
    }

    public class ViewHolderChonHinh extends RecyclerView.ViewHolder {

        ImageView imgChonHinhBinhLuan;
        CheckBox checkboxChonHinhBinhLuan;

        public ViewHolderChonHinh(@NonNull View itemView) {
            super(itemView);

            imgChonHinhBinhLuan = itemView.findViewById(R.id.imgChonHinhBinhLuan);
            checkboxChonHinhBinhLuan = itemView.findViewById(R.id.checkboxChonHinhBinhLuan);
        }
    }

    @NonNull
    @Override
    public AdapterChonHinhBinhLuan.ViewHolderChonHinh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ViewHolderChonHinh viewHolderChonHinh = new ViewHolderChonHinh(view);

        return viewHolderChonHinh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChonHinhBinhLuan.ViewHolderChonHinh holder, int position) {
        ChonHinhBinhLuanModel chonHinhBinhLuanModel = listDuongDan.get(position);
        Uri uri = Uri.parse(chonHinhBinhLuanModel.getDuongdan());
//        Log.d("kiemtra1", chonHinhBinhLuanModel.getDuongdan());
//        Log.d("kiemtra2", uri + "");

        holder.imgChonHinhBinhLuan.setImageURI(uri);
        holder.checkboxChonHinhBinhLuan.setChecked(chonHinhBinhLuanModel.isCheck());

        // Check vào hình thì cập nhật isCheck ChonHinhBinhLuanModel = true
        holder.checkboxChonHinhBinhLuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                listDuongDan.get(position).setCheck(checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDuongDan.size();
    }


}
