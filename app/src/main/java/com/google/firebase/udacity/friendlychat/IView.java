package com.google.firebase.udacity.friendlychat;

import android.support.annotation.NonNull;

/**
 * Created by Eduard on 15/04/2017.
 */

public interface IView {
        void clearAllMessage();

        void newMessage(FriendlyMessage msg);

        void notifyUser(String msg);

        void requestAuthentication();

        void updateMsgLength(int length);

        void setPresenter(@NonNull IPresenter presenter);
}
