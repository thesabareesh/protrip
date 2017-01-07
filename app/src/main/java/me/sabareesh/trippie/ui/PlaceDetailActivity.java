package me.sabareesh.trippie.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.model.PlaceDetail;
import me.sabareesh.trippie.model.PlaceList;
import me.sabareesh.trippie.util.AppController;
import me.sabareesh.trippie.util.Constants;

public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "PlaceDetailActivity";
    String place_id, place_name, image_URL;
    ProgressBar progressBar;
    ImageView mBanner;
    TextView tvPlaceName;
    RatingBar rbRatingBar;
    LinearLayout llUrlIcon,llCall,llDirections;

    PlaceDetail placeDetail = new PlaceDetail();

    private static final String TAG_RESULT = "result";
    private static final String TAG_ICON = "icon";
    private static final String TAG_NAME = "name";
    private static final String TAG_PLACE_ID = "place_id";
    private static final String TAG_RATING = "rating";
    private static final String TAG_ADDRESS = "vicinity";
    private static final String TAG_WEBSITE = "website";
    private static final String TAG_PHONE = "formatted_phone_number";
    private static final String TAG_URL = "url";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);


        if (getIntent().getExtras() != null) {
            place_id = getIntent().getStringExtra("place_id");
            place_name = getIntent().getStringExtra("place_name");
            image_URL = getIntent().getStringExtra("image_URL");
            Log.d(TAG, "Image url " + image_URL);
            //Toast.makeText(this, place_id, Toast.LENGTH_SHORT).show();

        }

        rbRatingBar=(RatingBar)findViewById(R.id.rating);
        llUrlIcon=(LinearLayout)findViewById(R.id.icon_link);
        llCall=(LinearLayout)findViewById(R.id.icon_call);
        llDirections=(LinearLayout)findViewById(R.id.icon_directions);
        llUrlIcon.setOnClickListener(this);
        llCall.setOnClickListener(this);
        llUrlIcon.setOnClickListener(this);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(place_name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBanner = (ImageView) findViewById(R.id.banner);
        if(image_URL!=null){
            Picasso.with(this).load(image_URL).fit().into(mBanner);
        }



        final String DOMAIN = Constants.BASE_URL_PLACE_DETAILS;
        final String APPKEY_PARAM = Constants.API_KEY_PARAM;
        final String PLACE_ID_PARAM = Constants.PLACE_ID_PARAM;



        try {
            StringBuilder sb = new StringBuilder(DOMAIN)
                    .append(PLACE_ID_PARAM + "=" + place_id)
                    .append("&" + APPKEY_PARAM + "=" + Constants.API_VALUE);

            Log.d(TAG, "Place URL built "+sb.toString());

            fetchPlaceDetails(sb.toString());

        }
        catch (Exception e){
            Log.e(TAG, "Error building url", e);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void fetchPlaceDetails(String url) {

        String tag_json_obj = "json_obj_req_pace";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject place = response.getJSONObject(TAG_RESULT);
                            for (int i = 0; i < place.length(); i++) {

                                placeDetail.setPlace_detail_id(place.getString(TAG_PLACE_ID));
                                placeDetail.setPlace_detail_icon_url(place.getString(TAG_ICON));
                                placeDetail.setPlace_detail_address(place.getString(TAG_ADDRESS));
                                placeDetail.setPlace_detail_name(place.getString(TAG_NAME));
                                if (place.has(TAG_RATING)) {
                                    placeDetail.setPlace_detail_rating(place.getDouble(TAG_RATING));
                                    rbRatingBar.setRating(Float.parseFloat(String.valueOf(placeDetail.getPlace_detail_rating())));
                                }

                                if (place.has(TAG_PHONE)) {
                                    placeDetail.setPlace_detail_phone(place.getString(TAG_PHONE));
                                }
                                if (place.has(TAG_WEBSITE)) {
                                    placeDetail.setPlace_detail_website(place.getString(TAG_WEBSITE));
                                }
                                if (place.has(TAG_URL)) {
                                    placeDetail.setPlace_detail_url(place.getString(TAG_URL));
                                }


                                //placeListDetailList.add(placeList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.icon_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+placeDetail.getPlace_detail_phone()));
                startActivity(intent);
                break;
            case R.id.icon_directions:
                break;
            case R.id.icon_uri:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(placeDetail.getPlace_detail_website()));
                startActivity(i);
                break;
            default:
                break;

        }
    }
}


