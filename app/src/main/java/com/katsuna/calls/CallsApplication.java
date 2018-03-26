package com.katsuna.calls;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class CallsApplication extends Application {

    private static final String TAG = "CallsApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // disable firebase crash collection for debug
            FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

}
