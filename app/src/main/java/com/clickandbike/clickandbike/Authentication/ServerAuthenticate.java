package com.clickandbike.clickandbike.Authentication;

/**
 * Created by sredorta on 1/12/2017.
 */
public interface ServerAuthenticate {
    public String userSignUp(final String phone, final String email, final String pass, String authType) throws Exception;
    public String userSignIn(final String user, final String pass, String authType) throws Exception;
}