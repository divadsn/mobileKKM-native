package de.codebucket.mkkm.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;

public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_REGISTRATION_COMPLETE = "registrationComplete";

    SwipeRefreshLayout mSwipeLayout;
    WebView mWebView;

    // for registration and file upload
    private static final String FORM_URL = "https://m.kkm.krakow.pl/#!/register";
    private static final int FILE_CHOOSER_RESULT_CODE = 100;
    private ValueCallback<Uri[]> mFilePathCallback;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        MaterialToolbar mToolbar = findViewById(R.id.registration_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.registration_title);

        // Load swipe layout
        mSwipeLayout = findViewById(R.id.registration_swipe_layout);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.color_primary));
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(true);

        // Setup webview
        mWebView = findViewById(R.id.registration_webview);
        mWebView.setWebChromeClient(new UploadWebChromeClient());
        mWebView.setWebViewClient(new RegistrationWebViewClient());
        mWebView.setBackgroundColor(0);
        mWebView.setBackgroundResource(R.color.color_background);

        // Enable JavaScript and offline storage support
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.loadUrl(FORM_URL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK && data != null) {
            String dataString = data.getDataString();
            if (dataString != null) {
                results = new Uri[]{ Uri.parse(dataString) };
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    private class RegistrationWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mSwipeLayout.setEnabled(true);
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            // Ignore error if it isn't our main page
            if (!request.isForMainFrame()) {
                return;
            }

            mSwipeLayout.setRefreshing(false);
            mSwipeLayout.setEnabled(false);

            Snackbar.make(findViewById(R.id.registration_layout), R.string.error_no_network, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mWebView.reload();
                        }
                    })
                    .setActionTextColor(Color.CYAN)
                    .show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSwipeLayout.setRefreshing(false);
            mSwipeLayout.setEnabled(false);

            // Close activity when registration is completed
            String page = url.substring(url.lastIndexOf('/') + 1);
            if (page.equalsIgnoreCase("login")) {
                Intent result = new Intent();
                result.putExtra(EXTRA_REGISTRATION_COMPLETE, true);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }
    }

    private class UploadWebChromeClient extends WebChromeClient {

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }

            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_chooser_file)), FILE_CHOOSER_RESULT_CODE);
            return true;
        }
    }
}
