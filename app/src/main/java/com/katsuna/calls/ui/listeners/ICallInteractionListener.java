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
package com.katsuna.calls.ui.listeners;

import com.katsuna.calls.domain.Call;
import com.katsuna.commons.entities.UserProfileContainer;

public interface ICallInteractionListener extends IContactResolver {

    void selectCall(int position);

    void focusCall(int position);

    void callContact(Call call);

    void sendSMS(Call call);

    void createContact(Call call);

    void addToContact(Call call);

    void editContact(Call call);

    void showCallDetails(Call call);

    void deleteCall(Call call);

    UserProfileContainer getUserProfileContainer();
}
