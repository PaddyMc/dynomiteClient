package com.org.hhh.id;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    private UserRegisterTask mRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button mRegisterButton = (Button) findViewById(R.id.register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();

            }
        });


    }


    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        //mEmailView.setError(null);
        //mPasswordView.setError(null);

        String email = "a";//mEmailView.getText().toString();
        String password = "a";//mPasswordView.getText().toString();

        mRegisterTask = new UserRegisterTask(email, password);
        mRegisterTask.execute((Void) null);

    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private JSONObject jsonData;
        private int userID;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            String url = "http://192.168.0.18:8000/hope/";
            postUserToDB(url);
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            if (success) {
                // Write success story
            }
        }


        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }



        private void postUserToDB(String url){

            try {
                JSONObject newJSONObject = new JSONObject();;

                newJSONObject.put("first_name", "s");
                newJSONObject.put("last_name", "s");
                newJSONObject.put("password", "S");
                newJSONObject.put("date_of_birth", "11-11-1111");
                newJSONObject.put("pictureURL", "s");

                Log.d("AAAAAAAAAAAAAAAAAAAAAA", newJSONObject.toString());

                URL urlConnect = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) urlConnect.openConnection();

                urlConnection.setRequestProperty("Authorization", "Basic cGE6cGF0Y2hlc21jaGFsZQ==");

                urlConnection.setRequestMethod("POST");
//                urlConnection.setDoOutput(true);

                byte[] out =  newJSONObject.toString().getBytes("UTF-8");
                // int length = out.length;

                //urlConnection.setFixedLengthStreamingMode(length);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.connect();
                OutputStream os = urlConnection.getOutputStream();
                os.write(out);


                Log.d("ZZZZZZZZZZZZZZZZ", newJSONObject.toString());

                int status = urlConnection.getResponseCode();
                Log.d("BBBBBBBBBBBBBBBBBB0", String.valueOf(status));


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("EEEEEEEEEEEEEEEEEEEEEEE", "EEEEEEEEEEEEEEEEE1");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("EEEEEEEEEEEEEEEEEEEEEEE", "EEEEEEEEEEEEEEEEE2");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("EEEEEEEEEEEEEEEEEEEEEEE", "EEEEEEEEEEEEEEEEE3");
            }
        }

    }

}
