package com.khoinguyen.foody2.Controller;

import android.util.Log;
import android.widget.TextView;

import com.khoinguyen.foody2.Controller.Interfaces.ChiTietQuanAnInterface;
import com.khoinguyen.foody2.Model.WifiQuanAnModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChiTietQuanAnController {

    WifiQuanAnModel wifiQuanAnModel;
    List<WifiQuanAnModel> wifiQuanAnModelList;

    public ChiTietQuanAnController() {
        wifiQuanAnModel = new WifiQuanAnModel();
        wifiQuanAnModelList = new ArrayList<>();
    }

    public void HienThiDanhSachQuanAn (String maquanan, TextView txtTenWifi,
                                       TextView txtMatKhauWifi, TextView txtNgayDangWifi) {

        ChiTietQuanAnInterface chiTietQuanAnInterface = new ChiTietQuanAnInterface() {
            @Override
            public void HienThiDanhSachWifi(WifiQuanAnModel wifiQuanAnModel) {
                wifiQuanAnModelList.add(wifiQuanAnModel);
                txtTenWifi.setText(wifiQuanAnModel.getTen());
                txtMatKhauWifi.setText(wifiQuanAnModel.getMatkhau());
                txtNgayDangWifi.setText(wifiQuanAnModel.getNgaydang());

            }
        };

        wifiQuanAnModel.LayDanhSachWifiQuanAn(maquanan, chiTietQuanAnInterface);


    }
}
