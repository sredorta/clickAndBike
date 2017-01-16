package com.clickandbike.clickandbike.Fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickandbike.clickandbike.Activity.MapActivity;
import com.clickandbike.clickandbike.Activity.OopsActivity;
import com.clickandbike.clickandbike.Authentication.SignInActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;
import com.clickandbike.clickandbike.Toolbox.Toolbox;

import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;


/**
 * This is the main Fragment... it's an empty fragment
 * We check internet/cloud connectivity and then start app
 */
public class StartUpFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName() + "::";
    private Boolean statusInternet = false;                     //Stores if internet connexion is possible
    private Boolean statusCloud = false;                        //Stores if internet connexion is possible
    private Boolean statusUser = false;                         //Stores if User could be logged
    private int trialCount = 0;                                 //Count of trials we are doing
    ProgressDialog dialog;                                      //Dialog to show processing
    String message = null;
    User me;                                                    //User singleton
    private final int REQ_SIGNIN = 2;                           //Identifier for the request to sign-in
    private final int REQ_OOPS = 3;                             //Identifier for the request to show oops
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    // Constructor
    public static StartUpFragment newInstance() {
        return new StartUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_MODE) Log.i(TAG, "onCreate");
        //Restore the preferences
        //Init the preferences (this can only be done once !)
        me = User.getUser();
        me.init(getContext());
        ////////////////////////////////Debug !!!
        User.uEmail = "toto";

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_startup, container, false);

        startChecker();
        return v;
    }

    private void startChecker() {
        //Reset all status to false in case we come in second iteration
        statusUser = false;
        statusCloud = false;
        statusInternet = false;
        //Start waiting dialog
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Checking...");
        dialog.show();
        new CheckerTask().execute();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG_MODE) Log.i(TAG, "onResume");
/*        //If there is a message then, start Oops activity
        if (message != null) {
            Intent oops = new Intent(getContext(), OopsActivity.class);
            oops.putExtra(OopsFragment.ARG_MESSAGE,message);
            startActivityForResult(oops,REQ_OOPS);
            message = null;
        }*/
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    private class CheckerTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            statusInternet = CloudFetchr.isNetworkConnected();
            statusCloud = new CloudFetchr().isCloudConnected();

            //Debug only
            //statusInternet = true;
            //statusCloud = true;

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Remove the dialog
            dialog.cancel();
            dialog.dismiss();

            message = null;
            //Analyze the results
            if (!statusInternet) {
                message = "No internet connection !";
            }
            if (!statusCloud && statusInternet) {
                message = "No server connection !";
                //Need to show the error and when onClick come-back here or exit !!!
            }
            if (message != null) {
                Intent oops = new Intent(getContext(), OopsActivity.class);
                oops.putExtra(OopsFragment.ARG_MESSAGE, message);
                startActivityForResult(oops, REQ_OOPS);
            } else {
                //Check that we have an account and is valid
                Intent signin = new Intent(getContext(), SignInActivity.class);
                //We ask to check if there is a valid account, and if not start SignInActivity
                if (User.uEmail != null)
                    signin.putExtra(ARG_ACCOUNT_NAME, User.uEmail);
                Log.i(TAG,"Extras for signin:");
                Toolbox.dumpIntent(signin);
                startActivityForResult(signin,REQ_SIGNIN);
            }
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG_MODE) Log.i(TAG, "onActivityResult");
        if (DEBUG_MODE) Log.i(TAG, "requestCode was: " + requestCode);
        if (DEBUG_MODE) Log.i(TAG, "resultCode is:" + resultCode);

        if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_OK) {
            // The sign up activity returned that the user has successfully created an account
            Intent map = new Intent(getContext(), MapActivity.class);
            startActivity(map);
            getActivity().finish();
        } else if (requestCode == REQ_SIGNIN && resultCode == Activity.RESULT_CANCELED) {
            //Count login trials here
            if (DEBUG_MODE) Log.i(TAG, "This is trial : " + trialCount);
            trialCount = trialCount + 1;
            //Only allow three trials of launching the loggin Activity
            if (trialCount > 3) {
                getActivity().finish();
            }
            message = "Invalid credentials";
            Intent oops = new Intent(getContext(), OopsActivity.class);
            oops.putExtra(OopsFragment.ARG_MESSAGE, message);
            startActivityForResult(oops, REQ_OOPS);
        } else if (requestCode == REQ_OOPS && resultCode == Activity.RESULT_OK) {
            //When we come back from Oops we restart everything
            startChecker();
        } else if (requestCode == REQ_OOPS && resultCode == Activity.RESULT_CANCELED){
            //If the Ooops was canceled then exit the application
            getActivity().finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);

    }

}
