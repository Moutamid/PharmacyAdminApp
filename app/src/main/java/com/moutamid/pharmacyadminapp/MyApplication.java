package com.moutamid.pharmacyadminapp;

import android.app.Application;

import com.fxn.stash.Stash;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        FirebaseApp.initializeApp(this);
    }
}
