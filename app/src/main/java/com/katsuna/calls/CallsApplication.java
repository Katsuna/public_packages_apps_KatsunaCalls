package com.katsuna.calls;

import com.katsuna.commons.framework.BaseApplication;
import com.katsuna.commons.utils.Log;

public class CallsApplication extends BaseApplication {

    private static final String APP_NAME = "Calls";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.initialize(getApplicationContext(), APP_NAME, true);
    }

    @Override
    protected void handleException(Throwable throwable) {
        Log.e(this, throwable.getMessage());
        System.exit(1);
    }
}
