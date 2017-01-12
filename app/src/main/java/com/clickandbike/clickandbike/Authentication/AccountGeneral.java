package com.clickandbike.clickandbike.Authentication;

/**
 * Created by sredorta on 1/12/2017.
 */

/* General settings for the Account handling*/
public class AccountGeneral {

    // Account type id
    public static final String ACCOUNT_TYPE = "com.clickandbike.auth_locker";

    // Account name
    public static final String ACCOUNT_NAME = "Locker";

    //Auth token types
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to a Locker account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a Locker account";

    public static final ServerAuthenticate sServerAuthenticate = new LockerServerAuthenticate();
}
