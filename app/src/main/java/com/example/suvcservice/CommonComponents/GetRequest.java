package com.example.suvcservice.CommonComponents;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.suvcservice.Adapters.RequestsAdapter;
import com.example.suvcservice.ITEmployeeActivities.ITRequestActivity;
import com.example.suvcservice.Objects.Requests;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetRequest extends AsyncTask<String, Void, String> {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private RequestsAdapter mRequestAdapter;
    private List<Requests> mRequests;
    private ListView mListView;


    public GetRequest(Context context, ListView listView, ProgressDialog progressDialog) {
        mContext = context;
        mListView = listView;
        mProgressDialog = progressDialog;
    }

    public GetRequest(Context context, ListView listView) {
        mContext = context;
        mListView = listView;
    }


    public GetRequest(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedPreferences preferences = mContext.getSharedPreferences("ProcessedRequests", Context.MODE_PRIVATE);
        Set<String> processedRequests = preferences.getStringSet("processed_requests", new HashSet<>());

        mRequests = new ArrayList<>();
        mRequestAdapter = new RequestsAdapter(mContext, mRequests);

        if (mProgressDialog != null) {
            mProgressDialog.setMessage("Загрузка данных...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        if (mListView != null) {
            mListView.setAdapter(mRequestAdapter);
        }

    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0] + "?userExecutor=" + strings[1]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "null";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (new DataProvider().checkResult(result)) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                JSONArray jsonArrayRequests = jsonArray;
                List<Requests> updatedData = new ArrayList<>();
                for (int i = 0; i < jsonArrayRequests.length(); i++) {
                    JSONObject jsonObjectRequest = jsonArrayRequests.getJSONObject(i);
                    Requests request = new Requests(
                            jsonObjectRequest.getInt("ID"),
                            jsonObjectRequest.getString("Description"),
                            jsonObjectRequest.getString("DateCreateRequest"),
                            jsonObjectRequest.getString("DateExecuteRequest"),
                            jsonObjectRequest.getString("UserRequestName"),
                            jsonObjectRequest.getString("UserExecutorName"),
                            jsonObjectRequest.getString("StatusName"),
                            jsonObjectRequest.getString("PriorityName"),
                            jsonObjectRequest.getString("EquipmentName"),
                            jsonObjectRequest.getString("Location"),
                            jsonObjectRequest.getInt("IDStatus"),
                            jsonObjectRequest.getInt("IDPriority"),
                            jsonObjectRequest.getInt("IDEquipment"),
                            jsonObjectRequest.getInt("IDUserRequest"),
                            jsonObjectRequest.getInt("IDExecutorRequest")
                    );
                    if (request.getIDStatus() != 3) {
                        if (!isRequestProcessed(request.getID())) {
                            sendNotification("Новая заявка", "Заявка от: " + request.getUserRequestName());
                            Intent onDataUpdatedIntent = new Intent("IT_REQUEST_ACTIVITY_DATA_UPDATED");
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(onDataUpdatedIntent);
                        }
                        mRequests.add(request);
                        markRequestAsProcessed(request.getID());
                        // }
                    } else {
                        Log.d("GetRequest", "Request already processed or has status 3.");
                    }
                }
                mRequestAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(() -> {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private boolean isRequestProcessed(int requestId) {
        SharedPreferences preferences = mContext.getSharedPreferences("ProcessedRequests", Context.MODE_PRIVATE);
        Set<String> processedRequests = preferences.getStringSet("processed_requests", new HashSet<>());
        return processedRequests.contains(String.valueOf(requestId));
    }

    private void markRequestAsProcessed(int requestId) {
        SharedPreferences preferences = mContext.getSharedPreferences("ProcessedRequests", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> processedRequests = preferences.getStringSet("processed_requests", new HashSet<>());
        processedRequests.add(String.valueOf(requestId));
        editor.putStringSet("processed_requests", processedRequests);
        editor.apply();
    }

    private void sendNotification(String title, String message) {
        Log.d("GetRequest", "Sending notification: " + title + " - " + message);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default_channel_id", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(mContext, ITRequestActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, "default_channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_arrow_left)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(0, notification);
    }
}
