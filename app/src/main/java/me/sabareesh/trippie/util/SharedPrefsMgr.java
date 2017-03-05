package me.sabareesh.trippie.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sabareesh on 14-Jan-17.
 */

public class SharedPrefsMgr  {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preference name should be changed if a new slide is added
    private static final String PREF_NAME = "intro-swipe-layout";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPrefsMgr(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}

