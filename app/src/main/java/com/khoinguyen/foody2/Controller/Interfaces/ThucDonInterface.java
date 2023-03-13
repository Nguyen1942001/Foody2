package com.khoinguyen.foody2.Controller.Interfaces;

import com.khoinguyen.foody2.Model.ThucDonModel;

import java.util.List;

public interface ThucDonInterface {

    // Trả về danh sách thực đơn của 1 quán ăn (trong mỗi thực đơn chứa nhiều món ăn)
    public void getThucDonThanhCong (List<ThucDonModel> thucDonModelList);

}
