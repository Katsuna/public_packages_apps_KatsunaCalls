package com.katsuna.calls.ui.listeners;

import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;

public interface IContactResolver {
    Contact getCallContact(Call call);
}
