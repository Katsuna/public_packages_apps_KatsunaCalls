package gr.crystalogic.calls;

import gr.crystalogic.commons.framework.BaseApplication;
import gr.crystalogic.commons.utils.Log;

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
