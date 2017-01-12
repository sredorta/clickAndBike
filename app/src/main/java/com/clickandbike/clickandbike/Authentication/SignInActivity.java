package com.clickandbike.clickandbike.Authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;

import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;

/**
 * The Authenticator activity.
 * Called by the Authenticator and in charge of identifing the user.
 * It sends back to the Authenticator the result.
 */
public class SignInActivity extends android.accounts.AccountAuthenticatorActivity {
    private static Boolean DEBUG_MODE = true;
    private final String TAG = this.getClass().getSimpleName() + "::";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        final TextView  userTextView = (TextView) findViewById(R.id.fragment_login_EditText_user);
        final TextView  passTextView = (TextView) findViewById(R.id.fragment_login_EditText_password);


        //Init the preferences (this can only be done once !)
        User me = User.getUser();
        me.init(getApplicationContext());
        userTextView.setText(User.uFirstName);


        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((TextView)findViewById(R.id.fragment_login_EditText_user)).setText(accountName);
        }

        //Define the locker type in case is null
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAccountType == null) {
            mAccountType = AccountGeneral.ACCOUNT_TYPE;
            getIntent().putExtra(ARG_ACCOUNT_TYPE,mAccountType);
            Log.i(TAG, "Set accountType to :" + mAccountType);
        }


        //Re-enter credentials
        findViewById(R.id.fragment_login_Button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE) Log.i(TAG, "Submitting credentials to account manager !");
                submit();
            }
        });
        //Create new user account
        findViewById(R.id.fragment_login_Button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE) Log.i(TAG, "Starting new activity to create account !");
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                //Give the kind of account we want to create
                if (getIntent().getExtras() != null) {
                    signup.putExtras(getIntent().getExtras());
                    Log.i(TAG, "When starting signup extras where found !");
                } else {
                    Log.i(TAG, "When starting signup no extras found !");
                }
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG_MODE) Log.i(TAG, "onActivityResult");
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    //Submits the credentials we have introduced
    public void submit() {
        final String userName = ((TextView) findViewById(R.id.fragment_login_EditText_user)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.fragment_login_EditText_password)).getText().toString();

        //final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        //accountType should not be null here
        Log.i(TAG, "accountType = " + mAccountType);
        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                if (DEBUG_MODE) Log.i(TAG, "Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                    Log.i(TAG,"KEY_ACCOUNT_NAME :" + AccountManager.KEY_ACCOUNT_NAME);
                    Log.i(TAG,"KEY_ACCOUNT_TYPE :" + AccountManager.KEY_ACCOUNT_TYPE);
                    Log.i(TAG,"KEY_AUTHTOKEN :" + AccountManager.KEY_AUTHTOKEN);
                    Log.i(TAG, "accountType is :" + mAccountType);
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, userPass);

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                //Check if we got a token... if it's null it means that we could not signUp
                if (authtoken == null) {
                    //Redo the query to get server answer with full details
                    JsonItem item = new CloudFetchr().userSignInDetails(userName, userPass,"users");
                    data.putString(KEY_ERROR_MESSAGE, item.getMessage());
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        if (DEBUG_MODE) Log.i(TAG, "finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        Log.i(TAG, "accountName is " + accountName);
        Log.i(TAG, "accountPassword is " + accountPassword);
        Log.i(TAG, "KEY ACCOUNT_TYPE is  : " + intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            if (DEBUG_MODE) Log.i(TAG, "finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            if (DEBUG_MODE) Log.i(TAG,  "finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
