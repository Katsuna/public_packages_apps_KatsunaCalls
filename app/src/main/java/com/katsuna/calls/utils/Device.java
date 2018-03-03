package com.katsuna.calls.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Device {

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    public static boolean hasAllPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasPermission(final Context context, final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(final Activity activity, final String permission,
                                          final int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
    }

    public static void requestPermissions(final Activity activity, final String[] permissions,
                                          final int requestCode) {
        String[] notGrantedPermissions = getNotGrantedPermissions(activity, permissions);

        if (notGrantedPermissions.length > 0) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions, requestCode);
        }
    }

    private static String[] getNotGrantedPermissions(Context context, String[] permissions) {
        List<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                notGrantedPermissions.add(permission);
            }
        }
        return notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
    }

}
