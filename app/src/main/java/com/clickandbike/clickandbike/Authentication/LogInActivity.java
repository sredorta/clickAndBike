package com.clickandbike.clickandbike.Authentication;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.clickandbike.clickandbike.Activity.SingleFragmentActivity;
import com.clickandbike.clickandbike.Fragment.OopsFragment;

/**
 * Created by sredorta on 1/16/2017.
 */
public class LogInActivity extends SingleFragmentActivity implements AccountFragment.OnFragmentInteractionListener{
    @Override
    public Fragment createFragment() {
        return LogInFragment.newInstance();
    }
    //Finish if we backpressed here
    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        setResult(RESULT_CANCELED);
        finish();
    }

/*    @Override
    public void messageFromParentFragment(Uri uri) {
        Log.i("TAG", "received communication from parent fragment");
    }*/

    @Override
    public void messageFromChildFragment(Uri uri) {
        Log.i("TAG", "received communication from child fragment");
    }

}