package com.clickandbike.clickandbike.Activity;

import android.support.v4.app.Fragment;

import com.clickandbike.clickandbike.Fragment.OopsFragment;
import com.clickandbike.clickandbike.Fragment.StartUpFragment;

/**
 * Created by sredorta on 1/13/2017.
 */
public class OopsActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return OopsFragment.newInstance();
    }

}
