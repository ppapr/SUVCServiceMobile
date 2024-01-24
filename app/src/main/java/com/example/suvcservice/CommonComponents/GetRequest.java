package com.example.suvcservice.CommonComponents;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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
    private OnDataUpdateListener onDataUpdateListener;


    public GetRequest(Context context, ListView listView, ProgressDialog progressDialog) {
        mContext = context;
        mListView = listView;
        mProgressDialog = progressDialog;
    }

    public GetRequest(Context context, ListView listView, ProgressDialog progressDialog, OnDataUpdateListener listener) {
        mContext = context;
        mListView = listView;
        mProgressDialog = progressDialog;
        onDataUpdateListener = listener;
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
                    if (request.getIDStatus() != 3 && !isRequestProcessed(request.getID())) {
                        mRequests.add(request);
                        markRequestAsProcessed(request.getID());
                        updatedData.add(request);
                        Log.d("GetRequest", "New request added. Sending notification.");
                        sendNotification("Новая заявка", "Поступила новая заявка");
                    } else {
                        Log.d("GetRequest", "Request already processed or has status 3.");
                    }
                }
                mRequestAdapter.notifyDataSetChanged();
                if (onDataUpdateListener != null) {
                    // Вызовите onDataUpdated на главном потоке
                    ((Activity) mContext).runOnUiThread(() -> {
                        onDataUpdateListener.onDataUpdated(updatedData);
                    });
                }
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
        Log.d("NotificationService", "Sending notification: " + title + " - " + message);
        Intent notificationIntent = new Intent(mContext, ITRequestActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_arrow_left)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
