package com.example.suvcservice.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.suvcservice.ITEmployeeActivities.ITCurrentRequestActivity;
import com.example.suvcservice.ITEmployeeActivities.ITProgramsActivity;
import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import java.util.List;

public class RequestsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Requests> mRequestsList;

    public RequestsAdapter(Context mContext, List<Requests> mRequestsList) {
        this.mContext = mContext;
        this.mRequestsList = mRequestsList;
    }

    @Override
    public int getCount() {
        return mRequestsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRequestsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRequestsList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_request, null);

        TextView txtStatus = v.findViewById(R.id.textStatus);
        TextView txtPriority = v.findViewById(R.id.textPriority);
        TextView txtNameRequest = v.findViewById(R.id.textNameRequest);
        TextView txtDate = v.findViewById(R.id.textDate);
        TextView txtEquipment = v.findViewById(R.id.textEquipment);

        Requests request = mRequestsList.get(position);
        txtStatus.setText(request.getStatusName());
        txtPriority.setText(request.getPriorityName());
        txtEquipment.setText(request.getEquipmentName());
        txtNameRequest.setText(request.getUserRequestName());
        txtDate.setText(request.getDateCreateRequest().toString().split("T")[0]);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ITCurrentRequestActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Request", request);
                mContext.startActivity(intent);
            }
        });

        return v;
    }
}
