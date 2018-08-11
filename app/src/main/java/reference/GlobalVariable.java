package reference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hyungiko on 8/8/18.
 */

public class GlobalVariable {
    static public final String sharedName	= "SP Project";

    public static boolean isJustSignedUp = false;
    public static String keyUserType = "userType";
    public static String keyList = "list";
    public static String keyOnline = "online";
    public static String keyTrue = "true";
    public static String keyFalse = "false";

    public static String keyEmailId = "emailId";
    public static String keyPreventor = "preventor";
    public static String keyPatient = "patient";
    public static String keyAvailable = "available";
    public static String keyUnAvailable = "unavailable";

    // savePreferences
    public static void saveStringPreferences(Context mContext, String key, String value) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences(sharedName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        editor.putString(key, value);
        editor.commit();
    }

    // loadPreferences
    public static String loadPreferences(Context mContext, String key) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences(sharedName, Activity.MODE_PRIVATE);
        String getProvedId = mySharedPreferences.getString(key, null);

        return getProvedId;
    }

    public static void removePreferences(Context mContext, String key) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences(sharedName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
