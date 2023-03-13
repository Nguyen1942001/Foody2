package com.khoinguyen.foody2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoinguyen.foody2.Model.ThucDonModel;
import com.khoinguyen.foody2.R;

import java.util.List;

public class AdapterThucDon extends RecyclerView.Adapter<AdapterThucDon.ViewHolderThucDon> {

    Context context;
    List<ThucDonModel> thucDonModels;

    public AdapterThucDon (Context context, List<ThucDonModel> thucDonModels) {
        this.context = context;
        this.thucDonModels = thucDonModels;
    }

    public class ViewHolderThucDon extends RecyclerView.ViewHolder {

        TextView txtTenThucDon;
        RecyclerView recyclerMonAn;

        public ViewHolderThucDon(@NonNull View itemView) {
            super(itemView);

            txtTenThucDon = itemView.findViewById(R.id.txtTenThucDon);
            recyclerMonAn = itemView.findViewById(R.id.recyclerMonAn);

        }
    }

    @NonNull
    @Override
    public AdapterThucDon.ViewHolderThucDon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_thucdon, parent, false);
        ViewHolderThucDon viewHolderThucDon = new ViewHolderThucDon(view);

        return viewHolderThucDon;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterThucDon.ViewHolderThucDon holder, int position) {
        ThucDonModel thucDonModel = thucDonModels.get(position);
        holder.txtTenThucDon.setText(thucDonModel.getTenthucdon());

        // Set adapter món ăn cho từng thực đơn
        holder.recyclerMonAn.setLayoutManager(new LinearLayoutManager(context));
        AdapterMonAn adapterMonAn = new AdapterMonAn(context, thucDonModel.getMonAnModelList());
        holder.recyclerMonAn.setAdapter(adapterMonAn);
        adapterMonAn.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return thucDonModels.size();
    }


}
