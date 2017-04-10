package com.google.firebase.udacity.friendlychat;

import com.google.firebase.database.ChildEventListener;

import android.net.Uri;

/**
 * Created by Eduard on 09/04/2017.
 */

public interface IRepository {

    void pushMessage(FriendlyMessage msg);

    void pushMessage(String msg);

    void pushImage(Uri imageUri);

    void addChildEventListener(ChildEventListener listener);

    void removeChildEventListener(ChildEventListener listener);

    void onSignetOutCleanUp();

    void onSignInInitialize(String username);
}
