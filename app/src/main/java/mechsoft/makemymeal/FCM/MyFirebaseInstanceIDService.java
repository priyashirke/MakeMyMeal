package mechsoft.makemymeal.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import mechsoft.makemymeal.API.APIController;
import mechsoft.makemymeal.Util.MConstants;
import mechsoft.makemymeal.MMMApplication;
import mechsoft.makemymeal.Util.MMMPreferences;

/**
 * Created by priyanka.shirke on 30/04/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(MMMApplication.getInstance().getApplicationContext());
        mmmPreferences.savePreferences(MConstants.PREVIOUS_TOKEN,mmmPreferences.loadPreferences(MConstants.TOKEN));
        mmmPreferences.savePreferences(MConstants.TOKEN,refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        APIController apiController=new APIController();
        apiController.notifyToken(token);
    }
}
