package mechsoft.makemymeal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {
    private static final String DEMO_URL = "http://apps.programmerguru.com/examples/chennai.html";
    WebView webView;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        checkInternetAndLoadWebview(savedInstanceState);
        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webView.reload();
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 4000);
                    }
                }
        );
        checkUserLoginForLaunch();
    }

    private void checkInternetAndLoadWebview(Bundle savedInstanceState) {
        if (!NetUtils.getInstance(this).isNetworkConnected()) {
            webView.setVisibility(View.GONE);
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        } else {
            webView.setVisibility(View.VISIBLE);
            // Reload the old WebView content
            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState);
                Log.i("onCreate", "savedInstanceState is not Null");
            }
            // Create the WebView
            else {
                Log.i("onCreate", "savedInstanceState is Null");
                initWebview();
            }
        }
    }

    private void initWebview() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.loadUrl(MConstants.HOME_URL);
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this, webView), MConstants.JAVASCRIPT_OBJ);
        setAppFlag();
    }

    // Save the state of the web view when the screen is rotated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
        Log.i("onSaveInstanceState", "saveState");
    }

    private boolean urlLoading(WebView view, String url) {
        loadInBrowserOrApp(url);
        return true;
    }

    public void loadInBrowserOrApp(String url) {
        if (url.equalsIgnoreCase(MConstants.HOME_URL)) {
            Intent browser_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browser_intent);
        } else
            webView.loadUrl(url);
    }

    public void setAppFlag() {
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        if (!mmmPreferences.loadBoolPreferences(MConstants.APP_JS_FLAG)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("javascript: " + "checkApkOrWeb(\"1\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s.equalsIgnoreCase("Success")) {
                            MMMPreferences mmmPreferences = MMMPreferences.getInstance(HomeActivity.this);
                            mmmPreferences.savePreferences(MConstants.APP_JS_FLAG, true);
                            Log.i("setAppFlag", "true");
                        }
                       // Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                webView.loadUrl("javascript:{Android.checkApkOrWeb(\"1\")}");
                mmmPreferences.savePreferences(MConstants.APP_JS_FLAG, true);
                Log.i("setAppFlag", "true");
            }
        }
    }

    private void checkUserLogin() {
        Log.i("checkUserLogin", "Authenticating user");
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("javascript: " +
                        "loginthroughandroidapp(\"" + username + "\",\"" + password + "\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        // Do nothing
                    }
                });
            } else {
                webView.loadUrl("javascript:{Android." +
                        "loginthroughandroidapp" +
                        "(\"" + username + "\",\"" + password + "\")}");
            }
            Log.i("checkUserLogin", "loginthroughandroidapp called");
        } else {
            Log.d("checkUserLogin", "User not logged in or invalid credential");
        }
    }

    private void checkUserLoginForLaunch() {
        Log.i("checkUserLoginForLaunch", "Authenticating user while launch");
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("javascript: " +
                        "loginthroughandroidappForLaunch(\"" + username + "\",\"" + password + "\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        // Do nothing
                    }
                });
            } else {
                webView.loadUrl("javascript:{Android." +
                        "loginthroughandroidappForLaunch" +
                        "(\"" + username + "\",\"" + password + "\")}");
            }
            Log.i("checkUserLoginForLaunch", "loginthroughandroidappForLaunch called");
        } else {
            Log.d("checkUserLoginForLaunch", "User not logged in or invalid credential");
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @RequiresApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return urlLoading(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return urlLoading(view, url);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLogin();
        setAppFlag();
    }

    @Override
    protected void onDestroy() {
        webView.removeJavascriptInterface(MConstants.JAVASCRIPT_OBJ);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
        String historyUrl = null;
        if (mWebBackForwardList != null) {
            if (mWebBackForwardList.getCurrentIndex() != 0) {
                historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
                if (historyUrl != null) {
                    if (historyUrl.equalsIgnoreCase(MConstants.HOME_URL))
                        finish();
                    else if (webView.canGoBack())
                        webView.goBack();
                }
            } else
                finish();
        } else
            super.onBackPressed();
    }
}
