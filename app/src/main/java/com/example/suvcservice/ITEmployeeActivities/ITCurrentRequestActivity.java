package com.example.suvcservice.ITEmployeeActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suvcservice.CommonEmployeeActivities.CRequestsActivity;
import com.example.suvcservice.Objects.Requests;
import com.example.suvcservice.Objects.Users;
import com.example.suvcservice.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ITCurrentRequestActivity extends AppCompatActivity {
    boolean isExecute;
    Requests currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itcurrent_request);
        ImageView btnArrow = findViewById(R.id.imageArrow);
        currentRequest = getIntent().getParcelableExtra("Request");

        EditText txtDateRequest = findViewById(R.id.textDateRequest);
        EditText txtDescRequest = findViewById(R.id.textDescRequest);
        EditText txtEquipment = findViewById(R.id.textEquipmentRequest);
        EditText txtLocation = findViewById(R.id.textLocationRequest);

        TextView txtCurrentUser = findViewById(R.id.textViewCurrentUser);
        txtCurrentUser.setText("Заявка: " + currentRequest.getUserRequestName());

        Button btnActionRequest = findViewById(R.id.buttonActionRequest);
        Button btnAddSpare = findViewById(R.id.buttonAddSpare);

        txtDateRequest.setText(currentRequest.getDateCreateRequest().toString().split("T")[0]);
        txtDescRequest.setText(currentRequest.getDescription());
        txtEquipment.setText(currentRequest.getEquipmentName());
        txtLocation.setText(currentRequest.getLocation());

        String equipmentLocation = currentRequest.getLocation();
        char equipmentFloor = equipmentLocation.charAt(1);

        LinearLayout linearLayout = findViewById(R.id.interactiveMap);

        if (isValidLocation(equipmentLocation)) {
            String floor = String.valueOf(equipmentFloor) + "2";
            addImageView(linearLayout, "up2.png");
            addImageView(linearLayout, floor + "/" + equipmentLocation + ".png");
        } else {
            addImageView(linearLayout, "up.png");
            addImageView(linearLayout, equipmentFloor + "/" + equipmentLocation + ".png");
        }

        TextView textBlockAction = findViewById(R.id.textAction);
        textBlockAction.setText("Подняться на " + equipmentFloor + " этаж, затем зайти в кабинет " + equipmentLocation);

        int currentStatus = currentRequest.getIDStatus();
        if (currentStatus == 1) {
            btnActionRequest.setText("Приступить к выполнению");
            isExecute = false;
        } else if (currentStatus == 2) {
            btnActionRequest.setText("Отметить выполненной");
            isExecute = true;
        } else if (currentStatus == 3) {
            btnActionRequest.setText("Заявка выполнена!");
            int grayColor = ContextCompat.getColor(this, R.color.gray);
            ColorStateList grayColorStateList = ColorStateList.valueOf(grayColor);
            btnActionRequest.setBackgroundTintList(grayColorStateList);
            btnActionRequest.setEnabled(false);
            btnAddSpare.setVisibility(View.INVISIBLE);
        }
        if(Users.user.getIDRole() == 3){
            btnActionRequest.setVisibility(View.INVISIBLE);
            btnAddSpare.setVisibility(View.INVISIBLE);
            btnArrow.setOnClickListener(view -> {
                Intent intent = new Intent(ITCurrentRequestActivity.this, CRequestsActivity.class);
                startActivity(intent);
                finish();
            });
        }
        else{
            btnArrow.setOnClickListener(view -> {
                Intent intent = new Intent(ITCurrentRequestActivity.this, ITRequestActivity.class);
                startActivity(intent);
                finish();
            });
        }

        btnActionRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExecute) {

                    UpdateRequestStatusTask updateStatusTask = new
                            UpdateRequestStatusTask(ITCurrentRequestActivity.this, currentRequest.getID(), 2);
                    updateStatusTask.execute();
                } else if (isExecute) {
                    UpdateRequestStatusTask updateStatusTask = new
                            UpdateRequestStatusTask(ITCurrentRequestActivity.this, currentRequest.getID(), 3);
                    updateStatusTask.execute();

                }
            }
        });
        btnAddSpare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ITCurrentRequestActivity.this, ITAddSpareRequest.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Request", currentRequest);
                startActivity(intent);
            }
        });
    }

    private void addImageView(LinearLayout linearLayout, String imageName) {
        ImageView imageView = new ImageView(this);
        try {
            InputStream inputStream = getAssets().open("InteractiveMap/" + imageName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    900,
                    900
            );
            layoutParams.setMargins(30, 0, 30, 0);
            linearLayout.addView(imageView, layoutParams);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidLocation(String location) {
        String[] validLocations = {"1502", "1503", "1504", "1505", "1506", "1507", "1509", "1511", "1513",
                "1402", "1403", "1404", "1405", "1406", "1407", "1409", "1411", "1413"};

        for (String validLocation : validLocations) {
            if (validLocation.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public class UpdateRequestStatusTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private ProgressDialog mProgressDialog;
        private int requestID;
        private int newStatusID;

        public UpdateRequestStatusTask(Context context, int requestID, int newStatusID) {
            mContext = context;
            this.requestID = requestID;
            this.newStatusID = newStatusID;
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
                String connapi = mContext.getString(R.string.api_link);
                URL url = new URL(connapi + "api/Requests/" + requestID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("ID", requestID);
                jsonParam.put("Description", currentRequest.getDescription());
                jsonParam.put("DateCreateRequest", currentRequest.getDateCreateRequest());
                jsonParam.put("DateExecuteRequest", currentRequest.getDateExecuteRequest());
                jsonParam.put("IDPriority", currentRequest.getIDPriority());
                jsonParam.put("IDEquipment", currentRequest.getIDEquipment());
                jsonParam.put("IDUserRequest", currentRequest.getIDUserRequest());
                jsonParam.put("IDExecutorRequest", currentRequest.getIDExecutorRequest());
                jsonParam.put("IDStatus", newStatusID);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("RESPONSE", response.toString());
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
                Toast.makeText(mContext, "Статус заявки успешно обновлен!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ITCurrentRequestActivity.this, ITRequestActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}