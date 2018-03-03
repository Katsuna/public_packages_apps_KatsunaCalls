package com.katsuna.calls.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;

import com.katsuna.calls.R;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.DrawUtils;

public class DrawableGenerator {

    public static Drawable getCallTypeDrawable(Context context, int callType,
                                               UserProfile userProfile) {
        // calc color and icon
        ColorProfile colorProfile = userProfile.colorProfile;
        int primaryColor1 = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_COLOR_1,
                colorProfile);
        int primaryColor2 = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_COLOR_2,
                colorProfile);
        int grey_color_1 = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_GREY_1,
                colorProfile);

        int iconId = R.drawable.ic_call_made_black_24dp;
        int iconColor = ContextCompat.getColor(context, R.color.common_black87);
        int iconBackground = 0;

        if (callType == CallLog.Calls.INCOMING_TYPE) {
            iconId = R.drawable.ic_call_received_white87_24dp;
            iconColor = ContextCompat.getColor(context, R.color.common_white);
            iconBackground = primaryColor2;
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            iconId = R.drawable.ic_call_made_black_24dp;
            if (colorProfile == ColorProfile.COLOR_IMPAIRMENT_AND_CONTRAST) {
                iconColor = ContextCompat.getColor(context, R.color.common_white);
            }
            iconBackground = grey_color_1;
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            iconId = R.drawable.ic_call_missed_black_24dp;
            iconBackground = primaryColor1;
        }

        // adjust circle
        GradientDrawable circleDrawable =
                (GradientDrawable) context.getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(iconBackground);
        }

        // adjust icon
        Drawable icon = context.getDrawable(iconId);
        DrawUtils.setColor(icon, iconColor);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = context.getResources().getDimensionPixelSize(R.dimen.item_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);

        return layerDrawable;
    }

}
