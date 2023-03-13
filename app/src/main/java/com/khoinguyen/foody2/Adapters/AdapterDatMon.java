package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Model.DatMonModel;
import com.khoinguyen.foody2.Model.MonAnModel;
import com.khoinguyen.foody2.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterDatMon extends RecyclerView.Adapter<AdapterDatMon.ViewHolderDatMon> {

    Context context;
    int resource;
    List<DatMonModel> datMonModelList;

    public AdapterDatMon (Context context, int resource, List<DatMonModel> datMonModelList) {
        this.context = context;
        this.resource = resource;
        this.datMonModelList = datMonModelList;
    }

    public class ViewHolderDatMon extends RecyclerView.ViewHolder {

        TextView txtTenMonAn, txtSoLuongMonAn, txtGiaMonAn;

        public ViewHolderDatMon(@NonNull View itemView) {
            super(itemView);

            txtTenMonAn = itemView.findViewById(R.id.txtTenMonAn);
            txtSoLuongMonAn = itemView.findViewById(R.id.txtSoLuongMonAn);
            txtGiaMonAn = itemView.findViewById(R.id.txtGiaMonAn);
        }
    }

    @NonNull
    @Override
    public AdapterDatMon.ViewHolderDatMon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_datmon, parent, false);
        AdapterDatMon.ViewHolderDatMon viewHolderDatMon = new ViewHolderDatMon(view);

        return viewHolderDatMon;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDatMon.ViewHolderDatMon holder, int position) {
        // Định dạng tiền Việt Nam
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        DatMonModel datMonModel = datMonModelList.get(position);
        long giatong = datMonModel.getGiatien() * datMonModel.getSoluong();

        holder.txtTenMonAn.setText(datMonModel.getTenmonan());
        holder.txtSoLuongMonAn.setText(datMonModel.getSoluong() + "");
        holder.txtGiaMonAn.setText(numberFormat.format(giatong));
    }

    @Override
    public int getItemCount() {
        return datMonModelList.size();
    }


}
