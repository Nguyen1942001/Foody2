package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.R;

import java.util.List;

public class AdapterHienThiHinhBinhLuanDuocChon extends
        RecyclerView.Adapter<AdapterHienThiHinhBinhLuanDuocChon.ViewHolderHinhBinhLuan> {

    Context context;
    int resource;
    List<String> list;

    public AdapterHienThiHinhBinhLuanDuocChon (Context context, int resource, List<String> list) {
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    public class ViewHolderHinhBinhLuan extends RecyclerView.ViewHolder {

        ImageView imageView, imgXoa;

        public ViewHolderHinhBinhLuan(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imgXoa = itemView.findViewById(R.id.imgXoa);
        }
    }

    @NonNull
    @Override
    public AdapterHienThiHinhBinhLuanDuocChon.ViewHolderHinhBinhLuan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ViewHolderHinhBinhLuan viewHolderHinhBinhLuan = new ViewHolderHinhBinhLuan(view);

        return viewHolderHinhBinhLuan;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHienThiHinhBinhLuanDuocChon.ViewHolderHinhBinhLuan holder, int position) {
        Uri uri = Uri.parse(list.get(position));
        holder.imageView.setImageURI(uri);

        // Gắn thứ tự hình cho nút xóa
        holder.imgXoa.setTag(position);

        // Xóa hình
        holder.imgXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vitri = (int) holder.imgXoa.getTag();
                list.remove(vitri);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
