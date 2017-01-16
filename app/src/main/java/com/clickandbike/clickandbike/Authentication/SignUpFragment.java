package com.clickandbike.clickandbike.Authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;

import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.ARG_ACCOUNT_TYPE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.KEY_ERROR_MESSAGE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.PARAM_USER_PASS;

//Create new user account activity
public class SignUpFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName() + "::";
    private String mAccountType;
    TextView phoneTextView;
    TextView accountNameTextView;
    TextView accountPasswordTextView;
    private String authtoken;

    // Constructor
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get parameter from Input Intent !
        mAccountType = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        final Button submitButton = (Button) v.findViewById(R.id.fragment_signup_Button_submit);
        phoneTextView =             (TextView) v.findViewById(R.id.fragment_signup_EditText_phone);
        accountNameTextView =      (TextView) v.findViewById(R.id.fragment_signup_EditText_email);
        accountPasswordTextView  = (TextView) v.findViewById(R.id.fragment_signup_EditText_password);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        return v;
    }



    private void createAccount() {
        // Validation!
        new AsyncTask<String, Void, Intent>() {

            String accountPhone = phoneTextView.getText().toString().trim();
            String accountName = accountNameTextView.getText().toString().trim();
            String accountPassword = accountPasswordTextView.getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                if (DEBUG_MODE) Log.i(TAG, "Started authenticating");

                authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = sServerAuthenticate.userSignUp(accountPhone, accountName, accountPassword, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);
                    data.putBoolean(SignInActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                //Check if we got a token... if it's null it means that we could not signUp
                Log.i(TAG, "SERGI: authtoken :" + authtoken);
                 //Store into the preferences all this data so that we can reload when necessary
                User.uFirstName = "empty";
                User.uLastName  = "empty";
                User.uEmail = accountName;
                User.uPhone = accountPhone;
                User.uPassword = accountPassword;
                User.uToken = authtoken;
                User me = User.getUser();
                me.saveToPreferences();

                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getActivity().getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {


                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        }.execute();
    }

//Need to handle this in the SignUpActivity and not fragment now
/*
    @Override
    public void getActivity().onBackPressedSergi() {
        getActivity().setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
    */
}
