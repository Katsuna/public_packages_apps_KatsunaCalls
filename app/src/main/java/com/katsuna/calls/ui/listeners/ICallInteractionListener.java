package com.katsuna.calls.ui.listeners;

import com.katsuna.calls.domain.Call;

public interface ICallInteractionListener {
    void callContact(Call call);

    void sendSMS(Call call);

    void createContact(Call call);
}
