package me.sabareesh.trippie.ui;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.RuntimeExecutionException;

import java.util.List;
import java.util.Locale;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.SharedPrefsMgr;
import me.sabareesh.trippie.util.Utils;


/**
 * Fetches the user location and sends it to MainActivity
 */
public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SplashActivity";
    Location mLastLocation;
    String mCurrentLocName, mCurrentLat, mCurrentLng;
    private GoogleApiClient mGoogleApiClient;
    ImageView logo;
    Handler handler = new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            launchHome();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

         logo = (ImageView)findViewById(R.id.logo);
    }

    protected void checkLocationPermission() {
        if (!Utils.isConnected(this) || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            skip();
        } else {
            getLocation();
        }
    }

    public void skip() {

        handler.postDelayed(runnable,Constants.SPLASH_DELAY_MS);

    }

    public void launchHome() {

        // Checking for first time launch
        SharedPrefsMgr prefManager = new SharedPrefsMgr(this);
        if (prefManager.isFirstTimeLaunch()) {
            //show welcome screen
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
            overridePendingTransition(0, 0);
        } else {
            //Launch home screen
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("currentLat", mCurrentLat);
            intent.putExtra("currentLng", mCurrentLng);
            intent.putExtra("currentLocName", mCurrentLocName);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
            overridePendingTransition(0, 0);
        }


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mCurrentLat = String.valueOf(mLastLocation.getLatitude());
                mCurrentLng = String.valueOf(mLastLocation.getLongitude());
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(mCurrentLat), Double.parseDouble(mCurrentLng), 1);
                    //mCurrentLocName = addresses.get(0).getLocality();
                    mCurrentLocName = addresses.get(0).getAddressLine(1);

                } catch (Exception e) {
                    //Log.d(TAG, "Exception");
                }
            }
            skip();
        }

    }

    //App lifecycle methods
    @Override
    public void onStart() {
        if (Utils.isGooglePlayServicesAvailable(this)) {
            mGoogleApiClient.connect();
        } else {
            skip();
        }
        super.onStart();
        //Log.d(TAG, "onStart");
    }

    protected void onResume() {
        checkLocationPermission();
        super.onResume();
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        //Log.d(TAG, "onStop");
    }


    //Google Location API methods
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.d(TAG, "gAPI connected");
        checkLocationPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.d(TAG, "gAPI suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
