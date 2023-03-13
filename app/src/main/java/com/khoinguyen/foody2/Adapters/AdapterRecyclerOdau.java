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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.Model.ChiNhanhQuanAnModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.ChiTietQuanAnActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerOdau extends RecyclerView.Adapter<AdapterRecyclerOdau.ViewHolder> {

    List<QuanAnModel> quanAnModelList;
    int resource;
    Context context;

    // Chạy đầu tiên
    public AdapterRecyclerOdau (Context context, List<QuanAnModel> quanAnModelList, int resource) {
        this.quanAnModelList = quanAnModelList;
        this.resource = resource;
        this.context = context;
    }

    // Chạy thứ 3
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenQuanAnOdau, txtTieuDeBinhLuan, txtTieuDeBinhLuan2, txtNoiDungBinhLuan,
                txtNoiDungBinhLuan2, txtChamDiemBinhLuan, txtChamDiemBinhLuan2, txtTongBinhLuan,
                txtTongHinhBinhLuan, txtDiemTrungBinhQuanAn,  txtDiaChiQuanAnOdau, txtKhoangCachQuanAnOdau;
        Button btnDatMonOdau;
        ImageView imageHinhQuanAnOdau;
        CircleImageView circleImageUser, circleImageUser2;
        LinearLayout containerBinhLuan, containerBinhLuan2;
        CardView cardViewOdau;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTenQuanAnOdau = itemView.findViewById(R.id.txtTenQuanAnOdau);
            btnDatMonOdau = itemView.findViewById(R.id.btnDatMonOdau);
            imageHinhQuanAnOdau = itemView.findViewById(R.id.imageHinhQuanAnOdau);
            txtTieuDeBinhLuan = itemView.findViewById(R.id.txtTieuDeBinhLuan);
            txtTieuDeBinhLuan2 = itemView.findViewById(R.id.txtTieuDeBinhLuan2);
            txtNoiDungBinhLuan = itemView.findViewById(R.id.txtNoiDungBinhLuan);
            txtNoiDungBinhLuan2 = itemView.findViewById(R.id.txtNoiDungBinhLuan2);
            circleImageUser = itemView.findViewById(R.id.circleImageUser);
            circleImageUser2 = itemView.findViewById(R.id.circleImageUser2);
            containerBinhLuan = itemView.findViewById(R.id.containerBinhLuan);
            containerBinhLuan2 = itemView.findViewById(R.id.containerBinhLuan2);
            txtChamDiemBinhLuan = itemView.findViewById(R.id.txtChamDiemBinhLuan);
            txtChamDiemBinhLuan2 = itemView.findViewById(R.id.txtChamDiemBinhLuan2);
            txtTongBinhLuan = itemView.findViewById(R.id.txtTongBinhLuan);
            txtTongHinhBinhLuan = itemView.findViewById(R.id.txtTongHinhBinhLuan);
            txtDiemTrungBinhQuanAn = itemView.findViewById(R.id.txtDiemTrungBinhQuanAn);
            txtDiaChiQuanAnOdau = itemView.findViewById(R.id.txtDiaChiQuanAnOdau);
            txtKhoangCachQuanAnOdau = itemView.findViewById(R.id.txtKhoangCachQuanAnOdau);
            cardViewOdau = itemView.findViewById(R.id.cardViewOdau);
        }
    }

    // Chạy thú 2
    @NonNull
    @Override
    public AdapterRecyclerOdau.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // Chạy thứ 4
    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerOdau.ViewHolder holder, int position) {
        QuanAnModel quanAnModel = quanAnModelList.get(position);
        holder.txtTenQuanAnOdau.setText(quanAnModel.getTenquanan());

        if (quanAnModel.isGiaohang() == true) {
            holder.btnDatMonOdau.setVisibility(View.VISIBLE);
        }

        if (quanAnModel.getBitmapList().size() > 0) {
            holder.imageHinhQuanAnOdau.setImageBitmap(quanAnModel.getBitmapList().get(0));
        }

        // Hiện bình luận cho quán ăn
        if (quanAnModel.getBinhLuanModelList().size() > 0) {
            BinhLuanModel binhLuanModel = quanAnModel.getBinhLuanModelList().get(0);
            holder.txtTieuDeBinhLuan.setText(binhLuanModel.getTieude());
            holder.txtNoiDungBinhLuan.setText(binhLuanModel.getNoidung());
            holder.txtChamDiemBinhLuan.setText(binhLuanModel.getChamdiem() + "");

            setHinhAnhBinhLuan(holder.circleImageUser, binhLuanModel.getThanhVienModel().getHinhanh());

            // Load nội dung bình luận số 2 (nếu có)
            if (quanAnModel.getBinhLuanModelList().size() >= 2) {
                BinhLuanModel binhLuanModel2 = quanAnModel.getBinhLuanModelList().get(1);
                holder.txtTieuDeBinhLuan2.setText(binhLuanModel2.getTieude());
                holder.txtNoiDungBinhLuan2.setText(binhLuanModel2.getNoidung());
                holder.txtChamDiemBinhLuan2.setText(binhLuanModel2.getChamdiem() + "");

                setHinhAnhBinhLuan(holder.circleImageUser2, binhLuanModel2.getThanhVienModel().getHinhanh());
            }

            // Lấy tổng bình luận của quán ăn
            holder.txtTongBinhLuan.setText(quanAnModel.getBinhLuanModelList().size() + "");

            // Lấy tổng số hình ảnh và điểm của các bình luận trong quán ăn
            int tongSoHinhAnhBinhLuan = 0;
            double tongDiem = 0;
            for (BinhLuanModel binhLuanModel1 : quanAnModel.getBinhLuanModelList()) {
                tongSoHinhAnhBinhLuan += binhLuanModel1.getHinhanhBinhLuanList().size();
                tongDiem += binhLuanModel1.getChamdiem();
            }
            if (tongSoHinhAnhBinhLuan > 0) {
                holder.txtTongHinhBinhLuan.setText(tongSoHinhAnhBinhLuan + "");
            }

            double diemTrungBinh = tongDiem / quanAnModel.getBinhLuanModelList().size();
            holder.txtDiemTrungBinhQuanAn.setText(String.format("%.1f", diemTrungBinh));

        }
        else {
            // Ẩn bình luận mẫu khi quán ăn không có bình luận
            holder.containerBinhLuan.setVisibility(View.GONE);
            holder.containerBinhLuan2.setVisibility(View.GONE);
        }


        // Lấy địa chỉ chi nhánh và khoảng cách từ chi nhánh tới location hiện tại
        if (quanAnModel.getChiNhanhQuanAnModelList().size() > 0) {
            ChiNhanhQuanAnModel chiNhanhQuanAnModelTam = quanAnModel.getChiNhanhQuanAnModelList().get(0);

            for (ChiNhanhQuanAnModel chiNhanhQuanAnModel : quanAnModel.getChiNhanhQuanAnModelList()) {
                if (chiNhanhQuanAnModelTam.getKhoangcach() > chiNhanhQuanAnModel.getKhoangcach()) {
                    chiNhanhQuanAnModelTam = chiNhanhQuanAnModel;
                }
            }

            holder.txtDiaChiQuanAnOdau.setText(chiNhanhQuanAnModelTam.getDiachi());
            holder.txtKhoangCachQuanAnOdau.setText(String.format("%.1f", chiNhanhQuanAnModelTam.getKhoangcach()) + "km");
        }


        // Chuyển sang trang chi tiết quán ăn khi click vào quán ăn
        holder.cardViewOdau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iChiTietQuanAn = new Intent(context, ChiTietQuanAnActivity.class);
                iChiTietQuanAn.putExtra("quanan", quanAnModel);
                context.startActivity(iChiTietQuanAn);
            }
        });
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

    @Override
    public int getItemCount() {
        return quanAnModelList.size();
    }


}
