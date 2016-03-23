package gr.crystalogic.calls.ui.listeners;

import gr.crystalogic.calls.domain.Call;

public interface ICallInteractionListener {
    void callContact(Call call);

    void sendSMS(Call call);
}
