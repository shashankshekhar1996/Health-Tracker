package health.vit.com.healthtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by akshaymahajan on 28/01/17.
 */
public final class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Started fetching prefs");
        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  If the activity has never started before...
        if (isFirstStart) {
            //  Launch app intro
            startActivity(new Intent(BlankActivity.this, IntroActivity.class));
        } else {
            // Launch Home Page
            startActivity(new Intent(BlankActivity.this, GoogleSignInActivity.class));
        }
        System.out.println("Done fetching prefs");
    }
}
