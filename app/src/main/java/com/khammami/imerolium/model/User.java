package com.khammami.imerolium.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String displayName;
    private String email;
    private boolean emailVerified;
    private String photoURL;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(FirebaseUser user){
        this.uid = user.getUid();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.emailVerified = user.isEmailVerified();
        this.photoURL = user.getPhotoUrl().toString();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}

