package com.example.suvcservice.ITEmployeeActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suvcservice.Adapters.RequestsAdapter;
import com.example.suvcservice.CommonComponents.GetRequest;
import com.example.suvcservice.CommonComponents.NotificationService;
import com.example.suvcservice.CommonComponents.OnDataUpdateListener;
import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import java.util.ArrayList;
import java.util.List;

public class ITRequestActivity extends AppCompatActivity implements OnDataUpdateListener {

    private ProgressDialog mProgressDialog;
    ListView lvRequests;
    private List<Requests> mRequests;
    private RequestsAdapter mRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itrequest);
        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        lvRequests = findViewById(R.id.listRequests);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Загрузка данных...");
        mProgressDialog.setCancelable(false);

        mRequests = new ArrayList<>();
        mRequestAdapter = new RequestsAdapter(this, mRequests);

        lvRequests.setAdapter(mRequestAdapter);
        String connapi = getString(R.string.api_link);
        new GetRequest(ITRequestActivity.this, lvRequests, mProgressDialog, ITRequestActivity.this)
                .execute(connapi + "api/Requests",
                String.valueOf(Users.user.getId()));

        LinearLayout btnStartPrograms = findViewById(R.id.btnProgramsPage);
        btnStartPrograms.setOnClickListener(view -> {
            Intent intent = new Intent(ITRequestActivity.this, ITSpecializationsActivity.class);
            startActivity(intent);
            finish();
        });
        LinearLayout btnStartProfile = findViewById(R.id.btnProfilePage);
        btnStartProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ITRequestActivity.this, ITProfileActivity.class);
            startActivity(intent);
            finish();
        });
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }

    @Override
    public void onDataUpdated(List<Requests> updatedData) {
        Log.d("ITRequestActivity", "onDataUpdated: Data updated. Size: " + updatedData.size());
        // Обновление ListView с новыми данными
        mRequests.clear();
        mRequests.addAll(updatedData);
        mRequestAdapter.notifyDataSetChanged();
    }
}