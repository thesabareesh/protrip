package me.sabareesh.trippie.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.model.Extras;
import me.sabareesh.trippie.model.PlaceDetail;
import me.sabareesh.trippie.model.Review;
import me.sabareesh.trippie.util.AppController;
import me.sabareesh.trippie.util.CircleTransform;
import me.sabareesh.trippie.util.Constants;


public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PlaceDetailActivity";
    private static final String TAG_RESULT = "result";
    private static final String TAG_ICON = "icon";
    private static final String TAG_NAME = "name";
    private static final String TAG_PLACE_ID = "place_id";
    private static final String TAG_RATING = "rating";
    private static final String TAG_REVIEWS = "reviews";
    private static final String TAG_ADDRESS = "vicinity";
    private static final String TAG_ADDRESS_FULL = "formatted_address";
    private static final String TAG_WEBSITE = "website";
    private static final String TAG_PHONE = "international_phone_number";
    private static final String TAG_MAP_URL = "url";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_REVIEW_NAME = "author_name";
    private static final String TAG_REVIEW_AVATAR = "profile_photo_url";
    private static final String TAG_REVIEW_TIME = "relative_time_description";
    private static final String TAG_REVIEW_BODY = "text";
    private static final String TAG_REVIEW_RATING = "rating";
    String place_id, place_name, image_URL;
    ProgressBar progressBar;
    ImageView mBanner;
    TextView tvAddress;
    RatingBar rbRatingBar;
    FloatingActionButton fabFav;
    CoordinatorLayout coordinatorLayout;
    LinearLayout llUrlIcon, llCall, llDirections, llReviews;
    PlaceDetail placeDetail = new PlaceDetail();

    public static boolean isFavourite(String placeId) {
        // TODO: 08-Jan-17 Co-pro query
        if (true) {
            return true;
        } else {
            return false;
        }
    }

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


        //Views and click listeners
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_place_detail);
        fabFav = (FloatingActionButton) findViewById(R.id.fab_favorite);
        rbRatingBar = (RatingBar) findViewById(R.id.rating);
        tvAddress = (TextView) findViewById(R.id.address);
        llUrlIcon = (LinearLayout) findViewById(R.id.icon_link);
        llCall = (LinearLayout) findViewById(R.id.icon_call);
        llDirections = (LinearLayout) findViewById(R.id.icon_directions);
        llReviews = (LinearLayout) findViewById(R.id.layout_reviews);
        fabFav.setOnClickListener(this);
        llUrlIcon.setOnClickListener(this);
        llCall.setOnClickListener(this);
        llDirections.setOnClickListener(this);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(place_name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBanner = (ImageView) findViewById(R.id.banner);
        if (image_URL != null && !image_URL.isEmpty()) {
            Picasso.with(this).load(image_URL).fit().into(mBanner);
        }

        //Ui
        fabFav.setImageResource(isFavourite(place_id) ?
                R.drawable.ic_favorite_white_24px : R.drawable.ic_favorite_border_white_24px);

        final String DOMAIN = Constants.BASE_URL_PLACE_DETAILS;
        final String APPKEY_PARAM = Constants.API_KEY_PARAM;
        final String PLACE_ID_PARAM = Constants.PLACE_ID_PARAM;


        try {
            StringBuilder sb = new StringBuilder(DOMAIN)
                    .append(PLACE_ID_PARAM + "=" + place_id)
                    .append("&" + APPKEY_PARAM + "=" + Constants.API_VALUE);

            Log.d(TAG, "Place URL built " + sb.toString());

            fetchPlaceDetails(sb.toString());

        } catch (Exception e) {
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
                        try {
                            JSONObject place = response.getJSONObject(TAG_RESULT);
                            placeDetail.setPlace_detail_id(place.getString(TAG_PLACE_ID));
                            placeDetail.setPlace_detail_icon_url(place.getString(TAG_ICON));
                            placeDetail.setPlace_detail_address(place.getString(TAG_ADDRESS_FULL));
                            tvAddress.setText(placeDetail.getPlace_detail_address());
                            placeDetail.setPlace_detail_name(place.getString(TAG_NAME));
                            if (place.has(TAG_RATING)) {
                                placeDetail.setPlace_detail_rating(place.getDouble(TAG_RATING));
                                rbRatingBar.setRating(Float.parseFloat(String.valueOf(placeDetail.getPlace_detail_rating())));
                                rbRatingBar.setVisibility(View.VISIBLE);
                            }
                            if (place.has(TAG_PHONE)) {
                                placeDetail.setPlace_detail_phone(place.getString(TAG_PHONE));
                                llCall.setVisibility(View.VISIBLE);
                            }
                            if (place.has(TAG_WEBSITE)) {
                                placeDetail.setPlace_detail_website(place.getString(TAG_WEBSITE));
                                llUrlIcon.setVisibility(View.VISIBLE);
                            }
                            if (place.has(TAG_MAP_URL)) {
                                placeDetail.setPlace_detail_url(place.getString(TAG_MAP_URL));
                                llDirections.setVisibility(View.VISIBLE);
                            }
                            if (place.has(TAG_REVIEWS)) {
                                JSONArray placeReviewsArray = place.getJSONArray(TAG_REVIEWS);
                                Review[] reviews = new Review[placeReviewsArray.length()];
                                for (int j = 0; j < placeReviewsArray.length(); j++) {
                                    JSONObject review = placeReviewsArray.getJSONObject(j);
                                    if (review.has(TAG_REVIEW_AVATAR)) {
                                        reviews[j] = new Review(
                                                review.getString(TAG_REVIEW_NAME),
                                                review.getString(TAG_REVIEW_BODY),
                                                review.getDouble(TAG_REVIEW_RATING),
                                                review.getString(TAG_REVIEW_AVATAR)
                                        );
                                    } else {
                                        reviews[j] = new Review(
                                                review.getString(TAG_REVIEW_NAME),
                                                review.getString(TAG_REVIEW_BODY),
                                                review.getDouble(TAG_REVIEW_RATING)
                                        );
                                    }
                                }
                                Extras extras = new Extras(reviews);
                                showReviews(extras);
                            }
                            //placeListDetailList.add(placeList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
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

    public void toggleFavourite() {
        if (isFavourite(placeDetail.getPlace_detail_id())) {
            // TODO: 08-Jan-17 Co-pro delete
            fabFav.setImageResource(R.drawable.ic_favorite_white_24px);
            Snackbar.make(coordinatorLayout, getString(R.string.notify_favorite), Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", this)
                    .show();

        } else {
            fabFav.setImageResource(R.drawable.ic_favorite_border_white_24px);
            // TODO: 08-Jan-17 Co-pro insert
            Snackbar.make(coordinatorLayout, getString(R.string.notify_unfavorite), Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", this)
                    .show();
        }

    }

    private void showReviews(Extras extras) {
        int numReviews = extras.getReviewsNum();
        boolean hasReviews = numReviews > 0;
        llReviews.setVisibility(hasReviews ? View.VISIBLE : View.GONE);


        if (hasReviews) {
            addReviews(extras);
        }
    }

    public void addReviews(final Extras extras) {
        int numReviews = extras.getReviewsNum();
        ViewGroup viewReviews = (ViewGroup) findViewById(R.id.reviews);
        viewReviews.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < numReviews; i++) {
            ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review, viewReviews,
                    false);
            TextView reviewAuthor = (TextView) reviewContainer.findViewById(R.id.review_author);
            TextView reviewContent = (TextView) reviewContainer.findViewById(R.id.review_content);
            ImageView rivAvatar = (ImageView) reviewContainer.findViewById(R.id.review_avatar);
            RatingBar reviewRatingBar = (RatingBar) reviewContainer.findViewById(R.id.review_rating);

            reviewAuthor.setText(extras.getReviewAtIndex(i).getAuthor());
            reviewContent.setText(extras.getReviewAtIndex(i).getBody().replace("\n\n", " ").replace("\n", " "));
            Log.d(TAG, "picasso url http:" + extras.getReviewAtIndex(i).getAvatar_url());
            Picasso.with(this).load("http:" + extras.getReviewAtIndex(i).getAvatar_url())
                    .placeholder(R.drawable.ic_account_circle_black_24px)
                    .transform(new CircleTransform())
                    .fit()
                    .into(rivAvatar);
            reviewRatingBar.setRating(Float.parseFloat(String.valueOf(extras.getReviewAtIndex(i).getRating())));
            viewReviews.addView(reviewContainer);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.icon_call:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + placeDetail.getPlace_detail_phone()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(callIntent, getString(R.string.intent_desc_link)));
                break;
            case R.id.icon_directions:
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setData(Uri.parse(placeDetail.getPlace_detail_url()));
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(mapIntent, getString(R.string.intent_desc_link)));
                }
                break;
            case R.id.icon_link:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(placeDetail.getPlace_detail_website()));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(browserIntent, getString(R.string.intent_desc_link)));
                }
                break;
            case R.id.fab_favorite:
                toggleFavourite();
            default:
                break;

        }
    }
}


