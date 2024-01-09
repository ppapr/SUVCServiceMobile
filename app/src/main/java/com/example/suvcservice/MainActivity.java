package com.example.suvcservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.suvcservice.CommonComponents.DataProvider;
import com.example.suvcservice.CommonEmployeeActivities.CRequestsActivity;
import com.example.suvcservice.ITEmployeeActivities.ITRequestActivity;
import com.example.suvcservice.Objects.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnAuthorization = findViewById(R.id.buttonAuthorization);
        btnAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText loginText = findViewById(R.id.textLogin);
                EditText passwordText = findViewById(R.id.textPassword);
                String login = loginText.getText().toString();
                String password = passwordText.getText().toString();
                new authorizationUser(MainActivity.this).execute(getString(R.string.api_link)
                        + "api/Users",login,password);
            }
        });
    }
    private class authorizationUser extends AsyncTask<String, Void, String> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public authorizationUser(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Загрузка...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0] + "?login=" + strings[1] + "&password=" + strings[2]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("ServerResponse", "Response: " + result.toString());
                    return result.toString();
                } else {
                    return "HTTP Error Code: " + responseCode;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException: " + e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (new DataProvider().checkResult(result)) {
                try {
                    JSONObject jsonObjectUser = new JSONObject(result);
                    int id = jsonObjectUser.getInt("ID");
                    int idRole =jsonObjectUser.getInt("IDRole");
                    if (id!=0) {
                        Users.user = new Users(
                                id,
                                jsonObjectUser.getString("Name"),
                                jsonObjectUser.getString("Surname"),
                                jsonObjectUser.getString("MiddleName"),
                                jsonObjectUser.getString("Login"),
                                jsonObjectUser.getString("Password"),
                                idRole
                        );
                        Users.saveSystemBasket(MainActivity.this);
                        if (idRole == 2){
                        Intent intent = new Intent(MainActivity.this, ITRequestActivity.class);
                        startActivity(intent);
                        }
                        else if (idRole == 3){
                            Intent intent = new Intent(MainActivity.this, CRequestsActivity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Неверный логин или пароль",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Неверный логин или пароль",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Неверный логин или пароль",Toast.LENGTH_LONG).show();
            }
            if (mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Успешная авторизация!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}