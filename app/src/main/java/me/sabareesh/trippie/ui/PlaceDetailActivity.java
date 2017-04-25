package me.sabareesh.trippie.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.model.Extras;
import me.sabareesh.trippie.model.PlaceDetail;
import me.sabareesh.trippie.model.Review;
import me.sabareesh.trippie.model.User;
import me.sabareesh.trippie.provider.PlacesProvider;
import me.sabareesh.trippie.provider.PlacesSQLiteHelper;
import me.sabareesh.trippie.util.AppController;
import me.sabareesh.trippie.util.CircleTransform;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.Log;


public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PlaceDetailActivity";
    private static boolean isFavPlace;
    private static DatabaseReference mFavsDbRef;
    String place_id, place_name, image_URL;
    ProgressBar progressBar;
    ImageView mBanner;
    TextView tvAddress;
    RatingBar rbRatingBar;
    FloatingActionButton fabFav;
    CoordinatorLayout coordinatorLayout;
    LinearLayout llUrlIcon, llCall, llDirections, llReviews;
    PlaceDetail placeDetail = new PlaceDetail();
    // Firebase & co instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUsername, mUserEmail, mUid;
    private Uri mUserAvatarUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        //Enable support for vector drawables on Pre-lollipop devices
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (getIntent().getExtras() != null) {
            place_id = getIntent().getStringExtra("place_id");
            place_name = getIntent().getStringExtra("place_name");
            image_URL = getIntent().getStringExtra("image_URL");
            Log.d(TAG, "Image url " + image_URL);
            //Toast.makeText(this, place_id, Toast.LENGTH_SHORT).show();

        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


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
            Log.e(TAG, "Error building url");
        }

        //Firebase Auth listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user);
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    //showFirebaseLogin();
                }
            }
        };
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
                            JSONObject place = response.getJSONObject(Constants.TAG_RESULT);
                            placeDetail.setPlace_detail_id(place.getString(Constants.TAG_PLACE_ID));
                            placeDetail.setPlace_detail_icon_url(place.getString(Constants.TAG_ICON));
                            placeDetail.setPlace_detail_address(place.getString(Constants.TAG_ADDRESS_FULL));
                            tvAddress.setText(placeDetail.getPlace_detail_address());
                            placeDetail.setPlace_detail_icon_url(image_URL);
                            placeDetail.setPlace_detail_name(place.getString(Constants.TAG_NAME));
                            if (place.has(Constants.TAG_RATING)) {
                                placeDetail.setPlace_detail_rating(place.getDouble(Constants.TAG_RATING));
                                rbRatingBar.setRating(Float.parseFloat(String.valueOf(placeDetail.getPlace_detail_rating())));
                                rbRatingBar.setVisibility(View.VISIBLE);
                            }
                            if (place.has(Constants.TAG_PHONE)) {
                                placeDetail.setPlace_detail_phone(place.getString(Constants.TAG_PHONE));
                                llCall.setVisibility(View.VISIBLE);
                            }
                            if (place.has(Constants.TAG_WEBSITE)) {
                                placeDetail.setPlace_detail_website(place.getString(Constants.TAG_WEBSITE));
                                llUrlIcon.setVisibility(View.VISIBLE);
                            }
                            if (place.has(Constants.TAG_MAP_URL)) {
                                placeDetail.setPlace_detail_url(place.getString(Constants.TAG_MAP_URL));
                                llDirections.setVisibility(View.VISIBLE);
                            }
                            if (place.has(Constants.TAG_REVIEWS)) {
                                JSONArray placeReviewsArray = place.getJSONArray(Constants.TAG_REVIEWS);
                                Review[] reviews = new Review[placeReviewsArray.length()];
                                for (int j = 0; j < placeReviewsArray.length(); j++) {
                                    JSONObject review = placeReviewsArray.getJSONObject(j);
                                    if (review.has(Constants.TAG_REVIEW_AVATAR)) {
                                        reviews[j] = new Review(
                                                review.getString(Constants.TAG_REVIEW_NAME),
                                                review.getString(Constants.TAG_REVIEW_BODY),
                                                review.getDouble(Constants.TAG_REVIEW_RATING),
                                                review.getString(Constants.TAG_REVIEW_AVATAR)
                                        );
                                    } else {
                                        reviews[j] = new Review(
                                                review.getString(Constants.TAG_REVIEW_NAME),
                                                review.getString(Constants.TAG_REVIEW_BODY),
                                                review.getDouble(Constants.TAG_REVIEW_RATING)
                                        );
                                    }
                                }
                                Extras extras = new Extras(reviews);
                                showReviews(extras);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fabFav.show();
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public int toggleFavourite() {

        Uri.Builder uriBuilder = PlacesProvider.CONTENT_URI.buildUpon();

        if (isFavPlace) {
            isFavPlace = false;
            fabFav.setImageResource(R.drawable.ic_favorite_border_white_24px);
            Snackbar.make(coordinatorLayout, getString(R.string.notify_unfavorite), Snackbar.LENGTH_SHORT)
                    .show();

            //Delete on Firebase and db
            //this.getContentResolver().delete(uriBuilder.build(), placeDetail.getPlace_detail_id(), null);
            Query placeQuery = mFavsDbRef.orderByChild(Constants.title_node_place_id).equalTo(placeDetail.getPlace_detail_id());
            placeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled " + databaseError.toException());
                }
            });

        } else {
            isFavPlace = true;
            fabFav.setImageResource(R.drawable.ic_favorite_white_24px);
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlacesSQLiteHelper.ID, placeDetail.getPlace_detail_id());
            contentValues.put(PlacesSQLiteHelper.TITLE, placeDetail.getPlace_detail_name());
            contentValues.put(PlacesSQLiteHelper.POSTERPATH_WIDE, image_URL);
            // contentValues.put(MoviesSQLiteHelper.FILEPATH_WIDE_CACHE, backdropFilePath);
            // contentValues.put(MoviesSQLiteHelper.FILEPATH_SQUARE_CACHE, getResources().getString(R.string.cache_thumbnails_path)+"Fav"+movie.mPosterId+".jpg");
            contentValues.put(PlacesSQLiteHelper.RATING_AVG, placeDetail.getPlace_detail_rating());
            contentValues.put(PlacesSQLiteHelper.ADDRESS_PHONE, placeDetail.getPlace_detail_phone());
            contentValues.put(PlacesSQLiteHelper.ADDRESS_URL, placeDetail.getPlace_detail_url());
            contentValues.put(PlacesSQLiteHelper.ADDRESS_WEB, placeDetail.getPlace_detail_website());
            contentValues.put(PlacesSQLiteHelper.ADDRESS_FULL, placeDetail.getPlace_detail_address());


            //this.getContentResolver().insert(PlacesProvider.CONTENT_URI, contentValues);
            mFavsDbRef.push().setValue(placeDetail);

            Snackbar.make(coordinatorLayout, getString(R.string.notify_favorite), Snackbar.LENGTH_SHORT)
                    .show();

        }
        return 0;

    }

    private void isFavourite(Context context, final String placeId) {
        /*String URL = PlacesProvider.URL;
        Uri places = Uri.parse(URL);
        Cursor cursor = null;
        cursor = context.getContentResolver().query(places, null, PlacesSQLiteHelper.ID +" = '"+ placeId +"'", null, PlacesSQLiteHelper.ROW_ID);
        if (cursor != null&&cursor.moveToNext()) {
            return true;
        } else {
            return false;
        }*/
        isFavPlace = false;
        Query placeQuery = mFavsDbRef.orderByChild(Constants.title_node_place_id).equalTo(placeId);
        placeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        isFavPlace = true;
                        fabFav.setImageResource(R.drawable.ic_favorite_white_24px);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled " + databaseError.toException());
            }
        });

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
            Picasso.with(this).load(extras.getReviewAtIndex(i).getAvatar_url())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
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
                if (mUsername != Constants.ANONYMOUS) {
                    toggleFavourite();
                } else {
                    showFirebaseLogin();
                }

            default:
                break;

        }
    }

    //Firebase methods
    private void showFirebaseLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.auth)
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER)
                        .build(),
                Constants.RC_SIGN_IN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, " activity onResume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                //toggleFavourite();
            } else if (resultCode == RESULT_CANCELED) {
                // TODO: 19-Feb-17 - Handle auth cancelled
            }
        }
    }

    private void onSignedInInitialize(FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl(),
                firebaseUser.getEmail(),
                firebaseUser.getUid());

        mUsername = user.getUsername();
        mUserAvatarUrl = user.getAvatarUrl();
        mUserEmail = user.getEmailId();
        mUid = user.getUid();

        mFavsDbRef = mFirebaseDatabase.getReference().getRoot().child(mUid + "/favoritePlaces");
        isFavourite(this, place_id);
    }

    private void onSignedOutCleanup() {
        mUsername = Constants.ANONYMOUS;
        User user = new User(null, null, null, null);

    }


}



