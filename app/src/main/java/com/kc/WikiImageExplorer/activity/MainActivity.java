package com.kc.WikiImageExplorer.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kc.WikiImageExplorer.R;
import com.kc.WikiImageExplorer.controller.ControllerCallback;
import com.kc.WikiImageExplorer.controller.NetworkRequestManager;
import com.kc.WikiImageExplorer.listener.ItemClickListener;
import com.kc.WikiImageExplorer.model.PageObj;
import com.kc.WikiImageExplorer.model.WikiSearchResponse;
import com.kc.WikiImageExplorer.utils.Constants;
import com.kc.WikiImageExplorer.utils.Utility;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int WIKI_SEARCH_DELAYED_MESSAGE = 1000;
    private RecyclerView mDetailsRecyclerView;
    private EditText mTitleSearchET;
    private TextView mSearchingTV;
    private LinearLayout mHelpLayout;
    private ImageView mProgressIV;
    private String mSearchText = "";
    private int mRequestId = -1;
    private boolean mHelpTextAnimNextTime = true;
    private Handler mHandler;
    private RecyclerViewAdapter mRecyclerAdapter;
    private Map<String, PageObj> mEmptyWikiSearchList = new HashMap<String, PageObj>();
    private WikiSearchResponse mWikiSearchResponse;
    private WikiSearchRetainerFragment mWikiSearchRetainerFragment;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_main);
        initView();

        mWikiSearchResponse = mWikiSearchRetainerFragment.getWikiSearchData();
        mSearchText = mWikiSearchRetainerFragment.getSearchText();

        handleSearchTextChange();
    }

    // Initialize View Parameters and their required listeners for functionality
    private void initView() {
        mDetailsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSearchingTV = (TextView) findViewById(R.id.searching_text_view);
        mTitleSearchET = (EditText) findViewById(R.id.page_title_search_edit_text);
        mHelpLayout = (LinearLayout) findViewById(R.id.help_layout);
        mProgressIV = (ImageView) findViewById(R.id.processing_image_view);

        mTitleSearchET.addTextChangedListener(new InputValidator());
        mHandler = new MyHandler(this);

        GridLayoutManager lLayout = new GridLayoutManager(MainActivity.this, 2);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lLayout.setSpanCount(3);
        }

        mDetailsRecyclerView.setLayoutManager(lLayout);

        mRecyclerAdapter = new RecyclerViewAdapter(mContext, mEmptyWikiSearchList,
                mItemClickListener);
        mDetailsRecyclerView.setAdapter(mRecyclerAdapter);

        // Create the fragment to store large data,
        // This data is to be used in case of Activity is recreated by android system.
        FragmentManager fm = getFragmentManager();
        mWikiSearchRetainerFragment = (WikiSearchRetainerFragment) fm.findFragmentByTag
                (Constants.WIKI_PAGE_RETAINER_FRAGMENT);
        if (mWikiSearchRetainerFragment == null) {
            // add the fragment
            mWikiSearchRetainerFragment = new WikiSearchRetainerFragment();
            fm.beginTransaction().add(mWikiSearchRetainerFragment,
                    Constants.WIKI_PAGE_RETAINER_FRAGMENT).commit();
        }

        // Handle touch on ClearText Button
        mTitleSearchET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mTitleSearchET.getRight() - mTitleSearchET
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mTitleSearchET.setText("");
                        return false;
                    }
                }
                return false;
            }
        });

        // Hide keyboard when GridView is scrolled by user. Here user wants to see more data from
        // GridView, so keyboard is not needed and we hide it.
        mDetailsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    // SearchText is not Empty : If we have some response data restored from earlier session of
    //      activity, update screen using that data, else lets make wiki search request again,
    // If SearchText is Empty : Update HelpView with proper info.
    private void handleSearchTextChange() {
        if (!TextUtils.isEmpty(mSearchText)) {
            if (mWikiSearchResponse != null) {
                updateScreenFromWikiSearchResponse();
            } else {
                // Send Delayed Message, so that if user updates search string before this message gets
                // delivered we remove this message from message queue.
                if (Utility.isConnected(this)) {
                    showHelpView(String.format(getResources().getString(R.string.searching_text),
                            mSearchText));
                    startProgressAnimation(true);
                    mHandler.removeMessages(WIKI_SEARCH_DELAYED_MESSAGE);
                    Message msg = new Message();
                    msg.obj = mSearchText;
                    msg.what = WIKI_SEARCH_DELAYED_MESSAGE;
                    mHandler.sendMessageDelayed(msg, 800);
                } else {
                    showHelpView(getResources().getString(R.string.network_not_available));
                }
            }
        } else {
            if (Utility.isConnected(this)) {
                mHandler.removeMessages(WIKI_SEARCH_DELAYED_MESSAGE);
                if (mRequestId != -1) {
                    NetworkRequestManager.getInstance().cancelRequest(mRequestId);
                    mRequestId = -1;
                }
                showHelpView("");
            } else {
                showHelpView(getResources().getString(R.string.network_not_available));
            }
        }
    }

    // To Watch-Out for text change on EditText.
    private class InputValidator implements TextWatcher {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mTitleSearchET.getText() != null && mSearchText.equals(mTitleSearchET.getText().toString())) {
                return;
            }
            mWikiSearchResponse = null;
            if (mTitleSearchET.getText().length() > 0) {
                mSearchText = mTitleSearchET.getText().toString();
            } else {
                mSearchText = "";
            }
            handleSearchTextChange();
        }
    }

    // Show HelpView with proper text, and animations if required.
    // We show HelpView, as we do not have data to shown GridView so we hide GridView.
    private void showHelpView(String helpText) {
        Log.d(TAG, "Show Help View with "+helpText);
        mDetailsRecyclerView.setVisibility(View.GONE);
        mDetailsRecyclerView.smoothScrollToPosition(0);
        mRecyclerAdapter.updateWikiPages(mEmptyWikiSearchList);
        if (!TextUtils.isEmpty(helpText)) {
            mSearchingTV.setText(helpText);
            mHelpLayout.setVisibility(View.VISIBLE);
            if (mHelpTextAnimNextTime == true) {
                mHelpLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_in_bottom));
                mHelpTextAnimNextTime = false;
            }
        } else {
            mHelpLayout.setVisibility(View.GONE);
            mHelpTextAnimNextTime = true;
        }
    }

    // Show GridView and hide Help View
    private void showGridView() {
        mHelpLayout.setVisibility(View.GONE);
        mDetailsRecyclerView.setVisibility(View.VISIBLE);
    }

    // Start or Stop progress animation in HelpView depending on started parameter.
    // If started is true, Show Progress ImageView with animation otherwise hide it.
    private void startProgressAnimation(boolean started) {
        if (started) {
            if (mProgressIV.isShown() == false) {
                mProgressIV.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim
                        .infinite_rotate);
                mProgressIV.startAnimation(animation);
            }
        } else {
            mProgressIV.clearAnimation();
            mProgressIV.setVisibility(View.GONE);
        }
    }

    // When delayed message gets delivered here, we send network call to get wiki pages depending on
    // network availability. Here we are keeing weak referrence of activity so that it gets
    // destroyed when activity is no more.
    private static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivityRef;

        MyHandler(MainActivity activity) {
            mActivityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIKI_SEARCH_DELAYED_MESSAGE:
                    String searchText = (String) msg.obj;
                    MainActivity activity = mActivityRef.get();
                    if (activity != null) {
                        activity.sendWikiSearchRequest(searchText);
                    }
                    break;
            }
        }
    }

    // Send Wiki Search request through NetworkRequestManager with search Text entered by user.
    private void sendWikiSearchRequest(String searchText) {
        Log.d(TAG, "ProcessWikiSearch = " + searchText);
        if (Utility.isConnected(this)) {
            if (mRequestId != -1) {
                NetworkRequestManager.getInstance().cancelRequest(mRequestId);
                mRequestId = -1;
            }
            mRequestId = NetworkRequestManager.getInstance().sendWikiSearch(searchText, Constants
                    .NO_OF_PAGES, Constants.THUMBNAIL_SIZE, mWikiSearchCallback);
        } else {
            showHelpView(getResources().getString(R.string.network_not_available));
        }
    }

    // Callback for search API. Callback methods will be called once we have response from network.
    private ControllerCallback mWikiSearchCallback = new ControllerCallback(this) {
        @Override
        protected void onSuccessResponse(int requestId, int status, Object data) {
            Log.d(TAG, "On SuccessResponse");
            mRequestId = -1;
            mHelpTextAnimNextTime = true;
            mWikiSearchResponse = (WikiSearchResponse) data;
            updateScreenFromWikiSearchResponse();
            startProgressAnimation(false);
        }

        @Override
        protected void onErrorResponse(int requestId, int status, Object data) {
            Log.d(TAG, "On ErrorResponse");
            mHelpTextAnimNextTime = true;
            showHelpView(getResources().getString(R.string.error_getting_data_text));
            startProgressAnimation(false);
        }
    };

    // From the Wikipedia search response for query, this will update GridiView with data.
    private void updateScreenFromWikiSearchResponse() {
        WikiSearchResponse response = mWikiSearchResponse;
        if (response != null && response.getQuery() != null && response.getQuery().getPages() !=
                null && response.getQuery().getPages().size() > 0) {
            Log.d(TAG, "Wiki Search Response size = " + response.getQuery().getPages().size());
            showGridView();
            mRecyclerAdapter.updateWikiPages(response.getQuery().getPages());
        } else {
            showHelpView(String.format(getResources().getString(R.string.no_data_found_text),
                    mSearchText));
        }
    }

    private ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onItemClicked(View view, PageObj pageObj, int position) {
            if (Utility.isConnected(mContext)) {
                if (pageObj != null) {
                    // Send Shared Image and Title View for across activity animations.
                    ImageView im = (ImageView) view.findViewById(R.id.page_thumbnail);
                    TextView tv = (TextView) view.findViewById(R.id.page_title);
                    Pair<View, String> imagePair = Pair.create((View) im, "cardImage");
                    Pair<View, String> titleTextPair = Pair.create((View) tv,
                            "pageTitleTransition");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity
                            .this, imagePair, titleTextPair);
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra(Constants.WIKI_PAGE_URL, Constants.WIKI_READ_URL_WITH_PAGE_ID
                            + pageObj.getPageid());
                    intent.putExtra(Constants.WIKI_PAGE_TITLE, pageObj.getTitle());
                    intent.putExtra(Constants.WIKI_PAGE_POS, position);
                    if (pageObj.getThumbnail() != null)
                        intent.putExtra(Constants.WIKI_PAGE_THUMBNAIL, pageObj.getThumbnail().getSource());
                    ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                }
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.network_not_available)
                        , Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWikiSearchRetainerFragment != null) {
            mWikiSearchRetainerFragment.setWikiSearchData(mWikiSearchResponse);
            mWikiSearchRetainerFragment.setSearchText(mSearchText);
            if (mRequestId != -1) {
                NetworkRequestManager.getInstance().cancelRequest(mRequestId);
            }
        }
        mHandler.removeMessages(WIKI_SEARCH_DELAYED_MESSAGE);
    }
}