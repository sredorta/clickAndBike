package com.clickandbike.clickandbike.Authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.clickandbike.clickandbike.Activity.MapActivity;
import com.clickandbike.clickandbike.Activity.OopsActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.Fragment.OopsFragment;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;
import com.clickandbike.clickandbike.Toolbox.Toolbox;

import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;

/**
 * The Authenticator activity.
 * Called by the Authenticator and in charge of identifing the user.
 * It sends back to the Authenticator the result.
 */

public class SignInActivity extends android.accounts.AccountAuthenticatorActivity {
    private Boolean DEBUG_MODE = true;
    private final String TAG = this.getClass().getSimpleName() + "::";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private final int REQ_LOGIN = 1;


    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;
    private String authToken;

    /**
     * Called when the activity is first created.
     */
    private boolean checkAccount(String accountName) {
        Log.i(TAG, "checkAccount !");
        Log.i(TAG, "accountName :" + accountName);
        Account myAccount = null;

        //Find if there is an account with the correct accountName and get its token
        for (Account account : mAccountManager.getAccounts()) {
            Log.i(TAG, "Account found:" + account.name);
            if (account.name.equals(accountName)) {
                myAccount = account;
                Log.i(TAG, "Found one account matching the name :" + accountName);
            }
        }
        authToken = null;
        if (myAccount != null) {
            authToken = mAccountManager.peekAuthToken(myAccount, mAuthTokenType);
        }
        Log.i(TAG, "Value for token:" + authToken);
        //We would need now to fetch the cloud with the token and check if it's valid !
        Boolean isValidToken = false;
        new CheckerTask().execute();
            //Fetch the server to see if the session is valid ! And store it in the User singleton or return it somehow better!
            /////TO BE DONE

        //If it's valid we return the result as good and we exit
 /*       if (isValidToken) {
            //We should store the Token in the User singleton here !
            //setAccountAuthenticatorResult(data.getExtras());
            setResult(RESULT_OK);
            Log.i(TAG, "Exit the activity !");
            finish();
        }*/
        return isValidToken;
    }



    private class CheckerTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (authToken!=null) {
                return new CloudFetchr().userIsTokenValid(User.uEmail, authToken, "users");
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isValidToken) {
            Log.i(TAG, "isValidtoken :" + isValidToken);
            if (isValidToken) {
                setResult(RESULT_OK);
                Log.i(TAG, "Exit the activity !");
                finish();
            } else {
                Log.i(TAG, "Account was not valid so starting the login activity");
                //If we hit this part of code it means that we have no valid account !
                //We then start the LogInActivity to login/create a new one !
                Intent login = new Intent(getBaseContext(), LogInActivity.class);
                //Give the kind of account we want to create
                if (getIntent().getExtras() != null) {
                    login.putExtras(getIntent().getExtras());
                    Log.i(TAG, "When starting signup extras where found !");
                } else {
                    Log.i(TAG, "When starting signup no extras found !");
                }
                Log.i(TAG, "Intent details for intent to start LogInActivity:");
                Toolbox.dumpIntent(login);
                startActivityForResult(login, REQ_LOGIN);
            }
        }
    }







    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean isValidUser = false;

        //Dump the input intent contents
        Log.i(TAG, "Intent details for input of SignInActivity:");
        Toolbox.dumpIntent(getIntent());

        mAccountManager = AccountManager.get(getBaseContext());
        //Reformat arguments for intent to start LogInFragment
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
            getIntent().putExtra(ARG_AUTH_TYPE, mAuthTokenType);
        }
        //Define the accountType type in case is null
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAccountType == null) {
            mAccountType = AccountGeneral.ACCOUNT_TYPE;
            getIntent().putExtra(ARG_ACCOUNT_TYPE, mAccountType);
            Log.i(TAG, "Set accountType to :" + mAccountType);
        }
        //If the incoming intent has an account name, check if we have valid access
        if (accountName != null) {
            Log.i(TAG, "UserName was found, so checking if there is a valid account for: " + accountName);
            isValidUser = checkAccount(accountName);
        }

        //We now check if account exists and valid if not we start activity login
//


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG_MODE) Log.i(TAG, "onActivityResult");

        Toolbox.dumpIntent(data);
        setAccountAuthenticatorResult(data.getExtras());
        setResult(RESULT_OK, data);
        finish();
    }


//////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //Backup
    private class SignInActivity2 extends android.accounts.AccountAuthenticatorActivity {
        private Boolean DEBUG_MODE = true;
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
            final TextView userTextView = (TextView) findViewById(R.id.fragment_login_EditText_user);
            final TextView passTextView = (TextView) findViewById(R.id.fragment_login_EditText_password);



            mAccountManager = AccountManager.get(getBaseContext());

            String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
            mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
            if (mAuthTokenType == null)
                mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

            //If the incoming intent has an account name, check if we have valid access
            Log.i(TAG, "We are here !");
            if (accountName != null) {
                Log.i(TAG, "Checking if account for username : " + accountName);
                checkAccount(accountName);

            }

            //Define the locker type in case is null
            Log.i(TAG, "We should not be here !");
            mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
            if (mAccountType == null) {
                mAccountType = AccountGeneral.ACCOUNT_TYPE;
                getIntent().putExtra(ARG_ACCOUNT_TYPE, mAccountType);
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
                        Log.i(TAG, "KEY_ACCOUNT_NAME :" + AccountManager.KEY_ACCOUNT_NAME);
                        Log.i(TAG, "KEY_ACCOUNT_TYPE :" + AccountManager.KEY_ACCOUNT_TYPE);
                        Log.i(TAG, "KEY_AUTHTOKEN :" + AccountManager.KEY_AUTHTOKEN);
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
                        JsonItem item = new CloudFetchr().userSignInDetails(userName, userPass, "users");
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
                if (DEBUG_MODE) Log.i(TAG, "finishLogin > setPassword");
                mAccountManager.setPassword(account, accountPassword);
            }

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
