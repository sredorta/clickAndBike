package com.clickandbike.clickandbike.Authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickandbike.clickandbike.R;

/**
 * Created by sredorta on 1/17/2017.
 */
public class AccountFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName() + "::";
    private AccountManager mAccountManager;
    private OnFragmentInteractionListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        if (DEBUG_MODE) Log.i(TAG, "Inflating child fragment");
        final TextView userDetails = (TextView) v.findViewById(R.id.fragment_account_textView_user);
        userDetails.setText("Sergi you are the best !");
        findAccountAndPopulate(v);

        return v;
    }

    private void findAccountAndPopulate(View v) {
        if (DEBUG_MODE) Log.i(TAG, "Populating child fragment");
        mAccountManager = AccountManager.get(getActivity().getBaseContext());
        //Reformat arguments for intent to start LogInFragment
        String accountName = getActivity().getIntent().getStringExtra(SignInActivity.ARG_ACCOUNT_NAME);
        if (DEBUG_MODE) Log.i(TAG, "Found accountName equal to: " + accountName);
        final TextView userDetails = (TextView) v.findViewById(R.id.fragment_account_textView_user);
        final TextView accountDetails = (TextView) v.findViewById(R.id.fragment_account_textView_account);
        //Get the details of the account
        if(accountName!= null) {
            //Find if there is an account with the correct accountName and get its token
            Account myAccount = null;
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
            if (myAccount!= null) {
                Log.i(TAG, "    PARAM_USER_FIRST_NAME: " + mAccountManager.getUserData(myAccount, SignInActivity.PARAM_USER_FIRST_NAME));
                Log.i(TAG, "    PARAM_USER_LAST_NAME: " + mAccountManager.getUserData(myAccount, SignInActivity.PARAM_USER_LAST_NAME));

                String FullName = mAccountManager.getUserData(myAccount, SignInActivity.PARAM_USER_FIRST_NAME);
                FullName = FullName + " " + mAccountManager.getUserData(myAccount, SignInActivity.PARAM_USER_LAST_NAME);

                userDetails.setText(FullName);
                accountDetails.setText(mAccountManager.getUserData(myAccount, SignInActivity.PARAM_USER_EMAIL));
            }
        } else {
            if (DEBUG_MODE) Log.i(TAG,"accountName was null !");
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void messageFromChildFragment(Uri uri);
    }
}
