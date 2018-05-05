package mechsoft.makemymeal.Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by priyanka on 10-03-2018.
 */

public class NetUtils {
    private Context mContext;
    private  static NetUtils netUtils;
    private NetUtils(Context context) {
        this.mContext=context;
    }

    public static NetUtils getInstance(Context context) {
        if (netUtils == null) {
            netUtils = new NetUtils(context);
        }
        return netUtils;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
