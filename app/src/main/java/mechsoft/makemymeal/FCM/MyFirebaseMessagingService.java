package mechsoft.makemymeal.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import mechsoft.makemymeal.View.HomeActivity;
import mechsoft.makemymeal.R;

/**
 * Created by priyanka.shirke on 30/04/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();

        //Collect Data
        /*String message="";
        if (remoteMessage.getNotification() != null) {
            message=remoteMessage.getNotification().getBody();
        }*/

        String navURL = "";
        String title = "";
        String body = "";
        String icon = "";


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                if (json.has("navigation_url"))
                    navURL = json.getString("navigation_url");
                if (json.has("title"))
                    title = json.getString("title");
                if (json.has("body"))
                    body = json.getString("body");
                if (json.has("icon"))
                    icon = json.getString("icon");
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = (int)System.currentTimeMillis();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); // Here pass your activity where you want to redirect.
        intent.putExtra("navURL", navURL);
        intent.putExtra("notificationId",notificationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //showNotificationMessage(getApplicationContext(),this.getResources().getString(R.string.app_name),message,"",intent);

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, 0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            currentapiVersion = R.mipmap.ic_mmm_launcher;
        } else {
            currentapiVersion = R.mipmap.ic_mmm_launcher;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(currentapiVersion)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);
        mNotificationManager.notify((int) notificationId, notificationBuilder.build());

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }*/
    }

    private void handleNotification(String message) {
        /*if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }*/

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long notificatioId = System.currentTimeMillis();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); // Here pass your activity where you want to redirect.
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //showNotificationMessage(getApplicationContext(),this.getResources().getString(R.string.app_name),message,"",intent);

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, 0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            currentapiVersion = R.mipmap.ic_mmm_launcher;
        } else {
            currentapiVersion = R.mipmap.ic_mmm_launcher;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(currentapiVersion)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);
        mNotificationManager.notify((int) notificatioId, notificationBuilder.build());


    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, 0);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, contentIntent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, 0);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl, contentIntent);
    }
}


