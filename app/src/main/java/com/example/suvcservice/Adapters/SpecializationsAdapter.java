package com.example.suvcservice.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suvcservice.ITEmployeeActivities.ITProgramsActivity;
import com.example.suvcservice.Objects.Specializations;
import com.example.suvcservice.R;

import java.util.List;

public class SpecializationsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Specializations> mSpecList;

    public SpecializationsAdapter(Context mContext, List<Specializations> mSpecList) {
        this.mContext = mContext;
        this.mSpecList = mSpecList;
    }

    @Override
    public int getCount() {
        return mSpecList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSpecList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSpecList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_specialization, null);
        TextView txtNameSpec = v.findViewById(R.id.textNameSpecialization);

        Specializations specializations = mSpecList.get(position);
        txtNameSpec.setText(specializations.getNameSpecialization());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ITProgramsActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Specialization", specializations);
                mContext.startActivity(intent);
            }
        });

        return v;
    }
}
