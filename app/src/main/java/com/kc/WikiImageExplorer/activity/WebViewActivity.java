package com.kc.WikiImageExplorer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.kc.WikiImageExplorer.R;
import com.kc.WikiImageExplorer.utils.Constants;

public class WebViewActivity extends ActionBarActivity {
    private static final String TAG = "WebviewActivity";
    private ProgressBar mProgressbar = null;
    private LinearLayout mLoadingLayout;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mContext = this;

        String pageUrl = getIntent().getStringExtra(Constants.WIKI_PAGE_URL);
        String title = getIntent().getStringExtra(Constants.WIKI_PAGE_TITLE);

        setTitle(title);

        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.clearCache(true);
        webView.clearHistory();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        webView.loadUrl(pageUrl);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                Log.d(TAG, "Progress: " + progress);
                mProgressbar.setProgress(progress);
                if (progress != 100 && mLoadingLayout.getVisibility() == View.GONE) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                } else if (progress == 100) {
                    hideLoadingLayout();
                }
            }
        });
    }

    // Hide Loading Layout with smooth animation.
    private void hideLoadingLayout() {
        final int initialHeight = mLoadingLayout.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    mLoadingLayout.setVisibility(View.GONE);
                } else {
                    mLoadingLayout.getLayoutParams().height = initialHeight - (int)
                            (initialHeight * interpolatedTime);
                    mLoadingLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // To increase duration for smooth hide effect. using factor 3 below.
        anim.setDuration((int) (initialHeight * 3 / mContext.getResources().getDisplayMetrics()
                .density));
        mLoadingLayout.startAnimation(anim);
    }
}
