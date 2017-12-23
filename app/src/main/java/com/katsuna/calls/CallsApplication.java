package com.katsuna.calls;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;

public class CallsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // disable firebase crash collection for debug
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }

}
