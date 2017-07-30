package com.katsuna.calls.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
        circleDrawable.setColor(color1);

        Drawable missedCallIcon = ctx.getDrawable(R.drawable.ic_call_missed_black_24dp);

        if (profile.colorProfile == ColorProfile.CONTRAST) {
            DrawUtils.setColor(missedCallIcon, whiteResId);
        }

        Drawable[] layers = {circleDrawable, missedCallIcon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = ctx.getResources().getDimensionPixelSize(R.dimen.call_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);


        return layerDrawable;
    }

}
