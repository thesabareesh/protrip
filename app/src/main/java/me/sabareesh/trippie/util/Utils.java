package me.sabareesh.trippie.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.Nullable;
import me.sabareesh.trippie.util.Log;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.sabareesh.trippie.R;

/**
 * Created by Sabareesh on 27-Dec-16.
 */

public class Utils {

    public static final String TAG = "Utils";

    public static boolean isConnected(@Nullable Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {

            return false;
        }
    }

    public void animateView(final View view){
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                View[] animatedViews = new View[]{view};
                staggerContent(animatedViews);
                return true;
            }
        });
    }

    private void staggerContent(View[] animatedViews) {

        Interpolator interpolator = new DecelerateInterpolator();
        for (int i = 0; i < animatedViews.length; ++i) {
            View v = animatedViews[i];
           /* v.setLayerType(View.LAYER_TYPE_HARDWARE, null);*/
            v.setAlpha(0f);
            v.setTranslationY(75);
            v.animate()
                    .setInterpolator(interpolator)
                    .alpha(1.0f)
                    .translationY(0)
                    .setStartDelay(100 + 75 * i)
                    .start();
        }
    }

    public static void loadStaticMap(Context ctx,ImageView imageView,String lat, String lng,String dim,String zoom){
        final String DOMAIN = Constants.BASE_URL_STATIC_MAP;
        final String APPKEY_PARAM = Constants.API_KEY_PARAM;
        final String CENTER_PARAM = Constants.CENTER_PARAM;
        final String ZOOM_PARAM = Constants.ZOOM_PARAM;
        final String SIZE_PARAM = Constants.SIZE_PARAM;


        try {
            StringBuilder sb = new StringBuilder(DOMAIN)
                    .append(CENTER_PARAM + "=" + lat + "," + lng)
                    .append("&" + ZOOM_PARAM + "=" + zoom)
                    .append("&" + SIZE_PARAM + "=" + dim)
                    .append("&" + APPKEY_PARAM + "=" + Constants.API_MAPS_VALUE);

            String url=sb.toString();
            Log.d(TAG, "Thumbnail URL built " + url);
            if(url!=null && !url.isEmpty()) {
                Picasso.with(ctx)
                        .load(url)
                        .into(imageView);
            }


        } catch (Exception e) {
            Log.e(TAG, "Error building url");
        }

    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                //googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Making notification bar transparent
     */
    public static void changeStatusBarColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }





}
