package health.vit.com.healthtracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import health.vit.com.healthtracker.utilities.Constants;
import health.vit.com.healthtracker.utilities.GoogleSignInHelper;

/**
 * Created by akshaymahajan on 29/01/17.
 */

public class App extends Application {
    private static App mInstance;
    private String PROFILE_PIC_URL;
    private GoogleSignInHelper googleSignInHelper;
    private String EMAIL_ADDRESS, USERNAME;
    private SharedPreferences getPrefs;
    private SharedPreferences.Editor e;

    public static synchronized App getInstance() {
        return mInstance;
    }

    public static GoogleSignInHelper getGoogleSignInHelper() {
        return getInstance().getGoogleSignInHelperInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        googleSignInHelper = new GoogleSignInHelper(mInstance);

        //  Initialize SharedPreferences
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        e = getPrefs.edit();
        setEMAIL_ADDRESS(getPrefs.getString(Constants.PREFS_PROFILE_EMAIL, getResources().getString(R.string.default_email)));
        setUSERNAME(getPrefs.getString(Constants.PREFS_PROFILE_USERNAME, getResources().getString(R.string.default_username)));
    }

    public String getEMAIL_ADDRESS() {
        return EMAIL_ADDRESS;
    }

    public void setEMAIL_ADDRESS(String EMAIL_ADDRESS) {
        this.EMAIL_ADDRESS = EMAIL_ADDRESS;

        e.putString(Constants.PREFS_PROFILE_EMAIL, EMAIL_ADDRESS);
        e.apply();
    }

    public String getPROFILE_PIC_URL() {
        return PROFILE_PIC_URL;
    }

    public void setPROFILE_PIC_URL(String PROFILE_PIC_URL) {
        this.PROFILE_PIC_URL = PROFILE_PIC_URL;
    }

    public GoogleSignInHelper getGoogleSignInHelperInstance() {
        return this.googleSignInHelper;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
        e.putString(Constants.PREFS_PROFILE_USERNAME, USERNAME);
        e.apply();
    }
}
