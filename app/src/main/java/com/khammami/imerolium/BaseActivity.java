package com.khammami.imerolium;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khammami.imerolium.ui.LoginActivity;
import com.khammami.imerolium.ui.MainActivity;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        checkAuth();
    }

    public void checkAuth() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!(this  instanceof LoginActivity) && currentUser == null) {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            startActivity(signInIntent);
            finish();
        }else if((this  instanceof LoginActivity) && currentUser != null){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
