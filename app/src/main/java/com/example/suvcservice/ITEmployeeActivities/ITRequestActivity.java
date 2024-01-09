package com.example.suvcservice.ITEmployeeActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suvcservice.Adapters.RequestsAdapter;
import com.example.suvcservice.CommonComponents.DataProvider;
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

    private RequestsAdapter mRequestAdapter;
    private List<Requests> mRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itrequest);
        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        ListView lvRequests = findViewById(R.id.listRequests);
        mRequests = new ArrayList<>();
        mRequestAdapter = new RequestsAdapter(ITRequestActivity.this, mRequests);
        lvRequests.setAdapter(mRequestAdapter);
        String connapi = getString(R.string.api_link);
        new getRequest(ITRequestActivity.this).execute(connapi + "api/Requests", String.valueOf(Users.user.getId()));

        LinearLayout btnStartPrograms = findViewById(R.id.btnProgramsPage);
        btnStartPrograms.setOnClickListener(view -> {
            Intent intent = new Intent(ITRequestActivity.this, ITSpecializationsActivity.class);
            startActivity(intent);
        });
        LinearLayout btnStartProfile = findViewById(R.id.btnProfilePage);
        btnStartProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ITRequestActivity.this, ITProfileActivity.class);
            startActivity(intent);
        });
        ImageView btnArrow = findViewById(R.id.imageArrow);
        btnArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ITRequestActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private class getRequest extends AsyncTask<String, Void, String> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public getRequest(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Загрузка данных...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                // Формирование ссылки
                URL url = new URL(strings[0] + "?userExecutor=" + strings[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                // Получение данных
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                // Отправка на парсинг полученого ответа
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Ошибка подключения
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
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();

            }
        }
    }
}