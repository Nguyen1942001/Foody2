package com.khoinguyen.foody2.Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoinguyen.foody2.Adapters.AdapterRecyclerOdau;
import com.khoinguyen.foody2.Controller.Interfaces.OdauInterface;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class OdauController {

    AdapterRecyclerOdau adapterRecyclerOdau;
    Context context;
    QuanAnModel quanAnModel;
    int itemdaco = 3;

    public OdauController (Context context) {
        this.context = context;
        quanAnModel = new QuanAnModel();
    }

    public void getDanhSachQuanAnController (Context context, NestedScrollView nestScrollViewOdau, RecyclerView recyclerViewOdau,
                                             ProgressBar progressBarOdau, Location vitrihientai) {
        final List<QuanAnModel> quanAnModelList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewOdau.setLayoutManager(layoutManager);
        adapterRecyclerOdau = new AdapterRecyclerOdau(context, quanAnModelList, R.layout.custom_layout_recyclerview_odau);
        recyclerViewOdau.setAdapter(adapterRecyclerOdau);

        progressBarOdau.setVisibility(View.VISIBLE);

        OdauInterface odauInterface = new OdauInterface() {
            @Override
            public void getDanhSachQuanAnModel(QuanAnModel quanAnModel) {
                List<Bitmap> bitmaps = new ArrayList<>();
                for (String linkhinh : quanAnModel.getHinhanhquanan()) {

                    // Lấy hình ảnh đầu tiên trong danh sách hình ảnh của từng quán ăn
                    StorageReference storageHinhAnh = FirebaseStorage.getInstance().getReference().
                            child("hinhanh").child(linkhinh);
                    long ONE_MEGABYTE = 1024 * 1024 * 5;
                    storageHinhAnh.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmaps.add(bitmap);
                            quanAnModel.setBitmapList(bitmaps);

                            // Kiểm tra xem đã lấy đủ hình ảnh chưa
                            if (quanAnModel.getBitmapList().size() == quanAnModel.getHinhanhquanan().size()) {
                                quanAnModelList.add(quanAnModel);

                                // Gọi lại AdapterRecyclerOdau
                                adapterRecyclerOdau.notifyDataSetChanged();
                                progressBarOdau.setVisibility(View.GONE);
                            }
                        }
                    });
                }


            }
        };

        nestScrollViewOdau.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldscrollX, int oldscrollY) {

                // Kiểm tra xem trong NestedScrollView còn item ko - dòng if bên dưới có nghĩa là còn item
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY >= (v.getChildAt(v.getChildCount() - 1)).getMeasuredHeight() -
                            v.getMeasuredHeight()) {

                        itemdaco += 3;

                        // Dòng này chạy trước dòng "OdauInterface odauInterface = new OdauInterface()"
                        quanAnModel.getDanhSachQuanAn(odauInterface, vitrihientai, itemdaco, itemdaco - 3);
                    }
                }
            }
        });

        // Dòng này chạy trước dòng "OdauInterface odauInterface = new OdauInterface()"
        quanAnModel.getDanhSachQuanAn(odauInterface, vitrihientai, itemdaco, 0);
    }
}
