package com.katsuna.calls.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;

import com.katsuna.calls.R;

public class CallTypeAdjuster {

    public static void adjustCardLayout(Context context, int callType, CardView cardView,
                                        View innerContainer) {
        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        if (callType == CallLog.Calls.INCOMING_TYPE) {
            cardColor = R.color.priority_two;
            cardColorAlpha = R.color.priority_two_tone_one;
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            cardColor = R.color.priority_one;
            cardColorAlpha = R.color.priority_one_tone_one;
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            cardColor = R.color.priority_three;
            cardColorAlpha = R.color.priority_three_tone_one;
        }

        // set colors
        if (cardColor != 0) {
            cardView.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(context, cardColor)));
            innerContainer.setBackgroundColor(
                    ContextCompat.getColor(context, cardColorAlpha));
        }
    }

}
