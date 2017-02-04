package health.vit.com.healthtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Created by akshaymahajan on 28/01/17.
 */

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("Health Monitoring System", "We will take care of you.",
                R.drawable.ic_heart_white, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntro2Fragment.newInstance("Graphs", "Analyse your health",
                R.drawable.ic_intro_graph, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntro2Fragment.newInstance("Health Tips", "Reach your fitness goals and remain healthy forever.",
                R.drawable.ic_run, getResources().getColor(R.color.colorPrimary)));

        showStatusBar(false);
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setFadeAnimation();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        /** set isFirst to false */
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();

        //  Edit preference to make it false because we don't want this to run again
        e.putBoolean("firstStart", false);
        e.apply();

        //end Activity
        finish();
        // Launch Home Activity
        startActivity(new Intent(IntroActivity.this, GoogleSignInActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
