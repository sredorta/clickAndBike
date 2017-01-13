package com.clickandbike.clickandbike.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickandbike.clickandbike.Activity.MapActivity;
import com.clickandbike.clickandbike.Authentication.SignInActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.R;
import com.clickandbike.clickandbike.Singleton.User;

/**
 * Created by sredorta on 1/13/2017.
 */
public class OopsFragment extends Fragment {
    private static Boolean DEBUG_MODE = true;
    private String TAG = getClass().getSimpleName() + "::";
    TextView errorText;                                         //Text when error happens
    TextView retryText;                                         //Text when error happens to retry operation
    ImageView errorImage;                                       //Image when error happens
    private String message;                                     //Stores the message we want to display
    public final static String ARG_MESSAGE = "ARG_MESSAGE";


    User me;                                                    //User singleton
    private final int REQ_SIGNIN = 2;                           //Identifier for the request to sign-in

    // Constructor
    public static OopsFragment newInstance() {
        return new OopsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_MODE) Log.i(TAG, "onCreate, got extra :" + getActivity().getIntent().getStringExtra(ARG_MESSAGE));

        //Get the message from the extras
        message = getActivity().getIntent().getStringExtra(ARG_MESSAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_oops, container, false);
        errorText = (TextView) v.findViewById(R.id.fragment_oops_TextView_message);
        retryText = (TextView) v.findViewById(R.id.fragment_oops_TextView_retry);
        errorImage = (ImageView) v.findViewById(R.id.fragment_oops_ImageView_error);
        LinearLayout myWindow = (LinearLayout) v.findViewById(R.id.fragment_oops_LinearLayout);
        //Set the text of the Error as we got from the intent
        errorText.setText(message);

        //When we click on the window we return to the activity that called us
        myWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
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
        Log.i(TAG, "onStop");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG_MODE) Log.i(TAG, "onResume");


    }



    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }




}
