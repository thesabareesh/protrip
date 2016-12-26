package me.sabareesh.trippie;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;


public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText mEdiTextHomeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();


        /*mEdiTextHomeSearch = (EditText) findViewById(R.id.eT_home_search);
        mEdiTextHomeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
