package mechsoft.makemymeal.WebInterface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import mechsoft.makemymeal.API.APIController;
import mechsoft.makemymeal.FCM.Config;
import mechsoft.makemymeal.Util.MConstants;
import mechsoft.makemymeal.Util.MMMPreferences;

/**
 * Created by admin2 on 24/2/18.
 */

public class WebAppInterface {
    Context mContext;
    WebView webView;

    /**
     * Instantiate the interface and set the context
     */
    public WebAppInterface(Context c, WebView webView) {
        mContext = c;
        this.webView = webView;
    }

    /**
     * To get username and password from interface
     */
    @JavascriptInterface
    public void showloginuser(String username, String password) {
        Log.i("showloginuser", "username=" + username + "password=" + password);
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(mContext);
        String loggedOutUser=mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        mmmPreferences.savePreferences(MConstants.KEY_USERNAME, username);
        mmmPreferences.savePreferences(MConstants.KEY_PASSWORD, password);
        Log.d("Login attempt",
                "username=" + username);

        /**
         * Updated FCM token when user logged out and logged in
         */
        String token =mmmPreferences.loadPreferences(MConstants.TOKEN);
        if(TextUtils.isEmpty(username)) { // existing user is logged out
            APIController apiController = new APIController();
            apiController.notifyToken(token);
            mmmPreferences.savePreferences(MConstants.LGN_TOKEN_SENT,false);
            Log.i("FCM Token", "Token sent blank for login id=" + loggedOutUser);
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_ANONYMOUS_USERS);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.TOPIC_LOGGED_USERS);
        }else{ //user is logged in
            if (!mmmPreferences.loadBoolPreferences(MConstants.LGN_TOKEN_SENT)) {
                APIController apiController = new APIController();
                apiController.notifyToken(token);
                Log.i("FCM Token", "Token sent " + token + " for login id=" + username);
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_LOGGED_USERS);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.TOPIC_ANONYMOUS_USERS);
            }
        }
    }

    /**
     * To share referral code
     */
    @JavascriptInterface
    public void CallApkloginmethod() {
        //Do nothing
        //HomeActivity.checkUserLoginForLaunch(mContext);
        Log.i("CallApkloginmethod", "loginthroughandroidappForLaunch called");
        /*Log.i("CallApkloginmethod", "Noflag=");
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(mContext);
        final String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        final String password = mmmPreferences.loadPreferences(MConstants.KEY_PASSWORD);*/

       /* if (!TextUtils.isEmpty(username) && webView != null && HomeActivity.isAppLaunched) {
            webView.post(new Runnable() {
                @Override
                public void run() {
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
                }
            });
            Log.i("CallApkloginmethod", "loginthroughandroidappForLaunch called");
        } else {
            Log.i("CallApkloginmethod", "User not logged in or invalid credential");
        }*/
    }

    /**
     * To share referral code
     */
    @JavascriptInterface
    public void StoreReferalbody(String rSubject, String rBody) {
        Log.i("StoreReferalbody", "rSubject=" + rSubject + " rBody=" + rBody);
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(mContext);
        if (!TextUtils.isEmpty(mmmPreferences.loadPreferences(MConstants.KEY_USERNAME))) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, rBody);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, rSubject);
            sendIntent.setType("text/plain");
            mContext.startActivity(Intent.createChooser(sendIntent, rSubject));
        }
    }

    /**
     * Show Dialog
     *
     * @param dialogMsg
     */
    public void showDialog(String dialogMsg) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

        // Setting Dialog Title
        alertDialog.setTitle("JS triggered Dialog");

        // Setting Dialog Message
        alertDialog.setMessage(dialogMsg);

        // Setting alert dialog icon
        //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(1, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Intent - Move to next screen
     */
    public void moveToNextScreen() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("Alert");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to leave to next screen?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Move to Next screen
                        Toast.makeText(mContext, "Move to Next screen. Open Activity.", Toast.LENGTH_SHORT).show();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel Dialog
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
}