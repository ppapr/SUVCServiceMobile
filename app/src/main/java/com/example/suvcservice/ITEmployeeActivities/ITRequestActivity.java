package com.example.suvcservice.ITEmployeeActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suvcservice.Adapters.RequestsAdapter;
import com.example.suvcservice.CommonComponents.GetRequest;
import com.example.suvcservice.CommonComponents.NotificationService;
import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import java.util.ArrayList;
import java.util.List;

public class ITRequestActivity extends AppCompatActivity {

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
        new GetRequest(ITRequestActivity.this, lvRequests, mProgressDialog)
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
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String connapi = getString(R.string.api_link);
                        new GetRequest(ITRequestActivity.this, lvRequests)
                                .execute(connapi + "api/Requests",
                                        String.valueOf(Users.user.getId()));
                    }
                },
                new IntentFilter("IT_REQUEST_ACTIVITY_DATA_UPDATED")
        );
         Intent serviceIntent = new Intent(this, NotificationService.class);
        NotificationService.enqueueWork(this, serviceIntent);
    }
}