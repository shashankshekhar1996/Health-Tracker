package health.vit.com.healthtracker;

import android.app.Application;

import health.vit.com.healthtracker.utilities.GoogleSignInHelper;

/**
 * Created by akshaymahajan on 29/01/17.
 */

public class App extends Application {
    private static App mInstance;
    private GoogleSignInHelper googleSignInHelper;
    private String EMAIL_ADDRESS;

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
    }

    public String getEMAIL_ADDRESS() {
        return EMAIL_ADDRESS;
    }

    public void setEMAIL_ADDRESS(String EMAIL_ADDRESS) {
        this.EMAIL_ADDRESS = EMAIL_ADDRESS;
    }

    public GoogleSignInHelper getGoogleSignInHelperInstance() {
        return this.googleSignInHelper;
    }
}
