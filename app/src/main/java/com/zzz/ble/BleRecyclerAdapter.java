package com.zzz.ble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public class BleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<BleDevice> bleList;
    OnCellClickListener onCellClickListener;

    public BleRecyclerAdapter(Context context, List<BleDevice> bleList) {
        this.context = context;
        this.bleList = bleList;
    }

    static class BleViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvMac;

        BleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMac = itemView.findViewById(R.id.tv_mac);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BleViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_ble, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        BleViewHolder holder1 = (BleViewHolder)holder;
        holder1.tvName.setText(bleList.get(position).getName());
        holder1.tvMac.setText(bleList.get(position).getMac());
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCellClickListener.onCellClick(bleList.get(position));
            }
        });
    }

    public void replaceData(List<BleDevice> newList){
        bleList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bleList.size();
    }

    public void setOnCellClickListener(OnCellClickListener listener){
        this.onCellClickListener = listener;
    }

    interface OnCellClickListener{
        void onCellClick(BleDevice bleDevice);
    }
}
