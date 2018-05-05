package mechsoft.makemymeal.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by priyanka on 10-03-2018.
 */


public class MMMPreferences {
    Context context;
    private static String mypreference = "MMMSharePreferences";
    private static MMMPreferences mmmUtils;
    private static SharedPreferences sharedpreferences;

    private MMMPreferences(Context context) {
        this.context = context;
    }

    public static MMMPreferences getInstance(Context context) {
        if (mmmUtils == null) {
            mmmUtils = new MMMPreferences(context);
            sharedpreferences = context.getSharedPreferences(mypreference,
                    Context.MODE_PRIVATE);
        }
        return mmmUtils;
    }

    public void savePreferences(String key, String value) {
        Log.i("savePreferences", "key"+key+" value"+value);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferences(String key, Boolean value) {
        Log.i("savePreferences", "key"+key+" value"+value);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String loadPreferences(String key) {
        String value = "";
        if (sharedpreferences.contains(key)) {
            value = sharedpreferences.getString(key, "");
        }
        Log.i("loadPreferences", "key"+key+" value"+value);
        return value;
    }

    public Boolean loadBoolPreferences(String key) {
        if (sharedpreferences.contains(key)) {
            Log.i("loadBoolPreferences", "key"+key);
            return sharedpreferences.getBoolean(key, false);
        }
        return false;
    }
}
