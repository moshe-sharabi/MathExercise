package com.example.mathexercises;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public final class MathView extends WebView {
    private String text, color;
    private static final String TAG = MathView.class.getSimpleName();
    private volatile boolean pageLoaded;
    AttributeSet attrs;

    public MathView(Context context) {
        super(context);
        init(context);
    }

    public MathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        setBackgroundColor(Color.TRANSPARENT);
        this.text = "";
        this.color = "white";
        this.pageLoaded = false;


        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MathView, 0, 0);

        this.text = a.getString(R.styleable.MathView_text);
        this.color = a.getString(R.styleable.MathView_text_color);
        a.recycle();

        // enable javascript
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setJavaScriptEnabled(true);

        // caching
        File dir = context.getCacheDir();
        if (!dir.exists()) {
            Log.d(TAG, "directory does not exist");
            boolean mkdirsStatus = dir.mkdirs();
            if (!mkdirsStatus) {
                Log.e(TAG, "directory creation failed");
            }
        }
        getSettings().setAppCachePath(dir.getPath());
        getSettings().setAppCacheEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // disable click
        setClickable(false);
        setLongClickable(false);
        getSettings().setUseWideViewPort(true);
        loadUrl("file:///android_asset/www/MathTemplate.html");
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;
                loadUrl("javascript:showFormula('" + MathView.this.text + "')");
                loadUrl("javascript:document.body.style.setProperty(\"color\", \"" + color + "\");");
                super.onPageFinished(view, url);
            }
        });
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
        if (pageLoaded) {
            loadUrl("javascript:showFormula('" + MathView.this.text + "')");
        } else {
            Log.e(TAG, "Page is not loaded yet.");
        }
    }

    public String getText() {
        return text.substring(1, text.length() - 1);
    }
}
