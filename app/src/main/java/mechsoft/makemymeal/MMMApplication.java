package mechsoft.makemymeal;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;

import mechsoft.makemymeal.FCM.Config;

/**
 * Created by priyanka.shirke on 02/05/18.
 */

public class MMMApplication extends Application{

    private static MMMApplication instance;

    public static MMMApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_ALL_USERS);
    }
}
