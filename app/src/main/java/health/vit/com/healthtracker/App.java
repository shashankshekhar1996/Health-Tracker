package health.vit.com.healthtracker;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

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
    private AlarmManager alarmManager;
    private PendingIntent reminderBroadcast;
    private PendingIntent healthTipsBroadcast;

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
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //  Initialize SharedPreferences
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        e = getPrefs.edit();
        setEMAIL_ADDRESS(getPrefs.getString(Constants.PREFS_PROFILE_EMAIL, getResources().getString(R.string.default_email)));
        setUSERNAME(getPrefs.getString(Constants.PREFS_PROFILE_USERNAME, getResources().getString(R.string.default_username)));
        setPROFILE_PIC_URL(getPrefs.getString(Constants.PREFS_PROFILE_PIC_URL, null));
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
        e.putString(Constants.PREFS_PROFILE_PIC_URL, PROFILE_PIC_URL);
        e.apply();
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

    public void startNotifications() {

        if (getPrefs.getBoolean((Constants.PREFS_GET_NOTIFICATIONS), true)) {
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");


            Intent reminderIntent = new Intent("android.intent.action.REMINDERACTIVITY");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            healthTipsBroadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            reminderBroadcast = PendingIntent.getBroadcast(this, 101, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 14400000, healthTipsBroadcast);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 9000000, reminderBroadcast);
        }
    }

    public void stopNotifications() {
        alarmManager.cancel(healthTipsBroadcast);
        alarmManager.cancel(reminderBroadcast);
    }
}
