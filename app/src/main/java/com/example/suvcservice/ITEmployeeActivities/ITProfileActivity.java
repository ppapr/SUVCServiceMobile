package com.example.suvcservice.ITEmployeeActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ITProfileActivity extends AppCompatActivity {

    private RequestsAdapter mRequestAdapter;
    private List<Requests> mRequests;

    String name, surname, middlename, login, password;
    EditText tv_name, tv_surname, tv_middlename, tv_login, tv_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itprofile);
        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        tv_name = findViewById(R.id.textName);
        tv_surname = findViewById(R.id.textSurname);
        tv_middlename = findViewById(R.id.textMiddleName);
        tv_login = findViewById(R.id.textLogin);
        tv_password = findViewById(R.id.textPassword);

        tv_name.setText(Users.user.getName());
        tv_surname.setText(Users.user.getSurname());
        tv_middlename.setText(Users.user.getMiddleName());
        tv_login.setText(Users.user.getLogin());
        tv_password.setText(Users.user.getPassword());

        LinearLayout btnStartRequests = findViewById(R.id.btnRequestsPage);
        btnStartRequests.setOnClickListener(view -> {
            Intent intent = new Intent(ITProfileActivity.this, ITRequestActivity.class);
            startActivity(intent);
        });
        LinearLayout btnStartPrograms = findViewById(R.id.btnProgramsPage);
        btnStartPrograms.setOnClickListener(view -> {
            Intent intent = new Intent(ITProfileActivity.this, ITSpecializationsActivity.class);
            startActivity(intent);
        });
        ImageView btnArrow = findViewById(R.id.imageArrow);
        btnArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ITProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        String connapi = getString(R.string.api_link);
        ListView lvRequests = findViewById(R.id.listRequests);
        mRequests = new ArrayList<>();
        mRequestAdapter = new RequestsAdapter(ITProfileActivity.this, mRequests);
        lvRequests.setAdapter(mRequestAdapter);
        new ITProfileActivity.getRequest(ITProfileActivity.this)
                .execute(connapi + "api/Requests", String.valueOf(Users.user.getId()));

        Button btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditUserProfile(ITProfileActivity.this).execute();
                Users.user.setName(name);
                Users.user.setSurname(surname);
                Users.user.setMiddleName(middlename);
                Users.user.setLogin(login);
                Users.user.setPassword(password);
                Users.saveSystemBasket(ITProfileActivity.this);
            }
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
                        if (request.getIDStatus() == 3) {
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

    class EditUserProfile extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public EditUserProfile(Context context) {
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
        protected Void doInBackground(Void... voids) {
            try {
                String connapi = getString(R.string.api_link);
                URL url = new URL(connapi + "api/Users/" + Users.user.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                name = tv_name.getText().toString();
                surname = tv_surname.getText().toString();
                middlename = tv_middlename.getText().toString();
                login = tv_login.getText().toString();
                password = tv_password.getText().toString();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("ID", Users.user.getId());
                jsonParam.put("Name", name);
                jsonParam.put("Surname", surname);
                jsonParam.put("MiddleName", middlename);
                jsonParam.put("Login", login);
                jsonParam.put("Password", password);
                jsonParam.put("IDRole", Users.user.getIDRole());
                System.out.println("JSON:" + jsonParam.toString());
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    Log.d("RESPONSE", response.toString());
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                Toast.makeText(ITProfileActivity.this, "Данные успешно загружены!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}