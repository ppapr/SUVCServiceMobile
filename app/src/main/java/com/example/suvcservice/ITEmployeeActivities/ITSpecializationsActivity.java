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

import com.example.suvcservice.Adapters.SpecializationsAdapter;
import com.example.suvcservice.CommonComponents.DataProvider;
import com.example.suvcservice.MainActivity;
import com.example.suvcservice.Objects.Specializations;
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

public class ITSpecializationsActivity extends AppCompatActivity {

    private SpecializationsAdapter mSpecAdapter;
    private List<Specializations> mSpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itspecializations);
        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        LinearLayout btnStartRequests = findViewById(R.id.btnRequestsPage);
        btnStartRequests.setOnClickListener(view -> {
            Intent intent = new Intent(ITSpecializationsActivity.this, ITRequestActivity.class);
            startActivity(intent);
        });
        LinearLayout btnStartProfile = findViewById(R.id.btnProfilePage);
        btnStartProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ITSpecializationsActivity.this, ITProfileActivity.class);
            startActivity(intent);
        });
        ImageView btnArrow = findViewById(R.id.imageArrow);
        btnArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ITSpecializationsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ListView lvSpec = findViewById(R.id.listSpecializations);
        mSpec = new ArrayList<>();
        mSpecAdapter = new SpecializationsAdapter(ITSpecializationsActivity.this, mSpec);
        lvSpec.setAdapter(mSpecAdapter);
        new ITSpecializationsActivity.getSpecializations(ITSpecializationsActivity.this).execute("http://apisuvc.somee.com/" + "api/Specializations", String.valueOf(Users.user.getId()));

    }

    private class getSpecializations extends AsyncTask<String, Void, String> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public getSpecializations(Context context) {
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
                URL url = new URL(strings[0]);
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
                    JSONArray jsonArraySpec = jsonArray;
                    for (int i = 0; i < jsonArraySpec.length(); i++) {
                        JSONObject jsonObjectRequest = jsonArraySpec.getJSONObject(i);
                        Specializations specializations = new Specializations(
                                jsonObjectRequest.getInt("ID"),
                                jsonObjectRequest.getString("NameSpecialization")
                        );
                        mSpec.add(specializations);
                    }
                    mSpecAdapter.notifyDataSetChanged();
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