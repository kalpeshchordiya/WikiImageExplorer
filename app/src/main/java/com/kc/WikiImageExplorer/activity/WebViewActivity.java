package com.kc.WikiImageExplorer.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kc.WikiImageExplorer.R;
import com.kc.WikiImageExplorer.utils.Constants;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebviewActivity";
    private ProgressBar mProgressbar = null;
    private LinearLayout mLoadingLayout;
    private Context mContext;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mContext = this;
        initView();
        loadWebViewUrlOnWindowAnimation();
    }

    private void initView() {
        String title = getIntent().getStringExtra(Constants.WIKI_PAGE_TITLE);
        String thumbnail = getIntent().getStringExtra(Constants.WIKI_PAGE_THUMBNAIL);
        int pagePos = getIntent().getIntExtra(Constants.WIKI_PAGE_POS, 0);
        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mWebView = (WebView) findViewById(R.id.wiki_webView);
        TextView tv = (TextView) findViewById(R.id.page_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView thumbnailImage = (ImageView) findViewById(R.id.page_thumbnail);
        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(this).load(thumbnail).placeholder(R.drawable.image_loader).into
                    (thumbnailImage);
        } else {
            Glide.with(this).load(R.drawable.no_image_available).into(thumbnailImage);
        }

        int modPos = pagePos % 3;
        if (modPos == 0)
            toolbar.setBackgroundColor(getResources().getColor(R.color.bar_color_0));
        else if (modPos == 1)
            toolbar.setBackgroundColor(getResources().getColor(R.color.bar_color_1));
        else if (modPos == 2)
            toolbar.setBackgroundColor(getResources().getColor(R.color.bar_color_2));
        tv.setText(title);
    }

    private void loadWebViewUrlOnWindowAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(500);
            getWindow().setSharedElementEnterTransition(changeBoundsTransition);
            changeBoundsTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    loadWebViewUrl();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        } else {
            loadWebViewUrl();
        }
    }

    private void loadWebViewUrl() {
        if (mWebView != null) {
            String pageUrl = getIntent().getStringExtra(Constants.WIKI_PAGE_URL);
            mWebView.clearCache(true);
            mWebView.clearHistory();
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            mWebView.loadUrl(pageUrl);
            mWebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    Log.d(TAG, "Progress: " + progress);
                    mProgressbar.setProgress(progress);
                    if (progress != 100 && mLoadingLayout.getVisibility() == View.GONE) {
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        mLoadingLayout.startAnimation(AnimationUtils.loadAnimation
                                (getApplicationContext(), R.anim.abc_fade_in));

                    } else if (progress == 100) {
                        hideLoadingLayout();
                    }
                }
            });
        }
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

    @Override
    public void onBackPressed() {
        mWebView.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            try {
                mWebView.clearCache(true);
                mWebView.clearHistory();
                mWebView.clearFormData();
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            } catch (Exception e) {
                mWebView = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_webview);
        initView();
        loadWebViewUrl();
    }
}
