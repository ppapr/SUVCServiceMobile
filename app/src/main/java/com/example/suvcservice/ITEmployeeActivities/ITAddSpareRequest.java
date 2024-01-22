package com.example.suvcservice.ITEmployeeActivities;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.SparesEquipment;
import com.example.suvcservice.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ITAddSpareRequest extends AppCompatActivity {

    Requests currentRequest;
    EditText txtNameSpare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itadd_spare_request);

        ImageView btnArrow = findViewById(R.id.imageArrow);
        btnArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ITAddSpareRequest.this, ITCurrentRequestActivity.class);
            startActivity(intent);
            finish();
        });

        currentRequest = getIntent().getParcelableExtra("Request");

        TextView txtEquipment = findViewById(R.id.textNameEquipment);
        TextView txtCurrentUser = findViewById(R.id.textViewCurrentUser);

        txtCurrentUser.setText("Заявка: " + currentRequest.getUserRequestName());
        txtEquipment.setText(txtEquipment.getText() + " " + currentRequest.getEquipmentName());

        txtNameSpare = findViewById(R.id.textNameSpare);

        Button btnAddSpare = findViewById(R.id.buttonAddSpare);
        btnAddSpare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameSpare = String.valueOf(txtNameSpare.getText());
                if (txtNameSpare.getText() != null) {
                    SparesEquipment sparesEquipment = new SparesEquipment(nameSpare, currentRequest.getIDEquipment());

                    new AddSparesEquipment(ITAddSpareRequest.this).execute(sparesEquipment);
                } else {
                    Toast.makeText(ITAddSpareRequest.this, "Введите название запчасти!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class AddSparesEquipment extends AsyncTask<SparesEquipment, Void, String> {

        private ProgressDialog mProgressDialog;
        private Context mContext;

        public AddSparesEquipment(Context context) {
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
        protected String doInBackground(SparesEquipment... sparesEquipments) {
            try {
                String connapi = getString(R.string.api_link);
                URL url = new URL(connapi + "api/SparesEquipments");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject sparesEquipmentJson = new JSONObject();
                sparesEquipmentJson.put("SpareName", sparesEquipments[0].getSpareName());
                sparesEquipmentJson.put("IDEquipment", sparesEquipments[0].getIDEquipment());
                Log.d("JSON Request", "Request JSON: " + sparesEquipmentJson.toString());
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(sparesEquipmentJson.toString());
                writer.flush();
                writer.close();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "success";
                } else {
                    return "failure";
                }
            }catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("AddSparesEquipment", "MalformedURLException: " + e.getMessage());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("AddSparesEquipment", "IOException or JSONException: " + e.getMessage());
                }
            return "null";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "Запчасть успешно добавлена!", Toast.LENGTH_SHORT).show();
                txtNameSpare.setText("");
            }
        }
    }
}