package mechsoft.makemymeal.FCM;

/**
 * Created by priyanka.shirke on 30/04/18.
 */

public class Config {
    // global topic to receive app wide push notifications
    public static final String TOPIC_ALL_USERS = "all_users";
    public static final String TOPIC_ANONYMOUS_USERS = "anonymous_users";
    public static final String TOPIC_LOGGED_USERS = "logged_in_users";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
