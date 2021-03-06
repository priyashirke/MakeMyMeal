package mechsoft.makemymeal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;


public class HomeActivity extends AppCompatActivity {
    private static final String DEMO_URL = "https://www.irctc.co.in/eticketing/loginHome.jsf";
    public static WebView webView;
    public static boolean isAppLaunched;
    SwipeRefreshLayout mySwipeRefreshLayout;
    String homeURL = "";
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdateCheckerC uc = new UpdateCheckerC();
        uc.getVersionCode(this);
        Log.i("LoadTrack", "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        webView.setVisibility(View.GONE);
        checkInternetAndLoadWebview(savedInstanceState);
        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadWebView();
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 4000);
                    }
                }
        );
    }

    // Save the state of the web view when the screen is rotated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
        Log.i("onSaveInstanceState", "saveState");
        //Logger.v(this, "onSaveInstanceState", "saveState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
        //Logger.v(this, "onRestoreInstanceState", "webView.restoreState called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Logger.v(this, "onResume", "Calling checkUserLogin and setAppFlag");
        checkUserLogin();
        setAppFlag();
    }

    @Override
    protected void onDestroy() {
        webView.removeJavascriptInterface(MConstants.JAVASCRIPT_OBJ);
        isAppLaunched = false;
        //Logger.v(this, "onDestroy", "removeJavascriptInterface and isAppLaunched flag set to false");
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
                    /*if (historyUrl.equalsIgnoreCase(homeURL))
                        finish();
                    else if (webView.canGoBack())
                        webView.goBack();*/
                    webView.clearCache(true);
                    webView.goBack();
                } else {
                    finish();
                }
            } else
                finish();
        } else
            super.onBackPressed();
    }

    private void initWebview() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.loadUrl(getHomeURL());
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this, webView), MConstants.JAVASCRIPT_OBJ);
        setAppFlag();
    }

    private void checkInternetAndLoadWebview(Bundle savedInstanceState) {
        if (!NetUtils.getInstance(this).isNetworkConnected()) {
            hideWeb();
            //Logger.v(this, "LoadWebview", "NO NetworkConnected");
        } else {
            //webView.setVisibility(View.VISIBLE);
            // Reload the old WebView content
            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState);
                //Logger.v(this, "LoadWebview", "restoreState of webview");
                Log.i("onCreate", "savedInstanceState is not Null");
            }
            // Create the WebView
            else {
                Log.i("onCreate", "savedInstanceState is Null");
               // Logger.v(this, "E&LoadWebview", "Initiating webview");
                initWebview();
            }
        }
    }

    private void reloadWebView() {
        if (!NetUtils.getInstance(this).isNetworkConnected()) {
            hideWeb();
            //Logger.v(this, "LoadWebview", "NO NetworkConnected");
        } else {
            webView.reload();
        }

    }

    private void hideWeb() {
        webView.setVisibility(View.GONE);
        Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
    }

    String getHomeURL() {
        homeURL = MConstants.HOME_URL;
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);
        //Logger.v(this, "Credentials: ", "Username=" + username + " Password=" + password);
        //if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
        try {
            homeURL = MConstants.HOME_URL + "/Home/Index?usnam=" + username + "&uspas=" + password;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        Log.i("Final HomeURL=", homeURL);
        //Logger.v(this, "HomeURL=", homeURL);
        return homeURL;
    }

    private boolean urlLoading(WebView view, String url) {
        loadInBrowserOrApp(url);
        return true;
    }

    public void loadInBrowserOrApp(String url) {
        if (url.equalsIgnoreCase(homeURL)) {
            Intent browser_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browser_intent);
        } else
            webView.loadUrl(url);
    }

    public void setAppFlag() {
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        if (!mmmPreferences.loadBoolPreferences(MConstants.APP_JS_FLAG)) {
            //Logger.v(this, "setAppFlag", "Calling checkApkOrWeb(1)");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
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
                } catch (Exception e) {
                    //Logger.e(this, "webview-checkApkOrWeb", e.getMessage());
                }

            } else {
                try {
                    webView.loadUrl("javascript:{Android.checkApkOrWeb(\"1\")}");
                } catch (Exception e) {
                    //Logger.e(this, "webview-checkApkOrWeb", e.getMessage());
                }

                mmmPreferences.savePreferences(MConstants.APP_JS_FLAG, true);
                Log.i("setAppFlag", "true");
            }
        }
    }

    private void checkUserLogin() {
        Log.i("checkUserLogin", "Authenticating user");
        //Logger.v(this, "Checking User Login", "Authenticating user");
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(this);
        String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    webView.evaluateJavascript("javascript: " +
                            "loginthroughandroidapp(\"" + username + "\",\"" + password + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            // Do nothing
                        }
                    });
                } catch (Exception e) {
                    //Logger.e(this, "checkUserLogin", e.getMessage());
                }
            } else {
                try {
                    webView.loadUrl("javascript:{Android." +
                            "loginthroughandroidapp" +
                            "(\"" + username + "\",\"" + password + "\")}");
                } catch (Exception e) {
                    //Logger.e(this, "checkUserLogin", e.getMessage());
                }

            }
            Log.i("checkUserLogin", "loginthroughandroidapp called");
            //Logger.v(this, "Checking User Login", "loginthroughandroidapp method called successfully");
        } else {
            //Logger.v(this, "Checking User Login", "User not logged in or invalid credential");
            Log.d("checkUserLogin", "User not logged in or invalid credential");
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

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //Logger.e(HomeActivity.this, "onReceivedError", error.toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            Log.i("LoadTrack", "onPageFinished : " + url);
            //Logger.v(HomeActivity.this, "LoadTrack", "onPageFinished : " + url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("LoadTrack", "onPageStarted : " + url);
            //Logger.v(HomeActivity.this, "LoadTrack", "onPageStarted : " + url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            Log.i("LoadTrack", "onPageCommitVisible : " + url);
            //Logger.v(HomeActivity.this, "LoadTrack", "onPageCommitVisible : " + url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.i("LoadTrack", "onLoadResource : " + url);
            //Logger.v(HomeActivity.this, "LoadTrack", "onLoadResource : " + url);
        }
    }

    public class UpdateCheckerC {
        Document doc = null;
        Activity act;
        String virsion = "";

        public void getVersionCode(Activity act) {
            this.act = act;
            new GetVersionCode(act).execute();
        }

        private void launchUpdateDailog() {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this.act);
            builder1.setMessage("New Version Available" + "\n" + "Please, update app to new version to continue ordering.");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Update",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + act.getPackageName())));
                            finish();
                            System.exit(0);
                        }
                    });
