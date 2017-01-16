package com.clickandbike.clickandbike.Authentication;

import android.util.Log;

import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.google.gson.Gson;

import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sredorta on 1/12/2017.
 */
public class LockerServerAuthenticate implements ServerAuthenticate {

    //Send to the server all fields and create a new user and get the token
    @Override
    public String userSignUp(String phone, String email, String password, String authType) {
        String authtoken = new CloudFetchr().userSignUp(phone,email,password, "users");
        return authtoken;
    }


    //Send to the Server username and password and get corresponding token
    @Override
    public String userSignIn(String user, String password, String authType) {
        String authtoken = new CloudFetchr().userSignIn(user,password, "users");
        return authtoken;
    }

}
