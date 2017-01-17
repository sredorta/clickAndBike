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
    public final static String PARAM_USER_EMAIL = "USER_EMAIL";
    public final static String PARAM_USER_PHONE = "USER_PHONE";
    private final int REQ_LOGIN = 1;


    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;
    private String authToken;

    /**
     * Called when the activity is first created.
     */
    private void checkAccount(String accountName) {
        Log.i(TAG, "checkAccount !");
        Log.i(TAG, "accountName :" + accountName);
        Account myAccount = null;

        //Find if there is an account with the correct accountName and get its token
        for (Account account : mAccountManager.getAccounts()) {
            Log.i(TAG, "Account found:" + account.name);
            Log.i(TAG, "    PARAM_USER_EMAIL: " + mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL));
            Log.i(TAG, "    PARAM_USER_PHONE: " + mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE));
            if (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL)!= null) {
                if (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL).equals(accountName)) {
                    myAccount = account;
                    Log.i(TAG, "Found one account matching the email :" + accountName);
                    break;
                }
            }
            if (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE)!= null) {
                if (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE).equals(accountName)) {
                    myAccount = account;
                    Log.i(TAG, "Found one account matching the phone :" + accountName);
                    break;
                }
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

    }



    private class CheckerTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (authToken!=null) {
                return new CloudFetchr().userIsTokenValid(User.uAccountName, authToken, "users");
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
                startLoginActivity();
            }
        }
    }

    public void startLoginActivity() {
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
            checkAccount(accountName);
        } else {
            Log.i(TAG, "No userName found, so starting LogIn activity !");
            startLoginActivity();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG_MODE) Log.i(TAG, "onActivityResult");

        Toolbox.dumpIntent(data);
        setAccountAuthenticatorResult(data.getExtras());
        setResult(RESULT_OK, data);
        finish();
    }

}
