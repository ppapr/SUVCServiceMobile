package com.example.suvcservice.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.suvcservice.Objects.Equipment;
import com.example.suvcservice.R;

import org.w3c.dom.Text;

import java.util.List;

public class EquipmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<Equipment> mEquipmentList;

    public EquipmentAdapter(Context mContext, List<Equipment> mEquipmentList) {
        this.mContext = mContext;
        this.mEquipmentList = mEquipmentList;
    }

    @Override
    public int getCount() {
        return mEquipmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEquipmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEquipmentList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_equipment, null);

        TextView txtNetwork = v.findViewById(R.id.textNetworkName);
        TextView txtEquipment = v.findViewById(R.id.textEquipmentName);
        TextView txtDesc = v.findViewById(R.id.textDescEquipment);
        TextView txtInventory = v.findViewById(R.id.textInventoryName);

        Equipment equipment = mEquipmentList.get(position);

        txtNetwork.setText(equipment.getNetworkName());
        txtEquipment.setText(equipment.getEquipmentName());
        txtDesc.setText(equipment.getEquipmentDescription());
        txtInventory.setText(equipment.getInventoryName());

        return v;
    }
}
