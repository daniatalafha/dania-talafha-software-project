package com.example.dania;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WatchVideoActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);  // Set the correct layout

        // Initialize WebView
        webView = findViewById(R.id.webview);

        // Enable JavaScript for the WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Load the YouTube URL into the WebView
        String videoUrl = "https://www.youtube.com/watch?v=AOHT-YiOeQA";  // Replace with your YouTube video URL
        webView.loadUrl(videoUrl);

        // Allow redirects to happen inside the WebView
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public void onBackPressed() {
        // Handle back navigation for the WebView
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
