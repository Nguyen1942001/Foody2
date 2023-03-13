package com.khoinguyen.foody2.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Controller.Interfaces.ThucDonInterface;

import java.util.ArrayList;
import java.util.List;

public class ThucDonModel {
    String mathucdon, tenthucdon;
    List<MonAnModel> monAnModelList;

    public ThucDonModel () {

    }

    public String getMathucdon() {
        return mathucdon;
    }

    public void setMathucdon(String mathucdon) {
        this.mathucdon = mathucdon;
    }

    public String getTenthucdon() {
        return tenthucdon;
    }

    public void setTenthucdon(String tenthucdon) {
        this.tenthucdon = tenthucdon;
    }

    public List<MonAnModel> getMonAnModelList() {
        return monAnModelList;
    }

    public void setMonAnModelList(List<MonAnModel> monAnModelList) {
        this.monAnModelList = monAnModelList;
    }

    public void getDanhSachThucDonQuanAnTheoMa (String maquanan, ThucDonInterface thucDonInterface) {
        DatabaseReference nodeThucDonQuanAn = FirebaseDatabase.getInstance().getReference().
                child("thucdonquanans").child(maquanan);
        nodeThucDonQuanAn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            // dataSnapshot: node "thucdonquanans"
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // thucDonModels: danh sách thực đơn của quán ăn (1 quán ăn có nhiều thực đơn)
                List<ThucDonModel> thucDonModels = new ArrayList<>();

                for (DataSnapshot valueThucDon : dataSnapshot.getChildren()) {
                    ThucDonModel thucDonModel = new ThucDonModel();

                    DatabaseReference nodeThucDon = FirebaseDatabase.getInstance().getReference().
                            child("thucdons").child(valueThucDon.getKey());

                    nodeThucDon.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        // dataSnapshotThucDon: node "thucdons"
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotThucDon) {
                            String mathucdon = dataSnapshotThucDon.getKey();

                            thucDonModel.setMathucdon(mathucdon);
                            thucDonModel.setTenthucdon(dataSnapshotThucDon.getValue(String.class));

                            // Danh sách món ăn theo từng loại thực đơn
                            List<MonAnModel> monAnModels = new ArrayList<>();

                            for (DataSnapshot valueMonAn : dataSnapshot.child(mathucdon).getChildren()) {
                                MonAnModel monAnModel = valueMonAn.getValue(MonAnModel.class);
                                monAnModel.setMamon(valueMonAn.getKey());
                                monAnModels.add(monAnModel);
                            }

                            // Thêm toàn bộ món ăn vào từng thực đơn tương ứng
                            thucDonModel.setMonAnModelList(monAnModels);

                            // Thêm các thực đơn vào danh sách thực đơn của quán ăn
                            thucDonModels.add(thucDonModel);

                            // Kích interface
                            thucDonInterface.getThucDonThanhCong(thucDonModels);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
