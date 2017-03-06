package me.sabareesh.trippie.ui;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import me.sabareesh.trippie.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.adapter.PlaceListAdapter;
import me.sabareesh.trippie.model.PlaceList;
import me.sabareesh.trippie.util.AppController;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.Utils;

import static me.sabareesh.trippie.R.id.rv_places;


public class PlaceListActivity extends AppCompatActivity {

    private final String LOG_TAG = PlaceListActivity.class.getSimpleName();
    String cityLatLng, categoryName;
    int itemPosition, bannerResId;
    String categoryType;
    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private List<PlaceList> placeList;
    private List<PlaceList> placeListDetailList = new ArrayList<>();
    ProgressBar progressBar;
    ImageView mBanner;

    private static final String TAG_RESULT = "results";
    private static final String TAG_ICON = "icon";
    private static final String TAG_NAME = "name";
    private static final String TAG_PLACE_ID = "place_id";
    private static final String TAG_RATING = "rating";
    private static final String TAG_ADDRESS = "vicinity";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_PHOTOS_REFERENCE = "photo_reference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        String type_param = Constants.TYPE_PARAM;

        if (getIntent().getExtras() != null) {
            cityLatLng = getIntent().getStringExtra("cityLatLng");
            itemPosition = getIntent().getIntExtra("itemPosition", 99);
            switch (itemPosition) {
                case 0:
                    categoryType = Constants.TYPE_VALUE_HOTEL;
                    categoryName = getString(R.string.title_hotel);
                    bannerResId = R.drawable.hotel;
                    break;
                case 1:
                    categoryType = Constants.TYPE_VALUE_RESTAURANT;
                    categoryName = getString(R.string.title_restaurant);
                    bannerResId = R.drawable.restaurant;
                    break;
                case 2:
                    categoryType = Constants.TYPE_VALUE_TOP_SPOTS;
                    categoryName = getString(R.string.title_top_spots);
                    type_param = Constants.TYPES_PARAM;
                    bannerResId = R.drawable.top_places;
                    break;
                case 3:
                    categoryType = Constants.TYPE_VALUE_SHOPPING;
                    categoryName = getString(R.string.title_shopping);
                    bannerResId = R.drawable.building_illustration;
                    break;
                default:
                    break;

            }

        }

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(categoryName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBanner = (ImageView) findViewById(R.id.banner);
        mBanner.setImageResource(bannerResId);


        //Recyclerview
        recyclerView = (RecyclerView) findViewById(rv_places);
        placeList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager;
        int span = (isTablet) ? 2 : 1;
        mLayoutManager = new StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new PlaceListAdapter(this, placeListDetailList);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        final String DOMAIN = Constants.BASE_URL_PLACES;
        final String APPKEY_PARAM = Constants.API_KEY_PARAM;
        final String RADIUS_PARAM = Constants.RADIUS_PARAM;
        final String LOCATION_PARAM = Constants.LOCATION_PARAM;
        final String RANK_BY_PARAM = Constants.RANK_BY_PARAM;


        try {
            StringBuilder sb = new StringBuilder(DOMAIN)
                    .append(LOCATION_PARAM + "=" + cityLatLng)
                    .append("&" + type_param + "=" + categoryType)
                    .append("&" + RANK_BY_PARAM + "=" + Constants.RANK_BY_VALUE)
                    .append("&" + RADIUS_PARAM + "=" + Constants.RADIUS_VALUE)
                    .append("&" + APPKEY_PARAM + "=" + Constants.API_VALUE);

            Log.d(LOG_TAG, "Places URL built " + sb.toString());

            fetchPlaces(sb.toString());

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error building url");
        }

        //Admob
        MobileAds.initialize(this, Constants.ABMOBS_APP_ID);
        final AdView mAdView = (AdView) findViewById(R.id.adGMSView);
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A735BBF17F1F716518CB3F5B1FE57111")
                .build();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // mAdView.loadAd(adRequest);
            }
        }, Constants.ADMOB_DELAY_MS);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                Log.d(LOG_TAG, getString(R.string.admob_loaded));
            }

        });


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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            // Credits for Item offsets : http://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/

            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void fetchPlaces(String url) {

        String tag_json_obj = "json_obj_req_places";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray list = response.getJSONArray(TAG_RESULT);
                            for (int i = 0; i < list.length(); i++) {

                                JSONObject place = list.getJSONObject(i);
                                PlaceList placeList = new PlaceList();

                                placeList.setPlace_id(place.getString(TAG_PLACE_ID));
                                placeList.setIcon_url(place.getString(TAG_ICON));
                                placeList.setPlace_address(place.getString(TAG_ADDRESS));
                                placeList.setPlace_name(place.getString(TAG_NAME));
                                if (place.has(TAG_RATING)) {
                                    placeList.setPlace_rating(place.getDouble(TAG_RATING));
                                }

                                if (place.has(TAG_PHOTOS)) {
                                    JSONArray photos = place.getJSONArray(TAG_PHOTOS);
                                    for (int j = 0; j < photos.length(); j++) {
                                        JSONObject photo = photos.getJSONObject(j);

                                        ArrayList<String> photos_reference = new ArrayList<>();
                                        photos_reference.add(photo.getString(TAG_PHOTOS_REFERENCE));

                                        placeList.setPhoto_reference(photos_reference);
                                    }
                                }

                                placeListDetailList.add(placeList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