/*
            builder1.setNegativeButton(
                    "Later",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });*/

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        private class GetVersionCode extends AsyncTask<Void, String, String> {
            Activity act;

            public GetVersionCode(Activity act) {
                this.act = act;
            }

            @Override
            protected String doInBackground(Void... voids) {

                String newVersion = null;
                try {

                    //  doc=Jsoup.connect("https://www.makemymeal.ae/makemymealdev/home/logthroughand") .timeout(30000)
                    //         .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    //       .referrer("https://www.makemymeal.ae")
                    //     .get();
                    switch (newVersion = Jsoup.connect("https://www.makemymeal.ae/makemymealdev/home/logthroughand")
                            .timeout(30000)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("https://www.makemymeal.ae")
                            .get()
                            .select("div[itemprop=SoftwareVersion]")
                            .first()
                            .ownText()) {
                    }
                    return newVersion;
                } catch (Exception e) {
                    return newVersion;
                }
            }

            @Override
            protected void onPostExecute(String onlineVersion) {
                super.onPostExecute(onlineVersion);
                if (onlineVersion != null && !onlineVersion.isEmpty()) {

                    try {
                        String currentVersion = act.getPackageManager().getPackageInfo(act.getPackageName(), 0).versionName;
                        // Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
                        //if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
//show dialog
                        //   Log.d("update", "LaunchDailog");
                        if (!currentVersion.equals(onlineVersion)) {
                            //CallOnCreate();
                            launchUpdateDailog();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    public static void checkUserLoginForLaunch(Context con) {
        Log.i("checkUserLoginForLaunch", "HomeActivity-Authenticating user while launch");
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(con);
        final String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        final String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);
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
        } else {
            Log.d("checkUserLoginForLaunch", "User not logged in or invalid credential");
        }

    }

}
