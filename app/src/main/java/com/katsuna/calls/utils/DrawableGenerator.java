package com.katsuna.calls.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;

import com.katsuna.calls.R;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.DrawUtils;

public class DrawableGenerator {

    public static Drawable getMissedCallDrawable(Context ctx, UserProfile profile) {

        int color1 = ColorCalc.getColor(ctx, ColorProfileKey.ACCENT1_COLOR, profile.colorProfile);
        int color2 = ColorCalc.getColor(ctx, ColorProfileKey.ACCENT2_COLOR, profile.colorProfile);
        int whiteResId = ContextCompat.getColor(ctx, com.katsuna.commons.R.color.common_white);
        int blackResId = ContextCompat.getColor(ctx, com.katsuna.commons.R.color.common_black);

        GradientDrawable circleDrawable =
                (GradientDrawable) ctx.getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(color1);
        }

        Drawable missedCallIcon = ctx.getDrawable(R.drawable.ic_call_missed_black_24dp);

        if (profile.colorProfile == ColorProfile.CONTRAST) {
            DrawUtils.setColor(missedCallIcon, whiteResId);
        }

        Drawable[] layers = {circleDrawable, missedCallIcon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = ctx.getResources().getDimensionPixelSize(R.dimen.item_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);


        return layerDrawable;
    }

    public static Drawable getCallTypeDrawable(Context ctx, int callType) {

        // calc color and icon
        int circleColorId = R.color.priority_one;
        int iconId = R.drawable.ic_call_made_black_24dp;

        if (callType == CallLog.Calls.INCOMING_TYPE) {
            circleColorId = R.color.priority_two;
            iconId = R.drawable.ic_call_received_black_24dp;
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            circleColorId = R.color.priority_one;
            iconId = R.drawable.ic_call_made_black_24dp;
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            circleColorId = R.color.priority_three;
            iconId = R.drawable.ic_call_missed_black_24dp;
        }

        // adjust circle
        GradientDrawable circleDrawable =
                (GradientDrawable) ctx.getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(ContextCompat.getColor(ctx, circleColorId));
        }

        // adjust icon
        Drawable icon = ctx.getDrawable(iconId);
        int black87 = ContextCompat.getColor(ctx, com.katsuna.commons.R.color.common_black87);
        DrawUtils.setColor(icon, black87);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = ctx.getResources().getDimensionPixelSize(R.dimen.item_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);

        return layerDrawable;
    }

}
