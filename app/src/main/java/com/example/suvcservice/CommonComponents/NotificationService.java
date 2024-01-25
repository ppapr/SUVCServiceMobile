package com.example.suvcservice.CommonComponents;

import static android.provider.Settings.System.getString;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;


public class NotificationService extends JobIntentService {

    private Handler handler;

    public NotificationService() {
        super();
    }

    @Override
    protected void onHandleWork(Intent intent) {
        handler = new Handler(Looper.getMainLooper());

        handler.post(() -> new GetRequest(getApplicationContext()).execute(
                getString(R.string.api_link) + "api/Requests",
                String.valueOf(Users.user.getId())
        ));

        handler.postDelayed(() -> NotificationService.enqueueWork(getApplicationContext(), intent), 8000);
        Log.d("NOTIFICATIONTEST", "TEST");
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, 0, work);
    }
}