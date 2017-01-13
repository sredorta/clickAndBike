package com.clickandbike.clickandbike.Activity;

import android.support.v4.app.Fragment;

import com.clickandbike.clickandbike.Fragment.StartUpFragment;


/**
 * Created by sredorta on 1/11/2017.
 */
public class StartUpActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return StartUpFragment.newInstance();
    }

}
