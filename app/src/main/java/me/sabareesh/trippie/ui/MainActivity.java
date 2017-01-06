package me.sabareesh.trippie.ui;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.util.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener, View.OnClickListener {

    public static final String TAG = "MainActivity";
    PlaceAutocompleteFragment mAutocompleteFragment;
    LinearLayout mCurrentCardLayout;
    CardView mCardView;
    CoordinatorLayout mCoordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //AutoCompleteFragment
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mAutocompleteFragment.setHint(getResources().getString(R.string.home_search_hint));
        ((EditText) mAutocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(Integer.parseInt(getResources().getString(R.string.text_size_home_search)));
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        mAutocompleteFragment.setFilter(typeFilter);
        mAutocompleteFragment.setOnPlaceSelectedListener(this);

        //current location layouts
        mCardView=(CardView)findViewById(R.id.current_location_card);
        mCurrentCardLayout=(LinearLayout) findViewById(R.id.current_location_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cLayout_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (mAutocompleteFragment.getView().
                        findViewById(R.id.place_autocomplete_search_input)).performClick();
            }
        });


        //NavigationDrawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOC: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Snackbar.make(mCoordinatorLayout, getString(R.string.notify_permission_denied), Snackbar.LENGTH_LONG).show();

                }
                break;
            }


        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        final String cityId = String.valueOf(place.getId());
        final String cityName = String.valueOf(place.getName());
        final String cityLatLng = String.valueOf(place.getLatLng().latitude + "," + place.getLatLng().longitude);

        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("cityName", cityName);
        intent.putExtra("cityLatLng", cityLatLng);
        startActivity(intent);

    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, getString(R.string.error_api) + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        ((EditText) mAutocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input)).setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.current_location_layout:
                final String cityLatLng = String.valueOf(lat+"," +lng);

                Intent intent = new Intent(this, CityActivity.class);
                /*intent.putExtra("cityId", cityId);*/
                intent.putExtra("cityName", cityName);
                intent.putExtra("cityLatLng", cityLatLng);
                startActivity(intent);
                break;

            default:
                break;

        }
    }


}
