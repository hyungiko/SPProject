package jordan.spproject.reference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by hyungiko on 8/8/18.
 */

public class GlobalVariable {
    static public final String sharedName	= "SP Project";
    static public final String ONLINE_PREVENTOR_UPDATE 	= "preventorUpdate";
    static public final String EMOJI_UPDATE 	= "emojiUpdate";
    static public final String SLEEP_UPDATE 	= "sleepUpdate";
    static public final String RATE_UPDATE 	= "rateUpdate";
    static public final String LIST_PREVENTOR = "listPreventor";
    static public final String LIST_HISTORY = "listPreventor";
    static public final String LIST_RATE = "listRate";
    static public final String LIST_SURVEY = "listSurvey";
    static public final String EMOJI_MSG = "emoji_msg";
    static public final String SLEEP_MSG = "sleep_msg";
    static public final String GREETING_MSG = "Hi, how are you?";
    static public final String START_MSG = "@@start";
    static public final String REJECT_MSG = "@@reject";
    static public final String ACCEPT_MSG = "@@accept";
    static public final String EXIT_MSG = "@@exit";
    static public final String PREVENTOR_EXIT_MSG = "@@preventor_exit";
    static public final String REQUEST_MSG = "Matching a Preventor...";

    public static boolean isJustSignedUp = false;
    public static String keyUserType = "userType";
    public static String keyList = "list";
    public static String keyOnline = "online";
    public static String keyTrue = "true";
    public static String keyFalse = "false";
    public static String keySurvey = "survey";

    public static String keyEmailId = "emailId";
    public static String keyPreventor = "preventor";
    public static String keyPatient = "patient";
    public static String keyAvailable = "available";
    public static String keyUnAvailable = "unavailable";
    public static String keyChatRoom = "chatRoom";

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

    public static String getDate() {
        Calendar c  = Calendar.getInstance();
        int year	= c.get(Calendar.YEAR);
        int month 	= c.get(Calendar.MONTH)+1;
        int day		= c.get(Calendar.DAY_OF_MONTH);

        String timeWindow = String.format("%02d%02d_%02d", month, day, year);
        return timeWindow;
    }
}
