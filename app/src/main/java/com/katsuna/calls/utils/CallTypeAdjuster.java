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

import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.CallLog;
import android.support.v7.widget.CardView;
import android.view.View;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalcV2;

public class CallTypeAdjuster {

    public static void adjustCardLayout(Context context, int callType, CardView cardView,
                                        View innerContainer, UserProfile userProfile) {
        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        ColorProfile colorProfile = userProfile.colorProfile;
        if (callType == CallLog.Calls.INCOMING_TYPE) {
            cardColor = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_COLOR_2,
                    colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(context, ColorProfileKeyV2.SECONDARY_COLOR_2,
                    colorProfile);
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            cardColor = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_GREY_1,
                    colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(context, ColorProfileKeyV2.SECONDARY_GREY_2,
                    colorProfile);
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            cardColor = ColorCalcV2.getColor(context, ColorProfileKeyV2.PRIMARY_COLOR_1,
                    colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(context, ColorProfileKeyV2.SECONDARY_COLOR_1,
                    colorProfile);
        }

        // set colors
        if (cardColor != 0) {
            cardView.setCardBackgroundColor(ColorStateList.valueOf(cardColor));
            innerContainer.setBackgroundColor(cardColorAlpha);
        }
    }

}
