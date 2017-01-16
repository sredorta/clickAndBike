package com.clickandbike.clickandbike.Toolbox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by sredorta on 1/16/2017.
 */
public class Toolbox {
    private static Boolean DEBUG_MODE = true;
    private static final String TAG = "TOOLBOX::";

    //Dump all extras of an intent
    public static void dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.i(TAG, "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.i(TAG,"[" + key + "=" + bundle.get(key)+"]");
            }
            Log.i(TAG,"Dumping Intent end");
        }
    }
}
