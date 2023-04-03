package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.Model.MonAnModel;
import com.khoinguyen.foody2.R;
import com.khoinguyen.foody2.View.ChiTietQuanAnActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterMonAn extends RecyclerView.Adapter<AdapterMonAn.ViewHolderMonAn> {

    Context context;
    List<MonAnModel> monAnModelList;

    // Tạo danh sách món ăn được đặt bằng static (xài bất kì ở đâu)
    public static ArrayList<DatMonModel> datMonModelList = new ArrayList<>();

    public AdapterMonAn (Context context, List<MonAnModel> monAnModelList) {
        this.context = context;
        this.monAnModelList = monAnModelList;
    }

    public class ViewHolderMonAn extends RecyclerView.ViewHolder {

        TextView txtTenMonAn, txtSoLuong, txtGiaMonAn;
        ImageView imgGiamSoLuong, imgTangSoLuong, imgHinhQuanAn;

        public ViewHolderMonAn(@NonNull View itemView) {
            super(itemView);

            txtTenMonAn = itemView.findViewById(R.id.txtTenMonAn);
            txtSoLuong = itemView.findViewById(R.id.txtSoLuong);
            imgGiamSoLuong = itemView.findViewById(R.id.imgGiamSoLuong);
            imgTangSoLuong = itemView.findViewById(R.id.imgTangSoLuong);
            txtGiaMonAn = itemView.findViewById(R.id.txtGiaMonAn);
            imgHinhQuanAn = itemView.findViewById(R.id.imgHinhQuanAn);
        }
    }

    @NonNull
    @Override
    public AdapterMonAn.ViewHolderMonAn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_monan, parent, false);
        AdapterMonAn.ViewHolderMonAn viewHolderMonAn = new ViewHolderMonAn(view);

        return viewHolderMonAn;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMonAn.ViewHolderMonAn holder, int position) {
        MonAnModel monAnModel = monAnModelList.get(position);
        String linkhinh = monAnModel.getHinhanh();

        // Dowload hình ảnh của món ăn
        StorageReference storageHinhMonAn = FirebaseStorage.getInstance().getReference().
                child("hinhanh").child(linkhinh);

        long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageHinhMonAn.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap hinhMonAn = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgHinhQuanAn.setImageBitmap(hinhMonAn);
            }
        });

        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        holder.txtTenMonAn.setText(monAnModel.getTenmon());
        holder.txtGiaMonAn.setText(numberFormat.format(monAnModel.getGiatien()));


        // Tăng số lượng món ăn
        holder.txtSoLuong.setTag(0);
        holder.imgTangSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dem = Integer.parseInt(holder.txtSoLuong.getTag().toString());
                dem++;
                holder.txtSoLuong.setText(dem + "");
                holder.txtSoLuong.setTag(dem);

                DatMonModel datMonModelTag = (DatMonModel) holder.imgGiamSoLuong.getTag();
                if (datMonModelTag != null) {
                    AdapterMonAn.datMonModelList.remove(datMonModelTag);
                }

                DatMonModel datMonModel = new DatMonModel();
                datMonModel.setMamon(monAnModel.getMamon());
                datMonModel.setTenmonan(monAnModel.getTenmon());
                datMonModel.setGiatien(monAnModel.getGiatien());
                datMonModel.setSoluong(dem);
                datMonModel.setLinkhinh(linkhinh);

                // Set tag vào nút giảm số lượng để loại bỏ món ăn khỏi list khi số lượng = 0
                holder.imgGiamSoLuong.setTag(datMonModel);

                AdapterMonAn.datMonModelList.add(datMonModel);

//                for (DatMonModel datMonModel1 : AdapterMonAn.datMonModelList) {
//                    Log.d("kiemtratang", datMonModel1.getTenmonan() + " - " + datMonModel1.getSoluong());
//                }
            }
        });

        // Giảm số lượng món ăn
        holder.imgGiamSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dem = Integer.parseInt(holder.txtSoLuong.getTag().toString());
                if (dem != 0) {
                    dem--;

                    // Nếu số lượng món ăn giảm về 0, thì xóa món ăn khỏi list
                    if (dem == 0) {
                        DatMonModel datMonModel = (DatMonModel) view.getTag();
                        AdapterMonAn.datMonModelList.remove(datMonModel);
                    }

                    // Nếu giảm số lượng của món ăn (> 0) thì xóa món ăn đó đi, cập nhật số lượng mới và thêm lại vào list
                    else {
                        DatMonModel datMonModel = (DatMonModel) view.getTag();
                        AdapterMonAn.datMonModelList.remove(datMonModel);

                        datMonModel.setSoluong(dem);
                        AdapterMonAn.datMonModelList.add(datMonModel);
                    }
                }

                holder.txtSoLuong.setText(dem + "");
                holder.txtSoLuong.setTag(dem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return monAnModelList.size();
    }


}
