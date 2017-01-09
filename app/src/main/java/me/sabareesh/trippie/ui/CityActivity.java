package me.sabareesh.trippie.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.adapter.CategoryAdapter;
import me.sabareesh.trippie.model.Category;
import me.sabareesh.trippie.tasks.PhotoTask;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.RecyclerItemClickListener;
import me.sabareesh.trippie.util.Utils;

import static me.sabareesh.trippie.R.id.recycler_view_city;


public class CityActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "CityActivity";
    String mCityId, mCityName, mCityLat, mCityLng, mStaticMapURL;
    GoogleApiClient mGoogleApiClient;
    ImageView mImageView;
    ProgressBar progressBar;

    /*ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            mImageView.setImageBitmap(placePhotoResult.getBitmap());
        }
    };*/

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mImageView = (ImageView) findViewById(R.id.widePoster);

        if (getIntent().getExtras() != null) {
            mCityId = getIntent().getStringExtra("cityId");
            mCityName = getIntent().getStringExtra("cityName");
            mCityLat = getIntent().getStringExtra("cityLat");
            mCityLng = getIntent().getStringExtra("cityLng");
            mStaticMapURL = getIntent().getStringExtra("mStaticMapURL");
            if (mCityId != null) {
                //placePhotosAsync(mCityId);
                fetchPlacePhotos(mCityId);
            } else {
                Utils.loadStaticMap(this, mImageView, mCityLat, mCityLng
                        , Constants.SIZE_VALUE_M, Constants.ZOOM_VALUE_HIGH);
            }
        }

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(mCityName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //mImageView.setImageResource(R.drawable.poster_placeholder);

        //Recyclerview
        recyclerView = (RecyclerView) findViewById(recycler_view_city);
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        loadCategories();


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int itemPosition = recyclerView.getChildLayoutPosition(view);
                        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                        Log.d(TAG, "City Item clicked: " + String.valueOf(itemPosition));
                        Intent intent = new Intent(view.getContext(), PlaceListActivity.class);
                        intent.putExtra("cityLatLng", mCityLat + "," + mCityLng);
                        intent.putExtra("itemPosition", itemPosition);
                        //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CityActivity.this, thumbnail, getString(R.string.transition_image));
                        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight());
                        startActivity(intent, options.toBundle());
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //// TODO: 03-Jan-17 On item long click code
                    }
                })
        );


    }


    private void placePhotosAsync(String placeId) {
    /*
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, mImageView.getWidth(),
                                            mImageView.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });*/
    }

    private void fetchPlacePhotos(String placeId) {


        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(Constants.WIDTH_CITY_GPHOTO,
                Constants.HEIGHT_CITY_GPHOTO, mGoogleApiClient) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.poster_placeholder);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    mImageView.setImageBitmap(attributedPhoto.bitmap);

                }
                progressBar.setVisibility(View.GONE);
            }
        }.execute(placeId);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void loadCategories() {
        int[] thumbnails = new int[]{
                R.drawable.hotel,
                R.drawable.restaurant,
                R.drawable.top_places,
                R.drawable.building_illustration,
                R.drawable.hotel,
                R.drawable.hotel
        };

        Category a = new Category(getString(R.string.title_hotel), thumbnails[0]);
        categoryList.add(a);

        a = new Category(getString(R.string.title_restaurant), thumbnails[1]);
        categoryList.add(a);

        a = new Category(getString(R.string.title_top_spots), thumbnails[2]);
        categoryList.add(a);

        a = new Category(getString(R.string.title_places), thumbnails[3]);
        categoryList.add(a);

        adapter.notifyDataSetChanged();

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

    @Override
    public void onResume() {
        mGoogleApiClient.connect();
        super.onResume();

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API services connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "API services connection failed. Please reconnect.");
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


}
