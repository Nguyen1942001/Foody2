package com.khoinguyen.foody2.Controller;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Adapters.AdapterDanhSachWifi;
import com.khoinguyen.foody2.Controller.Interfaces.ChiTietQuanAnInterface;
import com.khoinguyen.foody2.Model.WifiQuanAnModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;

public class CapNhatWifiController {
    WifiQuanAnModel wifiQuanAnModel;
    Context context;
    List<WifiQuanAnModel> wifiQuanAnModelList;

    public CapNhatWifiController (Context context) {
        this.context = context;
        wifiQuanAnModel = new WifiQuanAnModel();
    }

    public void HienThiDanhSachWifi (String maquanan, RecyclerView recyclerView, ProgressBar progressCapNhatWifi) {
        wifiQuanAnModelList = new ArrayList<>();
        progressCapNhatWifi.setVisibility(View.VISIBLE);

        ChiTietQuanAnInterface chiTietQuanAnInterface = new ChiTietQuanAnInterface() {
            @Override
            public void HienThiDanhSachWifi(WifiQuanAnModel wifiQuanAnModel) {
                wifiQuanAnModelList.add(wifiQuanAnModel);
                AdapterDanhSachWifi adapterDanhSachWifi = new AdapterDanhSachWifi(context,
                        R.layout.layout_wifi_chitietquanan, wifiQuanAnModelList);
                recyclerView.setAdapter(adapterDanhSachWifi);
                adapterDanhSachWifi.notifyDataSetChanged();
                progressCapNhatWifi.setVisibility(View.GONE);
            }
        };

        wifiQuanAnModel.LayDanhSachWifiQuanAn(maquanan, chiTietQuanAnInterface);
    }

    public void ThemWifi(Context context, WifiQuanAnModel wifiQuanAnModel, String maquanan) {
        wifiQuanAnModel.ThemWifiQuanAn(context, wifiQuanAnModel, maquanan);
    }
}
