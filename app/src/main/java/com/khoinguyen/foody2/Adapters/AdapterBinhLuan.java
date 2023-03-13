package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBinhLuan extends RecyclerView.Adapter<AdapterBinhLuan.ViewHolder> {

    Context context;
    int layout;
    List<BinhLuanModel> binhLuanModelList;


    public AdapterBinhLuan(Context context, int layout, List<BinhLuanModel> binhLuanModelList) {
        this.context = context;
        this.layout = layout;
        this.binhLuanModelList = binhLuanModelList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView txtTieuDeBinhLuan, txtNoiDungBinhLuan, txtSoDiem;
        RecyclerView recyclerHinhBinhLuan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.circleImageUser);
            txtTieuDeBinhLuan = itemView.findViewById(R.id.txtTieuDeBinhLuan);
            txtNoiDungBinhLuan = itemView.findViewById(R.id.txtNoiDungBinhLuan);
            txtSoDiem = itemView.findViewById(R.id.txtChamDiemBinhLuan);
            recyclerHinhBinhLuan = itemView.findViewById(R.id.recyclerHinhBinhLuan);
        }
    }

    @NonNull
    @Override
    public AdapterBinhLuan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBinhLuan.ViewHolder holder, int position) {
        BinhLuanModel binhLuanModel = binhLuanModelList.get(position);
        holder.txtTieuDeBinhLuan.setText(binhLuanModel.getTieude());
        holder.txtNoiDungBinhLuan.setText(binhLuanModel.getNoidung());
        holder.txtSoDiem.setText(binhLuanModel.getChamdiem() + "");
        setHinhAnhBinhLuan(holder.circleImageView, binhLuanModel.getThanhVienModel().getHinhanh());

        // Download các hình có trong mỗi bình luận
        List<Bitmap> bitmapList = new ArrayList<>();
        for (String linkhinh : binhLuanModel.getHinhanhBinhLuanList()) {
            StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().
                    child("hinhanh").child(linkhinh);

            long ONE_MEGABYTE = 1024 * 1024 * 5;
            storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmapList.add(bitmap);

                    // Kiểm tra xem đã download đủ hình ảnh trong từng bình luận hay chưa
                    if (bitmapList.size() == binhLuanModel.getHinhanhBinhLuanList().size()) {

                        // Load hình ảnh trong mỗi bình luận
                        AdapterRecyclerHinhBinhLuan adapterRecyclerHinhBinhLuan = new AdapterRecyclerHinhBinhLuan(context,
                                R.layout.custom_layout_hinhbinhluan, bitmapList, binhLuanModel, false);

                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
                        holder.recyclerHinhBinhLuan.setLayoutManager(layoutManager);
                        holder.recyclerHinhBinhLuan.setAdapter(adapterRecyclerHinhBinhLuan);
                        adapterRecyclerHinhBinhLuan.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int soBinhLuan = binhLuanModelList.size();
        if (soBinhLuan > 5) {
            return 5;
        }
        else {
            return soBinhLuan;
        }
    }

    private void setHinhAnhBinhLuan(CircleImageView circleImageView, String linkhinh) {
        StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().
                child("thanhvien").child(linkhinh);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImageView.setImageBitmap(bitmap);
            }
        });
    }


}
