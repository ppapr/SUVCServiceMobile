package com.example.suvcservice.ITEmployeeActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suvcservice.Adapters.RequestsAdapter;
import com.example.suvcservice.CommonComponents.ApiCheckService;
import com.example.suvcservice.CommonComponents.DataProvider;
import com.example.suvcservice.CommonComponents.GetRequest;
import com.example.suvcservice.MainActivity;
import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ITRequestActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itrequest);
        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        ListView lvRequests = findViewById(R.id.listRequests);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Загрузка данных...");
        mProgressDialog.setCancelable(false);

        String connapi = getString(R.string.api_link);
        new GetRequest(ITRequestActivity.this, lvRequests, mProgressDialog).execute(connapi + "api/Requests",
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
        Intent serviceIntent = new Intent(this, ApiCheckService.class);
        startService(serviceIntent);
    }
}