/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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
