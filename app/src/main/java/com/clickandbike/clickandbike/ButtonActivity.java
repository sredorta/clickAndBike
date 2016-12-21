package com.clickandbike.clickandbike;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class ButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        final Switch mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch.setChecked(false);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.i("SERGI","Sending ON");
                    new SearchTask().execute("on");
                } else {
                    Log.i("SERGI","Sending OFF");
                    new SearchTask().execute("off");
                }
            }
        });
    }

    private class SearchTask extends AsyncTask<String,Void,Void> {
        //       private GalleryItem mGalleryItem;

        @Override
        protected Void doInBackground(String... params) {
            CloudFetchr fetchr = new CloudFetchr();
            fetchr.setStatus(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
