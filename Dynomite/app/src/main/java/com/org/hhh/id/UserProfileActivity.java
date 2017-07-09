package com.org.hhh.id;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;


public class UserProfileActivity extends AppCompatActivity {
    private UserPictureTask mPictureTask = null;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String dob = intent.getStringExtra("dob");
        String pictureURL = intent.getStringExtra("pictureURL");

        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setText(name, TextView.BufferType.EDITABLE);

        EditText editText2 = (EditText)findViewById(R.id.editText2);
        editText2.setText(dob, TextView.BufferType.EDITABLE);

        imageView = (ImageView) findViewById(R.id.imageView);
        UserPictureTask userPictureTask = new UserPictureTask(pictureURL);
        userPictureTask.execute((Void) null);

        Log.d(name, dob);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class UserPictureTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPictureUrl;
        private Bitmap imageBitmap;

        UserPictureTask(String pictureUrl) {
            mPictureUrl = pictureUrl;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Bitmap bitmap = readImageFromUrl(mPictureUrl);
                imageBitmap = bitmap;
                //Thread.sleep(2000);
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onCancelled() {
            mPictureTask = null;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mPictureTask = null;
            if (success) {
                //DISPLAY IMAGE
                imageView.setImageBitmap(imageBitmap);
                //finish();
            } else {
                // Call check auth function
            }
        }


        private Bitmap readImageFromUrl(String url) throws JSONException, IOException {
            Bitmap imageBitMap = null;
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                imageBitMap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return imageBitMap;
        }
    }
}
