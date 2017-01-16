package com.clickandbike.clickandbike.Authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clickandbike.clickandbike.Activity.SingleFragmentActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.Fragment.OopsFragment;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;

import static com.clickandbike.clickandbike.Authentication.AccountGeneral.sServerAuthenticate;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.ARG_ACCOUNT_TYPE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.KEY_ERROR_MESSAGE;
import static com.clickandbike.clickandbike.Authentication.SignInActivity.PARAM_USER_PASS;


//Create new user account activity
public class SignUpActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return SignUpFragment.newInstance();
    }


    @Override
    public void onBackPressed() {
        Log.i("SERGI", "OnBackPressed was done, and we are now sending RESULT_CANCELED");
        setResult(RESULT_CANCELED);
        finish();
    }
}
