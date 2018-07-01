package com.khammami.imerolium.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.khammami.imerolium.BaseActivity;
import com.khammami.imerolium.R;
import com.khammami.imerolium.utilities.AppUtils;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    MaterialButton signInButton;
    ProgressBar mSignInProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = AppUtils.getGoogleSignInClient(this);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                updateUi(true);
            }
        });

        mSignInProgressBar = findViewById(R.id.sign_in_progress);

        TextView termsTextView = findViewById(R.id.login_terms_footer);
        termsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Google")
                        .putSuccess(true));
            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, "onActivityResult: error "+e.getMessage());
                if (!e.getMessage().contains("12501")) {
                    Toast.makeText(LoginActivity.this,
                            getString(R.string.toast_sign_in_uncatched_error),
                            Toast.LENGTH_SHORT).show();
                }

                // Google Sign In failed, update UI appropriately
                updateUi(false);
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Google")
                        .putSuccess(false));
            }
        }
    }

    private void updateUi(boolean isLoading) {
        if (isLoading){
            signInButton.setVisibility(View.GONE);
            mSignInProgressBar.setVisibility(View.VISIBLE);
        }else{
            signInButton.setVisibility(View.VISIBLE);
            mSignInProgressBar.setVisibility(View.GONE);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        //ProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                //new PeoplesAsync(LoginActivity.this).execute(account.getAccount());

                                Toast.makeText(LoginActivity.this,
                                        getString(R.string.toast_signed_as, user.getEmail()),
                                        Toast.LENGTH_SHORT)
                                        .show();

                                Answers.getInstance().logLogin(new LoginEvent()
                                        .putMethod("Firebase")
                                        .putSuccess(true));
                            }

                            redirectToMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.toast_sign_in_failed),
                                    Toast.LENGTH_SHORT).show();

                            updateUi(false);

                            Answers.getInstance().logLogin(new LoginEvent()
                                    .putMethod("Firebase")
                                    .putSuccess(false));
                        }

                        //hideProgressDialog();
                    }
                });
    }

    private void redirectToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
