package com.example.suvcservice.ITEmployeeActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.example.suvcservice.Adapters.ProgramsAdapter;
import com.example.suvcservice.CommonComponents.DataProvider;
import com.example.suvcservice.Objects.Programs;
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

public class ITProgramsActivity extends AppCompatActivity {

    private ProgramsAdapter mProgramsAdapter;
    private List<Programs> mPrograms;
    Specializations currentSpecialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itprograms);

        currentSpecialization = getIntent().getParcelableExtra("Specialization");

        ListView lvPrograms = findViewById(R.id.listPrograms);
        mPrograms = new ArrayList<>();
        mProgramsAdapter = new ProgramsAdapter(ITProgramsActivity.this, mPrograms);
        lvPrograms.setAdapter(mProgramsAdapter);
        String connapi = getString(R.string.api_link);
        new ITProgramsActivity.getPrograms(ITProgramsActivity.this).execute(connapi
                            + "api/RegistryPrograms", String.valueOf(Users.user.getId()));

    }

    private class getPrograms extends AsyncTask<String, Void, String> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public getPrograms(Context context) {
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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (new DataProvider().checkResult(result)) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONArray jsonArrayPrograms = jsonArray;
                    for (int i = 0; i < jsonArrayPrograms.length(); i++) {
                        JSONObject jsonObjectPrograms = jsonArrayPrograms.getJSONObject(i);
                        Programs programs = new Programs(
                                jsonObjectPrograms.getInt("ID"),
                                jsonObjectPrograms.getString("NameProgram"),
                                jsonObjectPrograms.getString("VersionProgram"),
                                jsonObjectPrograms.getString("DescriptionProgram"),
                                jsonObjectPrograms.getInt("IDSpecialization")
                        );
                        if (programs.getIDSpecialization() == currentSpecialization.getID()) {
                            mPrograms.add(programs);
                        }
                    }
                    mProgramsAdapter.notifyDataSetChanged();
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