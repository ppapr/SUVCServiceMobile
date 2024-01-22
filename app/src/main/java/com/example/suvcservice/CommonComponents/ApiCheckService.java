package com.example.suvcservice.CommonComponents;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.suvcservice.CommonEmployeeActivities.CRequestsActivity;
import com.example.suvcservice.ITEmployeeActivities.ITRequestActivity;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ApiCheckService extends Service {

    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String connapi = getString(R.string.api_link);
                new GetRequest(ApiCheckService.this).execute(connapi + "api/Requests", String.valueOf(Users.user.getId()));
            }
        }, 0, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
