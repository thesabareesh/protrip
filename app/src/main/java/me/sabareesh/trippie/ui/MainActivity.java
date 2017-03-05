package me.sabareesh.trippie.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import me.sabareesh.trippie.BuildConfig;
import me.sabareesh.trippie.R;
import me.sabareesh.trippie.model.User;
import me.sabareesh.trippie.util.CircleTransform;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.Log;
import me.sabareesh.trippie.util.NotificationUtils;
import me.sabareesh.trippie.util.Utils;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener,
        View.OnClickListener {

    public static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    PlaceAutocompleteFragment mAutocompleteFragment;
    LinearLayout mCurrentCardLayout;
    CardView mCardView;
    RelativeLayout mCurrentLayout;
    TextView tvCurrCityName, tvUserName, tvUserEmail;
    ImageView ivStaticMap, ivAvatar;
    CoordinatorLayout mCoordinatorLayout;
    String mCurrentLocName, mCurrentLat, mCurrentLng, mStaticMapURL;
    MenuItem logoutItem, favoritesItem;
    FloatingActionButton searchFAB;

    // Firebase & co instance variables
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String mUsername, mUserEmail, mUid;
    private Uri mUserAvatarUrl;
    private LinearLayout mSignInLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();


        // Initialize references to views
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mCardView = (CardView) findViewById(R.id.current_location_card);
        mCurrentCardLayout = (LinearLayout) findViewById(R.id.current_location_layout);
        tvCurrCityName = (TextView) findViewById(R.id.tv_city_name);
        ivStaticMap = (ImageView) findViewById(R.id.iv_staticMap);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cLayout_main);


        //AutoCompleteFragment
        mAutocompleteFragment.setHint(getResources().getString(R.string.home_search_hint));
        ((EditText) mAutocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(Integer.parseInt(getResources().getString(R.string.text_size_home_search)));
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        mAutocompleteFragment.setFilter(typeFilter);
        mAutocompleteFragment.setOnPlaceSelectedListener(this);

        //FAB
        searchFAB = (FloatingActionButton) findViewById(R.id.fab_search);
        searchFAB.show();
        searchFAB.setOnClickListener(new View.OnClickListener() {
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

        View headerLayout = navigationView.getHeaderView(0);
        logoutItem = navigationView.getMenu().findItem(R.id.nav_signout);
        favoritesItem = navigationView.getMenu().findItem(R.id.nav_favorites);
        mSignInLayout = (LinearLayout) headerLayout.findViewById(R.id.ll_header);
        tvUserName = (TextView) headerLayout.findViewById(R.id.user_name);
        tvUserEmail = (TextView) headerLayout.findViewById(R.id.user_email);
        ivAvatar = (ImageView) headerLayout.findViewById(R.id.user_avatar);
        //listener init
        mSignInLayout.setOnClickListener(this);


        //GetIntent
        if (getIntent().getExtras() != null) {
            mCurrentLocName = getIntent().getStringExtra("currentLocName");
            mCurrentLat = getIntent().getStringExtra("currentLat");
            mCurrentLng = getIntent().getStringExtra("currentLng");
            if (mCurrentLocName != null && mCurrentLat != null && mCurrentLng != null) {
                showCurrentCard();
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestLocationPermission();
                    }
                }, Constants.PERMISSION_DELAY_MS);
            }
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
        //Firebase notifications listener
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    private void showCurrentCard() {
        mCurrentCardLayout.setOnClickListener(this);
        tvCurrCityName.setText(mCurrentLocName);
        mCardView.setVisibility(View.VISIBLE);
        Utils.loadStaticMap(this, ivStaticMap, mCurrentLat, mCurrentLng, Constants.SIZE_VALUE_S, Constants.ZOOM_VALUE_LOW);
    }

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

    //Google location methods
    protected void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            Snackbar.make(mCoordinatorLayout, getString(R.string.notify_failed_location), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mCoordinatorLayout, getString(R.string.notify_permission_accepted), Snackbar.LENGTH_LONG).show();

                } else {
                    final Snackbar snackBar = Snackbar.make(mCoordinatorLayout, getString(R.string.notify_permission_denied), Snackbar.LENGTH_LONG);
                    snackBar.setAction(getString(R.string.snackbar_action_allow), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestLocationPermission();
                        }
                    })
                            .show();
                }
                break;
            }


        }
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, getString(R.string.error_api) + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }


    //App options methods
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


        if (id == R.id.action_settings) {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setType("text/email");
            email.setData(Uri.parse("mailto:" + getString(R.string.email_admin)));
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_feedback));
            email.putExtra(Intent.EXTRA_TEXT, "\n \n" + getResources().getString(R.string.desc_app_version) + BuildConfig.VERSION_NAME +
                    "\n" + getResources().getString(R.string.desc_device_info) + Build.BRAND.toUpperCase() + " " + Build.MODEL + ", OS : " + Build.VERSION.RELEASE);
            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(email, getString(R.string.intent_desc_link)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            Intent favIntent= new Intent(this, FavoritesActivity.class);
            favIntent.putExtra("mUid", mUid);
            startActivity(favIntent);
            return false;
        }

        if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_desc));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.intent_desc_share)));

            return true;
        } else if (id == R.id.nav_feedback) {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setType("text/email");
            email.setData(Uri.parse("mailto:" + getString(R.string.email_admin)));
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_feedback));
            email.putExtra(Intent.EXTRA_TEXT, "\n \n" + getResources().getString(R.string.desc_app_version) + BuildConfig.VERSION_NAME +
                    "\n" + getResources().getString(R.string.desc_device_info) + Build.BRAND.toUpperCase() + " " + Build.MODEL + ", OS : " + Build.VERSION.RELEASE);
            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(email, getString(R.string.intent_desc_link)));

            return true;

        } else if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
            return true;
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
            return true;
        } else if (id == R.id.nav_signout) {
            confirmSignOut(this, getString(R.string.alert_title_signout),
                    getString(R.string.alert_title_signout_desc),
                    getString(R.string.alert_choice_positive_signOut));

            return true;
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
        final String cityLat = String.valueOf(place.getLatLng().latitude);
        final String cityLng = String.valueOf(place.getLatLng().longitude);

        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("cityName", cityName);
        intent.putExtra("cityLat", cityLat);
        intent.putExtra("cityLng", cityLng);
        startActivity(intent);

    }

    private void onSignedInInitialize(FirebaseUser firebaseUser) {

        //Firebase Auth
        User user = new User(firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl(),
                firebaseUser.getEmail(), firebaseUser.getUid());
        mUsername = user.getUsername();
        mUserAvatarUrl = user.getAvatarUrl();
        mUserEmail = user.getEmailId();
        mUid = user.getUid();
        tvUserName.setText(mUsername);
        tvUserEmail.setVisibility(View.VISIBLE);
        tvUserEmail.setText(mUserEmail);
        logoutItem.setVisible(true);
        favoritesItem.setVisible(true);
        if (mUserAvatarUrl != null) {
            Picasso.with(this).load(mUserAvatarUrl).
                    placeholder(R.drawable.ic_account_circle_white_24dp).
                    transform(new CircleTransform()).
                    fit().
                    into(ivAvatar);
        }


    }

    private void onSignedOutCleanup() {
        //Firebase Auth
        mUsername = Constants.ANONYMOUS;
        new User(null, null, null, null);
        tvUserName.setText(getString(R.string.drawer_user_title));
        tvUserEmail.setText("");
        tvUserEmail.setVisibility(View.GONE);
        logoutItem.setVisible(false);
        favoritesItem.setVisible(false);
        ivAvatar.setImageResource(R.drawable.ic_account_circle_white_24px);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this);
        Toast.makeText(this, "Signed out !", Toast.LENGTH_SHORT).show();
        onSignedOutCleanup();
    }

    private void confirmSignOut(Context context, String title, String message, String positiveText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        //String positiveText = context.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                });

        String negativeText = context.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //App lifecycles

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, " activity onResume");

        ((EditText) mAutocompleteFragment.getView().
                findViewById(R.id.place_autocomplete_search_input)).setText("");
        /*if (!mDidInitLoader) {
            placeListDetailList.clear();
            tvFavPlaces.setVisibility(View.GONE);
            llNoFavsLayout.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            getSupportLoaderManager().restartLoader(PLACES_LOADER_ID, null, this);
        }*/
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        //Notifications
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // TODO: 19-Feb-17 - Handle auth cancelled     
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.current_location_layout:
                Intent intent = new Intent(this, CityActivity.class);
                intent.putExtra("mStaticMapURL", mStaticMapURL);
                intent.putExtra("cityName", mCurrentLocName);
                intent.putExtra("cityLat", mCurrentLat);
                intent.putExtra("cityLng", mCurrentLng);
                startActivity(intent);
                break;

            case R.id.ll_header:
                if (mUsername.equals(Constants.ANONYMOUS)) {
                    showFirebaseLogin();
                }
            default:
                break;

        }
    }


}


