package com.example.suvcservice.CommonEmployeeActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suvcservice.Adapters.EquipmentAdapter;
import com.example.suvcservice.CommonComponents.DataProvider;
import com.example.suvcservice.Objects.Equipment;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CAddRequestActivity extends AppCompatActivity {

    EditText descRequest;
    List<Equipment> equipmentList;
    EquipmentAdapter equipmentAdapter;
    int selectedEquipment;
    boolean isRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadd_request);

        ImageView btnArrow = findViewById(R.id.imageArrow);
        btnArrow.setOnClickListener(view -> {
            Intent intent = new Intent(CAddRequestActivity.this, CRequestsActivity.class);
            startActivity(intent);
            finish();
        });

        TextView txtName = findViewById(R.id.textViewCurrentUser);
        txtName.setText(Users.user.getSurname() + " " + Users.user.getName() + " " + Users.user.getMiddleName());

        descRequest = findViewById(R.id.textDescRequest);
        isRequest = true;
        CheckBox cbAsk = findViewById(R.id.cbAsk);
        Spinner equipmentSpinner = findViewById(R.id.spinnerEquipments);
        cbAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAsk.isChecked()) {
                    isRequest = false;
                    equipmentSpinner.setVisibility(View.INVISIBLE);
                } else {
                    isRequest = true;
                    equipmentSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        equipmentList = new ArrayList<>();
        equipmentAdapter = new EquipmentAdapter(CAddRequestActivity.this, equipmentList);
        equipmentSpinner.setAdapter(equipmentAdapter);

        String connapi = getString(R.string.api_link);
        new getEquipments().execute(connapi + "api/Equipments?user=" + Users.user.getId());

        equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Equipment currentEquipment = equipmentList.get(position);
                selectedEquipment = currentEquipment.getID();
                Log.d("ItemSelected", "Position: " + position + ", Selected Equipment ID: " + selectedEquipment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button btnAddRequest = findViewById(R.id.buttonAddRequest);
        btnAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddRequest(CAddRequestActivity.this).execute();
            }
        });
    }

    public class AddRequest extends AsyncTask<Void, Void, String> {

        private ProgressDialog mProgressDialog;
        private Context mContext;

        public AddRequest(Context context) {
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
        protected String doInBackground(Void... voids) {
            try {
                String connapi = getString(R.string.api_link);
                URL url = new URL(connapi + "api/Requests");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                LocalDate executeDefault = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    executeDefault = LocalDate.of(1, 1, 1);
                }
                int insertEquipment = isRequest ? selectedEquipment : 61;
                JSONObject requestJson = new JSONObject();
                requestJson.put("Description", descRequest.getText());
                requestJson.put("DateCreateRequest", currentDate);
                requestJson.put("DateExecuteRequest", executeDefault);
                requestJson.put("IDStatus", 1);
                requestJson.put("IDPriority", 2);
                requestJson.put("IDEquipment", insertEquipment);
                requestJson.put("IDUserRequest", Users.user.getId());
                requestJson.put("IDExecutorRequest", 10);
                Log.d("JSON Request", "Request JSON: " + requestJson.toString());
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(requestJson.toString());
                writer.flush();
                writer.close();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Log.d("Server Response", "Response: " + response.toString());
                } else {
                    // Request failed, handle accordingly
                    Log.e("AddRequest", "HTTP response code: " + responseCode);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("AddSparesEquipment", "MalformedURLException: " + e.getMessage());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("AddSparesEquipment", "IOException or JSONException: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "Заявка успешно добавлена!", Toast.LENGTH_SHORT).show();
                descRequest.setText("");
            }
        }
    }

    private class getEquipments extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (new DataProvider().checkResult(result)) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONArray jsonArrayEquipment = jsonArray;
                    for (int i = 0; i < jsonArrayEquipment.length(); i++) {
                        JSONObject jsonObjectEquipment = jsonArrayEquipment.getJSONObject(i);
                        Equipment equipment = new Equipment(
                                jsonObjectEquipment.getInt("ID"),
                                jsonObjectEquipment.getString("EquipmentName"),
                                jsonObjectEquipment.getString("EquipmentDescription"),
                                jsonObjectEquipment.getString("NetworkName"),
                                jsonObjectEquipment.getString("InventoryName"),
                                jsonObjectEquipment.getInt("IDOwnerEquipment")
                        );
                        equipmentList.add(equipment);
                    }
                    equipmentAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}