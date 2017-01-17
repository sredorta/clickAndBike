
package com.clickandbike.clickandbike.Authentication;

        import android.accounts.Account;
        import android.accounts.AccountManager;
        import android.accounts.AccountManagerFuture;
        import android.accounts.AuthenticatorException;
        import android.accounts.OperationCanceledException;
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.clickandbike.clickandbike.Activity.SingleFragmentActivity;
        import com.clickandbike.clickandbike.DAO.CloudFetchr;
        import com.clickandbike.clickandbike.DAO.JsonItem;
        import com.clickandbike.clickandbike.R;
        import com.clickandbike.clickandbike.Singleton.User;
        import com.clickandbike.clickandbike.Toolbox.Toolbox;

        import java.io.IOException;
        import java.util.concurrent.Executor;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
        import java.util.concurrent.TimeUnit;

        import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;
        import static com.clickandbike.clickandbike.Authentication.SignInActivity.ARG_ACCOUNT_TYPE;

/**
 * Created by sredorta on 1/13/2017.
 */
public class LogInFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName() + "::";
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
    String accountName;

    // Constructor
    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get parameter from Input Intent !

        //Init the preferences (this can only be done once !)
        User me = User.getUser();
        me.init(getActivity().getApplicationContext());

        mAccountManager = AccountManager.get(getActivity().getBaseContext());

        accountName = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getActivity().getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;


        //Define the User Account type in case is null
        mAccountType = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAccountType == null) {
            mAccountType = AccountGeneral.ACCOUNT_TYPE;
            getActivity().getIntent().putExtra(ARG_ACCOUNT_TYPE,mAccountType);
            Log.i(TAG, "Set accountType to :" + mAccountType);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText userTextView = (EditText) v.findViewById(R.id.fragment_login_EditText_user);
        final EditText passTextView = (EditText) v.findViewById(R.id.fragment_login_EditText_password);
        //We recover from preferences and set default user name
        userTextView.setText(User.uAccountName);

        //Re-enter credentials
        v.findViewById(R.id.fragment_login_Button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE) Log.i(TAG, "Submitting credentials to account manager !");
                if (User.checkPasswordInput(passTextView.getText().toString())) {
                    submit(userTextView.getText().toString(), passTextView.getText().toString());
                    passTextView.setText("");
                } else {
                    passTextView.setText("");
                    Toast.makeText(getActivity(),"Password must be at least 8 chars !", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Create new user account
        v.findViewById(R.id.fragment_login_Button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE) Log.i(TAG, "Starting new activity to create account !");
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getActivity().getBaseContext(), SignUpActivity.class);
                //Give the kind of account we want to create
                if (getActivity().getIntent().getExtras() != null) {
                    signup.putExtras(getActivity().getIntent().getExtras());
                    Log.i(TAG, "When starting signup extras where found !");
                } else {
                    Log.i(TAG, "When starting signup no extras found !");
                }
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });

        return v;
    }

    //When we come back from new account creation we fall here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG_MODE) Log.i(TAG, "onActivityResult");
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == Activity.RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }



    //Submits the credentials we have introduced
    public void submit(final String userName, final String userPass) {

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
                //Check if we got a token... if it's null it means that we could not LogIn
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
                    Toast.makeText(getActivity().getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    //Update the User singleton
                    User.setEmailOrPhone(userName);
                    User.uAccountName = userName;
                    User.uPassword = userPass;

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
        Log.i(TAG, "Intent details SERGI:");
        Toolbox.dumpIntent(intent);
        if (intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
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

        Log.i(TAG, "We are supposed to be saving user data to the account here !");
        updateAccountData(account);
        purgeAccounts();
        User me = User.getUser();
        me.saveToPreferences();
        //Return all results to the SignInActivity
//        setAccountAuthenticatorResult(intent.getExtras());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    // Updates the Account with all USER parameters
    private void updateAccountData(Account account) {
        Log.i(TAG, "Storing all user data in the account...");
        mAccountManager.setUserData(account,SignInActivity.PARAM_USER_EMAIL, User.uEmail);
        mAccountManager.setUserData(account,SignInActivity.PARAM_USER_PHONE, User.uPhone);

    }
    //Removes all accounts except the one we have just created
    private void purgeAccounts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Find if there is an account with the correct accountName and get its token
        for (Account account : mAccountManager.getAccounts()) {
            Boolean needToRemove = false;
            Log.i(TAG, "Account found:" + account.name);
            Log.i(TAG, "    PARAM_USER_EMAIL: " + mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL));
            Log.i(TAG, "    PARAM_USER_PHONE: " + mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE));
            if ((mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL)== null) ||
                    (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE)== null))
                        needToRemove = true;
            if (!needToRemove) {
                if (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL).equals(User.uEmail) &&
                        (mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE).equals(User.uPhone))) {
                        needToRemove = false;
                } else {
                    needToRemove = true;
                    //Add in the queue to remove the account from the Server also
                    final String email = mAccountManager.getUserData(account, SignInActivity.PARAM_USER_EMAIL);
                    final String phone = mAccountManager.getUserData(account, SignInActivity.PARAM_USER_PHONE);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            new CloudFetchr().removeUser(email, phone,"users");
                        }
                    });
                }
            }
            if (needToRemove) {
                        Boolean isDone = false;
                        String name = account.name;
                        if (Build.VERSION.SDK_INT<22) {
                            //@SuppressWarnings("deprecation")
                            final AccountManagerFuture<Boolean> booleanAccountManagerFuture = mAccountManager.removeAccount(account, null, null);
                            try {
                              isDone = booleanAccountManagerFuture.getResult(1, TimeUnit.SECONDS);
                            } catch (OperationCanceledException e) {
                                Log.i(TAG, "Caught exception : " + e);
                            } catch (IOException e) {
                                Log.i(TAG, "Caught exception : " + e);
                            } catch (AuthenticatorException e) {
                                Log.i(TAG, "Caught exception : " + e);
                            }
                            if (isDone) Log.i(TAG, "Successfully removed account : " + name);

                        } else {
                            isDone = mAccountManager.removeAccountExplicitly(account);
                            if (isDone) Log.i(TAG, "Successfully removed account : " + name);
                        }

            }
        }
        executor.shutdown();

    }

}