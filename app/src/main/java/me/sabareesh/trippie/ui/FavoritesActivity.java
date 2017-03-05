package me.sabareesh.trippie.ui;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.adapter.PlaceListAdapter;
import me.sabareesh.trippie.model.PlaceDetail;
import me.sabareesh.trippie.model.PlaceList;
import me.sabareesh.trippie.provider.PlacesProvider;
import me.sabareesh.trippie.util.Log;
import me.sabareesh.trippie.util.Utils;


public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String TAG = "FavoritesActivity";
    //Loader
    private static final int PLACES_LOADER_ID = 0;
    //UI
    LinearLayout llNoFavsLayout,llOfflinelayout;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    ImageView mBanner;
    Button ivReload;
    private boolean mDidInitLoader;
    private RecyclerView recyclerView;
    private List<PlaceList> placeList;
    private PlaceListAdapter adapter;
    private List<PlaceList> placeListDetailList = new ArrayList<>();
    private ProgressBar mProgressBar;
    // Firebase & co instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFavsDbRef;
    private ChildEventListener mChildEventListener;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_titlebar_favorites));

        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(false, false);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //collapsingToolbarLayout.setTitle(getString(R.string.title_favorite_places));
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_favorites);
        mBanner = (ImageView) findViewById(R.id.banner);
        ivReload=(Button)findViewById(R.id.iv_offline_reload);
        ivReload.setOnClickListener(this);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if (getIntent().getExtras() != null) {
            mUid = getIntent().getStringExtra("mUid");
        }

        Picasso.with(this).load(R.drawable.monuments).fit().into(mBanner);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFavsDbRef = mFirebaseDatabase.getReference().getRoot().child(mUid + "/favoritePlaces");


        //Init UI views
        llNoFavsLayout = (LinearLayout) findViewById(R.id.layout_no_favs);
        llOfflinelayout = (LinearLayout) findViewById(R.id.layout_offline);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Loader
        if (!mDidInitLoader) {
            getSupportLoaderManager().initLoader(PLACES_LOADER_ID, null, this);
            mDidInitLoader = true;
        } else {
            getSupportLoaderManager().restartLoader(PLACES_LOADER_ID, null, this);
        }

        //Recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.rv_fav_places);
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
    }


    //Loader methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "onCreateLoader");
        if (id == PLACES_LOADER_ID) {
            Uri uri = PlacesProvider.CONTENT_URI;
            return new CursorLoader(this, uri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
       /* Log.v(TAG, "onLoadFinished");
        if (loader.getId() == PLACES_LOADER_ID && cursor != null) {
            while (cursor != null && cursor.moveToNext()) {
                PlaceList placeList = new PlaceList();
                placeList.setPlace_id(cursor.getString(cursor.getColumnIndex(PlacesSQLiteHelper.ID)));
                placeList.setPlace_name(cursor.getString(cursor.getColumnIndex(PlacesSQLiteHelper.TITLE)));
                placeList.setPlace_address(cursor.getString(cursor.getColumnIndex(PlacesSQLiteHelper.ADDRESS_FULL)));
                placeList.setPlace_rating(cursor.getDouble(cursor.getColumnIndex(PlacesSQLiteHelper.RATING_AVG)));
                placeList.setIcon_url(cursor.getString(cursor.getColumnIndex(PlacesSQLiteHelper.POSTERPATH_WIDE)));

                // Toast.makeText(this, cursor.getString(cursor.getColumnIndex(PlacesSQLiteHelper.TITLE)), Toast.LENGTH_SHORT).show();

                //placeListDetailList.add(placeList);
                tvFavPlaces.setVisibility(View.VISIBLE);
                llNoFavsLayout.setVisibility(View.GONE);
            }

        } else {
            tvFavPlaces.setVisibility(View.GONE);

        }
        adapter.notifyDataSetChanged();*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*// Log.v(TAG, "onLoaderReset");
        placeListDetailList.clear();
        adapter.notifyDataSetChanged();
        tvFavPlaces.setVisibility(View.GONE);
        llNoFavsLayout.setVisibility(View.VISIBLE);*/

    }


    //App lifecycles

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, " activity onResume");
        /*if (!mDidInitLoader) {
            placeListDetailList.clear();
            tvFavPlaces.setVisibility(View.GONE);
            llNoFavsLayout.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            getSupportLoaderManager().restartLoader(PLACES_LOADER_ID, null, this);
        }*/
        loadFavorites();
        placeListDetailList.clear();
        adapter.notifyDataSetChanged();
        mDidInitLoader = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Loader destroyed");
        getSupportLoaderManager().destroyLoader(PLACES_LOADER_ID);
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    PlaceDetail placeDetail = dataSnapshot.getValue(PlaceDetail.class);
                    PlaceList placeList = new PlaceList();
                    placeList.setPlace_id(placeDetail.getPlace_detail_id());
                    placeList.setPlace_name(placeDetail.getPlace_detail_name());
                    placeList.setPlace_address(placeDetail.getPlace_detail_address());
                    placeList.setPlace_rating(placeDetail.getPlace_detail_rating());
                    placeList.setIcon_url(placeDetail.getPlace_detail_icon_url());

                    placeListDetailList.add(placeList);
                    adapter.notifyDataSetChanged();

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mFavsDbRef.addChildEventListener(mChildEventListener);
            mFavsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "Done with Firebase loading");
                    mProgressBar.setVisibility(View.GONE);
                    if (placeListDetailList.size() > 0) {
                        llNoFavsLayout.setVisibility(View.GONE);
                        appBarLayout.setExpanded(true, true);
                    } else {
                        llNoFavsLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgressBar.setVisibility(View.GONE);
                }

            });

        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mFavsDbRef.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.iv_offline_reload):
                loadFavorites();
                break;
            default:
                break;
        }
    }

    private void loadFavorites(){
        if (mUid != null && Utils.isConnected(this)) {
            llOfflinelayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            attachDatabaseReadListener();
        } else {
            llOfflinelayout.setVisibility(View.VISIBLE);
            /*final Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.notify_connection), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snackbar_action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            }).show();*/
        }


    }

    //recyclerview methods
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

}
