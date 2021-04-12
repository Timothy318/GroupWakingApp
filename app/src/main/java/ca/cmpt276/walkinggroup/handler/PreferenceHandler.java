package ca.cmpt276.walkinggroup.handler;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.activity.LoginActivity;
import ca.cmpt276.walkinggroup.activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Handles all the preference calls made in the application
 *
 * takes in model Data and saves it to the device to enable automatic login
 */

public class PreferenceHandler {
    private static final String TOKEN_TAG = MainActivity.getUserLoggedInTokenTag();
    private static final String EMAIL_TAG = MainActivity.getUserLoggedInEmailTag();
    private static final String ID_TAG = MainActivity.getUserLoggedInIdTag();

    private static PreferenceHandler instance = null;

    private SharedPreferences pref = null;

    private PreferenceHandler() {
        // Do nothing
    }

    public static PreferenceHandler getInstance() {
        if (instance == null)
            instance = new PreferenceHandler();

        return instance;
    }

    public SharedPreferences getSharedPreference(Context context) {
        if (pref == null)
            pref = context.getSharedPreferences(context.getResources().getString(R.string.prefName)
                    , MODE_PRIVATE);

        return pref;
    }

    public String getLoggedInUserToken(Context context) {
        return getSharedPreference(context)
                .getString(TOKEN_TAG, null);
    }

    public String getLoggedInUserEmail(Context context) {
        return getSharedPreference(context)
                .getString(EMAIL_TAG, null);
    }

    public Long getLoggedInUserId(Context context) {
        return getSharedPreference(context)
                .getLong(ID_TAG, -1);
    }

    public void saveUserEmail(Context context, String email) {
        SharedPreferences.Editor editor = PreferenceHandler
                .getInstance()
                .getSharedPreference(context).edit();

        editor.putString(EMAIL_TAG, email);
        editor.apply();
    }

    public void saveUserId(Context context, Long id) {
        SharedPreferences.Editor editor = PreferenceHandler
                .getInstance()
                .getSharedPreference(context).edit();

        editor.putLong(ID_TAG, id);
        editor.apply();
    }

    public void saveUserToken(Context context, String token) {
        SharedPreferences.Editor editor = PreferenceHandler
                .getInstance()
                .getSharedPreference(context).edit();

        editor.putString(TOKEN_TAG, token);
        editor.apply();
    }

    public void removeSavedId(Context context) {
        PreferenceHandler.getInstance()
                .getSharedPreference(context)
                .edit()
                .remove(ID_TAG)
                .apply();
    }

    public void removeSavedToken(Context context) {
        PreferenceHandler.getInstance()
                .getSharedPreference(context)
                .edit()
                .remove(TOKEN_TAG)
                .apply();
    }

    public void removeSavedEmail(Context context) {
        PreferenceHandler.getInstance()
                .getSharedPreference(context)
                .edit()
                .remove(EMAIL_TAG)
                .apply();
    }
}
