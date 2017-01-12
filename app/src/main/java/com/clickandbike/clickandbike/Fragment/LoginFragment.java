package com.clickandbike.clickandbike.Fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clickandbike.clickandbike.Activity.LoginActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.QueryPreferences;
import com.clickandbike.clickandbike.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by sredorta on 1/11/2017.
 */
public class LoginFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;           // Enables/disables verbose logging
    private static final String TAG ="LoginFragment::";

    private Boolean statusInternet = false;             //Stores if internet connexion is possible
    private Boolean statusCloud = false;                //Stores if internet connexion is possible
    private Boolean statusUser = false;                 //Stores if User could be logged
    ProgressDialog dialog;

    // Constructor
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Verify Accounts, if account already exists then jump directly to first activity !
        // Need to use AccountManager instead of preferences !!!!



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText userInput     = (EditText) v.findViewById(R.id.fragment_login_EditText_user);
        final EditText passwordInput = (EditText) v.findViewById(R.id.fragment_login_EditText_password);
        final Button connectButton = (Button) v.findViewById(R.id.fragment_login_Button_connect);
        final Button createButton = (Button) v.findViewById(R.id.fragment_login_Button_create);

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Check if user is registered
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = String.valueOf(userInput.getText());
                String password = String.valueOf(passwordInput.getText());

                QueryPreferences.setPreference(getActivity(),QueryPreferences.PREFERENCE_USER_NAME,user);
                QueryPreferences.setPreference(getActivity(),QueryPreferences.PREFERENCE_USER_PASSWORD,password);
                //Executes sequentially tasks
                CloudFetchr.setDebugMode(true);


                //Start waiting dialog
                dialog = new ProgressDialog(getContext());
                dialog.setMessage("Checking...");
                dialog.show();
                new CloudTask().execute();
                //Reset password input just in case
                passwordInput.setText("");
            }
        });

        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private class CloudTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            statusInternet = CloudFetchr.isNetworkConnected();
            statusCloud = new CloudFetchr().isCloudConnected();
            statusUser = new CloudFetchr().isUserRegistered(
                    QueryPreferences.getPreference(getActivity(),QueryPreferences.PREFERENCE_USER_NAME),
                    QueryPreferences.getPreference(getActivity(),QueryPreferences.PREFERENCE_USER_PASSWORD),
                    "users");

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            dialog.dismiss();
            //Analyze the results
            if (!statusInternet) {
                Toast.makeText(getActivity(),"No internet connection !",Toast.LENGTH_SHORT).show();
                //Need to start a new Activity showing the error and when onClick come-back here
            }
            if (!statusCloud && statusInternet) {
                Toast.makeText(getActivity(),"No server connection !",Toast.LENGTH_SHORT).show();
                //Need to start a new Activity showing the error and when onClick come-back here or exit !!!
            }
            if(statusCloud && statusInternet && !statusUser) {
                Toast.makeText(getActivity(),"Invalid credentials !",Toast.LENGTH_SHORT).show();
            }
            if(statusCloud && statusInternet && statusUser) {
                Toast.makeText(getActivity(),"Valid credentials !",Toast.LENGTH_SHORT).show();
                //Need to start next activity here !!!!
            }
        }
    }


}
