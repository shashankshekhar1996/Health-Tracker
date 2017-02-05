package health.vit.com.healthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by akshaymahajan on 28/01/17.
 */

public class GoogleSignInActivity extends AppCompatActivity {


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInOptions gso = null;
    private GoogleApiClient mGoogleApiClient = null;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);

        progressBar = (ProgressBar) findViewById(R.id.pb_signin);

        firebaseInit();

        //Check if connected
        if (App.getGoogleSignInHelper().isConnected()) {
            //Get google api client
            Log.i("GoogleSignInActivity", "Google Api Client is connected.");
            mGoogleApiClient = App.getGoogleSignInHelper().getApiClient();
        } else {
            Log.i("GoogleSignInActivity", "Google Api Client is disconnected.");
            App.getGoogleSignInHelper().setApiClient();
            mGoogleApiClient = App.getGoogleSignInHelper().getApiClient();
            App.getGoogleSignInHelper().connect();
        }


        SignInButton btn_google_sign_in = (SignInButton) findViewById(R.id.btn_google_signin);
        btn_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void firebaseInit() {
        /** Firebase Authentication Listener */
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(GoogleSignInActivity.this, MainActivity.class));
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * Retrieve User profile details and set username,email,profile pic
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                System.out.println("USER NAME = " + account.getDisplayName() + account.getPhotoUrl());

                App.getInstance().setEMAIL_ADDRESS(account.getEmail());
                App.getInstance().setUSERNAME(account.getDisplayName());
                App.getInstance().setPROFILE_PIC_URL(account.getPhotoUrl().toString());

              /*  *//** G+ profile *//*
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String gender = person.getGender() == 0 ? "male" : "female";
                Log.i("Profile", gender + person.getAgeRange().getMin());*/

                //TODO: Send details in bundle
                //downloadProfilePic(account.getPhotoUrl().toString());

                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google Sign in Failed.", Toast.LENGTH_LONG).show();
                System.out.println("GOOGLE SIGN IN FAILED.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            String exception = task.getException().getClass().getSimpleName();
                            if (exception.equals("FirebaseNetworkException"))
                                Toast.makeText(GoogleSignInActivity.this, "Check your Internet connection.",
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            /** Sign in was Successful, go to home page and set profile details in header */
                            finishAffinity();
                            startActivity(new Intent(GoogleSignInActivity.this, MainActivity.class));
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
