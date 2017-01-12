package com.clickandbike.clickandbike.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.R;

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
                    new SearchTask().execute();
                } else {
                    Log.i("SERGI","Sending OFF");
                    new SearchTask().execute();
                }
            }
        });
    }

    private class SearchTask extends AsyncTask<Void,Void,Boolean> {
        //       private GalleryItem mGalleryItem;

        @Override
        protected Boolean doInBackground(Void... params) {
            CloudFetchr fetchr = new CloudFetchr();
 //           fetchr.setStatus(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }
}
