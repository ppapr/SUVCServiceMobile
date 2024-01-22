package com.example.suvcservice.CommonComponents;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.List;

public class GetRequest extends AsyncTask<String, Void, String> {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private List<Requests> mPreviousRequests;
    private RequestsAdapter mRequestAdapter;
    private List<Requests> mRequests;
    private ListView mListView;

    public GetRequest(Context context, ListView listView) {
        mContext = context;
        mListView = listView;
    }

    public GetRequest(Context context, ListView listView, ProgressDialog progressDialog) {
        mContext = context;
        mListView = listView;
        mProgressDialog = progressDialog;
    }

    public GetRequest(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

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
                        mRequests.add(request);
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
                checkForNewRequests();
            });
        }
    }

    private void checkForNewRequests() {
        if (mPreviousRequests != null) {
            for (Requests newRequest : mRequests) {
                if (!mPreviousRequests.contains(newRequest)) {
                    showNotification("Новая заявка", "Получена новая заявка");
                    break;
                }
            }
        }
    }
    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channel_id")
                .setSmallIcon(R.drawable.ic_arrow_left)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}
