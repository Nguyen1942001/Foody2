package com.khoinguyen.foody2.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.HienThiChiTietBinhLuanActivity;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecyclerHinhBinhLuan extends RecyclerView.Adapter<AdapterRecyclerHinhBinhLuan.ViewHolderHinhBinhLuan> {

    Context context;
    int resource;
    List<Bitmap> listHinh;
    BinhLuanModel binhLuanModel;
    boolean isChiTietBinhLuan;


    public AdapterRecyclerHinhBinhLuan (Context context, int resource, List<Bitmap> listHinh,
                                        BinhLuanModel binhLuanModel, boolean isChiTietBinhLuan) {
        this.context = context;
        this.resource = resource;
        this.listHinh = listHinh;
        this.binhLuanModel = binhLuanModel;
        this.isChiTietBinhLuan = isChiTietBinhLuan;
    }

    public class ViewHolderHinhBinhLuan extends RecyclerView.ViewHolder {

        ImageView imageHinhBinhLuan;
        TextView txtSoHinhBinhLuan;
        FrameLayout khungSoHinhBinhLuan;

        public ViewHolderHinhBinhLuan(@NonNull View itemView) {
            super(itemView);

            imageHinhBinhLuan = itemView.findViewById(R.id.imageHinhBinhLuan);
            txtSoHinhBinhLuan = itemView.findViewById(R.id.txtSoHinhBinhLuan);
            khungSoHinhBinhLuan = itemView.findViewById(R.id.khungSoHinhBinhLuan);
        }
    }

    @NonNull
    @Override
    public AdapterRecyclerHinhBinhLuan.ViewHolderHinhBinhLuan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ViewHolderHinhBinhLuan viewHolderHinhBinhLuan = new ViewHolderHinhBinhLuan(view);

        return viewHolderHinhBinhLuan;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerHinhBinhLuan.ViewHolderHinhBinhLuan holder, int position) {
        holder.imageHinhBinhLuan.setImageBitmap(listHinh.get(position));

        // Nếu đang ở trang bình luận (trang chi tiết quán ăn - thì cho phép click
        // vào hình cuối cùng để load chi tiết bình luận, ngược lại thì ko cho click)
        if (isChiTietBinhLuan == false) {

            // Nếu số hình ảnh trong bình luận đó > 4, thì sẽ thêm phần "+ số hình còn lại"
            if (position == 3) {
                int sohinhconlai = listHinh.size() - 4;
                if (sohinhconlai > 0) {
                    holder.khungSoHinhBinhLuan.setVisibility(View.VISIBLE);
                    holder.txtSoHinhBinhLuan.setText("+" + sohinhconlai);

                    // Click vào hình cuối cùng để qua trang chi tiết bình luận (nếu số hình > 4)
                    holder.imageHinhBinhLuan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent iChiTietBinhLuan = new Intent(context, HienThiChiTietBinhLuanActivity.class);
                            iChiTietBinhLuan.putExtra("binhluanmodel", binhLuanModel);
                            context.startActivity(iChiTietBinhLuan);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isChiTietBinhLuan == false) {
            if (listHinh.size() < 4) {
                return listHinh.size();
            }
            else {
                return 4;
            }
        }
        else {
            return listHinh.size();
        }
    }

}
