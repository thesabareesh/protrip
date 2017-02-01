package me.sabareesh.trippie.util;


import android.app.Application;
import me.sabareesh.trippie.BuildConfig;

/**
 * Created by Sabareesh on 31-Jan-17.
 *
 * Deactivates calls to all Log levels except Error level
 * in the source code for release build variant.
 *
 * Remove the IF condition to enable the LOG for desired level
 */

public class Log extends Application {

    static final boolean LOG = (BuildConfig.DEBUG);

    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        android.util.Log.e(tag, string);
    }
    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }
    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }
    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }
}