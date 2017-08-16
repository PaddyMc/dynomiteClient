package com.org.hhh.id;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private UserRegisterTask mRegisterTask = null;


    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    String filename = "";
    Bitmap picToUpload = null;

    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mDateOfBirthView;

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

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        Button capture = (Button) findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                count++;
                String file = dir+count+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                }

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/" + count + ".jpg";
            String photoPath = filename;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(bitmap);
            Log.d("CameraDemo", "Pic saved");
            picToUpload = bitmap;
        }
    }


    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }
        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mEmailView = (EditText) findViewById(R.id.email);
        mDateOfBirthView = (EditText) findViewById(R.id.dateOfBirth);
        mPasswordView = (EditText) findViewById(R.id.password);

        //mEmailView.setError(null);
        //mPasswordView.setError(null);
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String dateOfBirth = mDateOfBirthView.getText().toString();
        String password = mPasswordView.getText().toString();

        mRegisterTask = new UserRegisterTask(firstName, lastName, email, dateOfBirth, password);
        mRegisterTask.execute((Void) null);

    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mFirstName;
        private final String mLastName;
        private final String mEmail;
        private final String mDateOfBirth;
        private final String mPassword;
        private final String mPictureURL;
        private JSONObject jsonData;
        private int userID;

        UserRegisterTask(String firstName, String lastName, String email, String dateOfBirth, String password) {
            mFirstName = firstName;
            mLastName = lastName;
            mEmail = email;
            mPassword = password;
            mDateOfBirth = dateOfBirth;
            mPictureURL = mFirstName+mLastName+".jpg";
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            String url = "http://192.168.0.17:8000/newidusers/";
            postUserToDB(url);
            postPicUserToDB(url);
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            if (success) {
                // Write success story
                //Intent intent = new Intent(getApplicationContext(), LoginActivityId.class);
                //startActivity(intent);
            }
        }


        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }



        private void postUserToDB(String url){

            try {
                JSONObject newJSONObject = new JSONObject();

                newJSONObject.put("first_name", mFirstName);
                newJSONObject.put("last_name", mLastName);
                newJSONObject.put("email", mEmail);
                newJSONObject.put("password", mPassword);
                newJSONObject.put("date_of_birth", mDateOfBirth);
                newJSONObject.put("pictureURL", mPictureURL);

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

        private void postPicUserToDB(String url) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            //InputStream inputStream = null;

            String twoHyphens = "--";
            String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
            String lineEnd = "\r\n";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            String filefield = "file";

            String[] q = filename.split("/");
            int idx = q.length - 1;

            File file = new File(filename);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                String pictureURL = "http://192.168.0.17:8000/upload/"+mPictureURL;

                URL picUrl = new URL(pictureURL);
                connection = (HttpURLConnection) picUrl.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
                connection.setRequestProperty("Authorization", "Basic cGE6cGF0Y2hlc21jaGFsZQ==");

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] +"\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while(bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //inputStream = connection.getInputStream();

                int status = connection.getResponseCode();
                Log.d("imagga", String.valueOf(status));

                /*if (status == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    inputStream.close();
                    connection.disconnect();
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    Log.i("imagga", response.toString());
                } else {
                    throw new Exception("Non ok response returned");
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
