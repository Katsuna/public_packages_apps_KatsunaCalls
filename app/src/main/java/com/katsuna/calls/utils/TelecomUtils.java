package com.katsuna.calls.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.util.Log;

/**
 * Helper class to handle calls related telecom operations.
 */

public class TelecomUtils {

    private static final String TAG = "TelecomUtils";

    public static boolean hasModifyPhoneStatePermission(Context context) {
        return hasPermission(context, Manifest.permission.MODIFY_PHONE_STATE);
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static void cancelMissedCallsNotification(Context context) {
        try {
            getTelecomManager(context).cancelMissedCallsNotification();
        } catch (SecurityException e) {
            Log.w(TAG, "TelecomManager.cancelMissedCalls called without permission.");
        }
    }

    private static TelecomManager getTelecomManager(Context context) {
        return (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }

}
