package com.google.firebase.udacity.friendlychat;

import com.google.firebase.database.ChildEventListener;

import android.net.Uri;

/**
 * Created by Eduard on 09/04/2017.
 */

public interface IRepository {

    interface RepositoryListener {
        void clearAllMessage();

        void newMessage(FriendlyMessage msg);

        void notifyUser(String msg);

        void requestAuthentication();

        void updateMsgLength(int length);
    }


    void setViewListener(RepositoryListener viewListener);

    void removeViewListener();


    void pushMessage(String msg);

    void pushImage(Uri imageUri);

    void attachAuthStateListener();

    void detachAuthStateListener();
}
