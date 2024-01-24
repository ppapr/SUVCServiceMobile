package com.example.suvcservice.CommonComponents;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

public class NotificationService extends IntentService {

    private Handler handler;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Периодическая проверка новых заявок с использованием GetRequest
        String connapi = getString(R.string.api_link);
        handler.post(() -> new GetRequest(getApplicationContext()).execute(connapi + "api/Requests",
                String.valueOf(Users.user.getId())));

        // Заснуть на какое-то время (например, 1 минуту) перед следующей проверкой
        try {
            Thread.sleep(6000);
            Log.d("AfterThread", "New service");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Перезапустить службу для следующей проверки
        Intent restartServiceIntent = new Intent(this, NotificationService.class);
        startService(restartServiceIntent);
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Создайте канал уведомлений (требуется для Android 8.0 и выше)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default_channel_id", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Постройте уведомление
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default_channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_arrow_left)
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        // Отправьте уведомление
        notificationManager.notify(0, notification);
    }
}
