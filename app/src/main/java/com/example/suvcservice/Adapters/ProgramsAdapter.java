package com.example.suvcservice.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.suvcservice.Objects.Programs;
import com.example.suvcservice.R;

import org.w3c.dom.Text;

import java.util.List;

public class ProgramsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Programs> mProgramsList;

    public ProgramsAdapter(Context mContext, List<Programs> mProgramsList) {
        this.mContext = mContext;
        this.mProgramsList = mProgramsList;
    }

    @Override
    public int getCount() {
        return mProgramsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProgramsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProgramsList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_program, null);
        TextView txtNameProgram = v.findViewById(R.id.textNameProgram);
        TextView txtVersion = v.findViewById(R.id.textVersion);
        TextView txtDescription = v.findViewById(R.id.textDescriptionProgram);

        Programs programs = mProgramsList.get(position);
        txtDescription.setText(programs.getDescriptionProgram());
        txtNameProgram.setText(programs.getNameProgram());
        txtVersion.setText(programs.getVersionProgram());

        return v;
    }
}
