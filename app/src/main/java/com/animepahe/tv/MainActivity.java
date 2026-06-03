package com.animepahe.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;
    private static final String URL = "https://animepahe.ch/";
    private String lastUrl = URL;

    private static final String[] AD_HOSTS = {
        "googlesyndication.com",
        "googleadservices.com",
        "doubleclick.net",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "ads.yahoo.com",
        "adnxs.com",
        "advertising.com",
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "exoclick.com",
        "juicyads.com",
        "trafficjunky.com",
        "hilltopads.net",
        "adsterra.com",
        "mgid.com",
        "revcontent.com",
        "outbrain.com",
        "taboola.com",
        "clickadu.com",
        "adskeeper.com",
        "push.house",
        "moonicorn.com",
        "adcash.com",
        "zedo.com",
        "traffic-media.co",
        "a-ads.com"
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setUserAgentString(
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        );

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Hanya izinkan URL animepahe, blokir semua yang lain
                if (url.contains("animepahe")) {
                    lastUrl = url;
                    return false;
                }

                // Blokir semua URL lain
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Inject JS untuk blokir popup & redirect
                view.evaluateJavascript(
                    "window.open = function() { return null; };" +
                    "window.alert = function() {};" +
                    "window.confirm = function() { return false; };" +
                    "document.addEventListener('click', function(e) {" +
                    "  var el = e.target;" +
                    "  while(el) {" +
                    "    if(el.tagName === 'A') {" +
                    "      el.target = '_self';" +
                    "      var href = el.getAttribute('href');" +
                    "      if(href && href.indexOf('animepahe') === -1) {" +
                    "        e.preventDefault();" +
                    "      }" +
                    "    }" +
                    "    el = el.parentElement;" +
                    "  }" +
                    "}, true);",
                    null
                );
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                if (host != null) {
                    for (String adHost : AD_HOSTS) {
                        if (host.contains(adHost)) {
                            return new WebResourceResponse("text/plain", "utf-8", null);
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return false; // Blokir semua popup window
            }
        });

        webView.loadUrl(URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }
}
