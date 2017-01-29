package health.vit.com.healthtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import health.vit.com.healthtracker.utilities.Constants;

/**
 * Created by akshaymahajan on 26/01/17.
 */

public class PreferencesScreen extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        /** Set User email address in prefs */
        Preference prefs_connected_account = findPreference(Constants.PREFS_CONNECTED_ACCOUNT);
        String prefs_profile_email = getPrefs.getString(Constants.PREFS_PROFILE_EMAIL,
                getResources().getString(R.string.default_email));
        prefs_connected_account.setSummary(prefs_profile_email);

        /** Sign out */
        Preference prefs_change_account = findPreference(Constants.PREFS_CHANGE_ACCOUNT);
        prefs_change_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                return true;
            }
        });

        /** Disconnect Account */
        Preference prefs_disconnect_account = findPreference(Constants.PREFS_DISCONNECT_ACCOUNT);
        prefs_disconnect_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                revokeAccess();
                return true;
            }
        });
    }

    private void signOut() {
        // Firebase sign out

        FirebaseAuth.getInstance().signOut();
        GoogleApiClient mGoogleApiClient = App.getGoogleSignInHelper().getApiClient();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.i("Preferences", "Successfully Signed out.");
                        Toast.makeText(PreferencesScreen.this, "Signed out.", Toast.LENGTH_SHORT).show();
                    }
                });
        resetApp();
    }

    private void revokeAccess() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        GoogleApiClient mGoogleApiClient = App.getGoogleSignInHelper().getApiClient();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.i("Preferences", "Successfully Disconnected.");
                        Toast.makeText(PreferencesScreen.this, "Disconnected account from this app.", Toast.LENGTH_SHORT).show();
                    }
                });
        resetApp();
    }

    /**
     * Reset username,email,pic,etc.
     */
    private void resetApp() {
        finishAffinity();
        startActivity(new Intent(PreferencesScreen.this, GoogleSignInActivity.class));
    }
}
