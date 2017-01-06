package me.sabareesh.trippie.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.util.Utils;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "SplashActivity";
    private final int SPLASH_DISPLAY_TIME = 1000;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String cityName, lat, lng;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Handler handler = new Handler();

        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, SPLASH_DISPLAY_TIME);*/
    }


    protected void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            launchHome();
        } else {
            getLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "gAPI connected");
        checkLocationPermission();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void launchHome() {

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(0, 0);

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lng = String.valueOf(mLastLocation.getLongitude());
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                    cityName = addresses.get(0).getLocality();
                    TextView tvCityName = (TextView) findViewById(R.id.tv_city_name);
                    if (cityName.length() > 0) {
                        //mCurrentCardLayout.setOnClickListener(this);
                        tvCityName.setText(cityName);
                        //mCardView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception");
                }

            } else {
                //Snackbar.make(mCoordinatorLayout, getString(R.string.notify_failed_location), Snackbar.LENGTH_LONG).show();

            }
        }

    }
}
