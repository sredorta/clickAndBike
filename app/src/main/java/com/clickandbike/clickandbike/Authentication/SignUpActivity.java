package com.clickandbike.clickandbike.Authentication;

import android.accounts.AccountManager;
import android.app.Activity;
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
import static com.clickandbike.clickandbike.Authentication.SignInActivity.ARG_ACCOUNT_TYPE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.KEY_ERROR_MESSAGE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.PARAM_USER_PASS;


//Create new user account activity
public class SignUpActivity extends Activity {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName();
    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        setContentView(R.layout.fragment_signup);

        findViewById(R.id.fragment_signup_Button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        // Validation!
        new AsyncTask<String, Void, Intent>() {

            String name = ((TextView) findViewById(R.id.fragment_signup_EditText_name)).getText().toString().trim();
            String accountName = ((TextView) findViewById(R.id.fragment_signup_EditText_email)).getText().toString().trim();
            String accountPassword = ((TextView) findViewById(R.id.fragment_signup_EditText_password)).getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                if (DEBUG_MODE) Log.i(TAG, "Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = sServerAuthenticate.userSignUp(name, accountName, accountPassword, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                //Check if we got a token... if it's null it means that we could not signUp

                if (authtoken == null) {
                    //Redo the query to get server answer
                    JsonItem item = new CloudFetchr().userSignUpDetails(name, accountName, accountPassword,"users");
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
                    //Store into the preferences all this data so that we can reload when necessary
                    User.uFirstName = name;
                    User.uLastName  = name;
                    User.uEmail = accountName;
                    User me = User.getUser();
                    me.saveToPreferences();

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
