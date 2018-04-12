package mechsoft.makemymeal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by admin2 on 24/2/18.
 */

public class WebAppInterface {
    Context mContext;
    WebView webView;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c, WebView webView) {
        mContext = c;
        this.webView=webView;
    }

    /** To get username and password from interface */
    @JavascriptInterface
    public void showloginuser(String username,String password) {
        Log.i("showloginuser", "username="+username+"password="+password);
        MMMPreferences mmmPreferences=MMMPreferences.getInstance(mContext);
        mmmPreferences.savePreferences(MConstants.KEY_USERNAME,username);
        mmmPreferences.savePreferences(MConstants.KEY_PASSWORD,password);
        Log.d("Login attempt","username="+username);
    }

    /** To share referral code */
    @JavascriptInterface
    public void SetReferflag(String flag) {
        Log.i("SetReferflag", "flag="+flag);
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(mContext);
        String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        if (!TextUtils.isEmpty(username)&&webView!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("javascript: " +"checkApkOrWeb(\"1\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s.equalsIgnoreCase("Success")){
                            MMMPreferences mmmPreferences=MMMPreferences.getInstance(mContext);
                            mmmPreferences.savePreferences(MConstants.APP_JS_FLAG,true);
                        }
                        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
                    }

                });
            }
            else {
                webView.loadUrl("javascript:{Android.checkApkOrWeb("+flag+")}");
            }
        } else {
            Log.i("checkUserLogin", "User not logged in or invalid credential");
        }
    }

    /** To share referral code */
    @JavascriptInterface
        public void StoreReferalbody(String rSubject ,String rBody) {
        Log.i("StoreReferalbody", "rSubject="+rSubject+" rBody="+rBody);
        MMMPreferences mmmPreferences=MMMPreferences.getInstance(mContext);
        if(!TextUtils.isEmpty(mmmPreferences.loadPreferences(MConstants.KEY_USERNAME))){
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
     * @param dialogMsg
     */
    public void showDialog(String dialogMsg){
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
    public void moveToNextScreen(){
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