package com.khoinguyen.foody2.Controller;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Adapters.AdapterThucDon;
import com.khoinguyen.foody2.Controller.Interfaces.ThucDonInterface;
import com.khoinguyen.foody2.Model.ThucDonModel;

import java.util.List;

public class ThucDonController {

    ThucDonModel thucDonModel;

    public ThucDonController () {
        thucDonModel = new ThucDonModel();
    }

    public void  getDanhSachThucDonQuanAnTheoMa (Context context, String maquanan, RecyclerView recyclerThucDon) {
        recyclerThucDon.setLayoutManager(new LinearLayoutManager(context));

        ThucDonInterface thucDonInterface = new ThucDonInterface() {
            @Override
            public void getThucDonThanhCong(List<ThucDonModel> thucDonModelList) {
                AdapterThucDon adapterThucDon = new AdapterThucDon(context, thucDonModelList);
                recyclerThucDon.setAdapter(adapterThucDon);
                adapterThucDon.notifyDataSetChanged();

            }
        };

        // Dòng này sẽ chạy trước dòng thucDonInterface
        thucDonModel.getDanhSachThucDonQuanAnTheoMa(maquanan, thucDonInterface);
    }
}
