package me.sabareesh.trippie.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.sabareesh.trippie.R;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(0,0);

            }
        }, SPLASH_DISPLAY_TIME);
    }
}
