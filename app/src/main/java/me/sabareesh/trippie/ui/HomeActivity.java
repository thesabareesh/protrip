package me.sabareesh.trippie.ui;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import me.sabareesh.trippie.R;


public class HomeActivity extends AppCompatActivity implements PlaceSelectionListener  /*GoogleApiClient.OnConnectionFailedListener*/ {

    public static final String TAG = "HomeActivity";
    PlaceAutocompleteFragment autocompleteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        autocompleteFragment = (PlaceAutocompleteFragment)
                 getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint(getResources().getString(R.string.home_search_hint));
        ((EditText)autocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(Integer.parseInt(getResources().getString(R.string.text_size_home_search)));
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(this);


        ViewPager mImageViewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mImageViewPager, true);


    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        final String cityId = String.valueOf(place.getId());
        final String cityName=String.valueOf(place.getName());

        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("cityName", cityName);
        startActivity(intent);

    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, getString(R.string.error_api) + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((EditText)autocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input)).setText("");
    }


}