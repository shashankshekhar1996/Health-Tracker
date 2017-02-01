package health.vit.com.healthtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private static int PICK_CONTACT = 0;

    SharedPreferences getPrefs;
    SharedPreferences.Editor editor;

    private String Phone = "phoneKey";
    private String Name = "nameKey";
    private String already_added = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //  Initialize SharedPreferences
        getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());


        editor = getPrefs.edit();

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

        /** Set an Emergency Contact */
        Preference prefs_emergency_contact = findPreference(Constants.PREFS_EMERGENCY_CONTACT);
        prefs_emergency_contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /** ask for reading_contacts permission, then continue */
                askForPermission();
                return true;
            }
        });
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(PreferencesScreen.this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PreferencesScreen.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            pickContactAlertDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    /** Permission Granted */
                    pickContactAlertDialog();

                } else {
                    /** Permission Denied */
                    Toast.makeText(this, "Grant Read_Contact permission to enable this feature.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void pickContactAlertDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreferencesScreen.this);

        if (getPrefs.contains(Name)) {

            already_added = getPrefs.getString(Name, "");
            System.out.println(already_added);
        }

        // set dialog message
        alertDialogBuilder
                .setTitle("Emergency Contacts")
                .setCancelable(true);

        if (already_added == null || already_added.isEmpty()) {
            alertDialogBuilder
                    .setMessage("No Contacts yet   :/")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickContact();
                        }
                    });
        } else {
            alertDialogBuilder
                    .setMessage(already_added)
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickContact();
                        }
                    });
        }

        alertDialogBuilder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                already_added = null;
                editor.remove(Name);
                editor.remove(Phone);
                editor.apply();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void pickContact() {
        //Start activity to get contact
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, PICK_CONTACT);
    }

    private void signOut() {
        AlertDialog.Builder builder = showConfirmDialog("Sign out ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Firebase sign out */
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
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void revokeAccess() {

        AlertDialog.Builder builder = showConfirmDialog("Disconnect from this app ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Firebase sign out */
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
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private AlertDialog.Builder showConfirmDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesScreen.this);
        builder.setTitle("Confirm").setMessage(msg).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    /**
     * Reset username,email,pic,etc.
     */
    private void resetApp() {
        editor.clear();
        /* required so that IntroActivity is not launched */
        editor.putBoolean("firstStart", false);
        editor.apply();
        finishAffinity();
        startActivity(new Intent(PreferencesScreen.this, GoogleSignInActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CONTACT) {
                Uri returnUri = data.getData();
                Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);

                if (cursor.moveToNext()) {
                    int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactID = cursor.getString(columnIndex_ID);

                    int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);

                    if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                        Cursor cursorNum = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID,
                                null,
                                null);

                        //Get the first phone number
                        if (cursorNum.moveToNext()) {

                            int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String stringNumber = cursorNum.getString(columnIndex_number);
                            int columnIndex_name = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            String stringName = cursorNum.getString(columnIndex_name);

                            editor.putString(Phone, stringNumber);
                            editor.putString(Name, stringName);
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "Added " + stringName + stringNumber + " to Emergency contacts", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No Phone Number!", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "No data!", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("TAG", "OnActivityResult Error");
            }

        }
    }

}
