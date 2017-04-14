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
    }

    void setListener(RepositoryListener listener);

    void removeListener();

    void pushMessage(FriendlyMessage msg);

    void pushMessage(String msg);

    void pushImage(Uri imageUri);

    void addChildEventListener(ChildEventListener listener);

    void removeChildEventListener(ChildEventListener listener);

    void attachAuthStateListener();

    void detachAuthStateListener();

    void onSignetOutCleanUp();

    void onSignInInitialize(String username);
}
