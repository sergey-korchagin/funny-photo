package com.parse.starter.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Created by User on 30/11/2015.
 */
public class Utils {

    public static void replaceFragment(FragmentManager fragmentManager, int container, Fragment fragment, boolean AddToBackStack) {

        //Enter Animations Later
        if(fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (AddToBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(container, fragment).commitAllowingStateLoss();

        }
    }
}
