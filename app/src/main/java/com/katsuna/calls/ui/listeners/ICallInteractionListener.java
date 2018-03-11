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
