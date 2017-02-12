package me.sabareesh.trippie.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import me.sabareesh.trippie.R;

public class PrivacyPolicy extends AppCompatActivity {

    WebView mWebview;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_privacy);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_titlebar_privacy_policy));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebview = (WebView) findViewById(R.id.webView_privacy);
        mWebview.setWebViewClient(new myWebClient());
        mWebview.loadUrl(getString(R.string.site_privacy_policy));
    }



    public class myWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
