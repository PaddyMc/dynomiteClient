package com.org.hhh.id;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class LoginActivityId extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;



    JSONObject newJSONObject = null;
    String firstNameToAdd = "Hi";
    String lastNameToAdd =  "Cael";
    String passwordToAdd =  "pass123";
    String dobToAdd =       "2001-01-01";
    String picURLToAdd =    "inchbigger.jpg";

//    String jsonToAdd = "{\"first_name\":" + firstNameToAdd
//            + ",\"last_name\"" + lastNameToAdd
//            + ",\"password\"" + passwordToAdd
//            + ",\"dobToAdd\"" + dobToAdd
//            + ",\"pictureURL\"" + picURLToAdd
//            +"}";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_id);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private JSONObject jsonData;
        private int userID;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                postUserToDB();


                String url = "http://192.168.0.18:8000/idusers/";
                JSONObject hope = readJsonFromUrl(url);
                jsonData = hope;
                System.out.print(hope.getJSONArray("results").toString());


            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        public boolean checkAuthDetails() {
            boolean userAuth = false;
            try {
                int numOfUsers = jsonData.getInt("count");
                for (int i = 0; i < numOfUsers && !userAuth; i++) {
                    String email = jsonData.getJSONArray("results").optJSONObject(i).getString("first_name");
                    String password = jsonData.getJSONArray("results").optJSONObject(i).getString("password");
                    if (email.equals(mEmail) && password.equals(mPassword)) {
                        userID = jsonData.getJSONArray("results").optJSONObject(i).getInt("user_id");
                        userAuth = true;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return userAuth;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                 if (checkAuthDetails()) {
                    Log.d("Finished","Finsihed");
                     switchToUserProfile();
                }

                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                // Call check auth function
            }
        }


        private void switchToUserProfile() {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            String name = "";
            String dob = "";
            String pictureURL = "";
            try {
                int numOfUsers = jsonData.getInt("count");
                for (int i = 0; i < numOfUsers; i++) {
                    int tempID = jsonData.getJSONArray("results").optJSONObject(i).getInt("user_id");
                    Log.d("tempID", String.valueOf(tempID));
                    Log.d("userID", String.valueOf(userID));
                    if (tempID == userID) {
                        name = jsonData.getJSONArray("results").optJSONObject(i).getString("first_name");
                        Log.d("name", name);
                        dob = jsonData.getJSONArray("results").optJSONObject(i).getString("date_of_birth");
                        Log.d("dob", dob);
                        pictureURL = jsonData.getJSONArray("results").optJSONObject(i).getString("pictureURL");
                        Log.d("pictureURL", pictureURL);
                    }
                }

                intent.putExtra("name", name);
                intent.putExtra("dob", dob);
                intent.putExtra("pictureURL", pictureURL);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        private JSONObject readJsonFromUrl(String url) throws JSONException, IOException {
            JSONObject jsonData = null;


            try {
                URL urlConnect = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) urlConnect.openConnection(); // .openStream() returns openConnection().getInputStream()
                urlConnection.setRequestProperty("Authorization", "Basic cGE6cGF0Y2hlc21jaGFsZQ==");
                try{
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String jsonText = readBuffer(bufferedReader);
                    jsonData = new JSONObject(jsonText);
                    Log.d("Network", jsonData.getJSONArray("results").toString());
                    inputStream.close();
                } finally {
                    Log.d("Network", "Finished");
                    urlConnection.disconnect();
                }

            } catch(JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            return jsonData;
        }


        private String readBuffer(BufferedReader bufferedReader) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            int charFromBuffer;
            while ((charFromBuffer = bufferedReader.read()) != -1) {
                stringBuilder.append((char) charFromBuffer);
            }
            return stringBuilder.toString();
        }


        private void postUserToDB(){
            String url = "http://192.168.0.18:8000/hope/";

            try {
                Log.d("AAAAAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                newJSONObject = new JSONObject();

                newJSONObject.put("first_name", firstNameToAdd);
                newJSONObject.put("last_name", lastNameToAdd);
                newJSONObject.put("password", passwordToAdd);
                newJSONObject.put("date_of_birth", dobToAdd);
                newJSONObject.put("pictureURL", picURLToAdd);

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

