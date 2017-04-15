package com.google.firebase.udacity.friendlychat;

import com.google.firebase.database.ChildEventListener;

import android.net.Uri;

/**
 * Created by Eduard on 09/04/2017.
 */

public interface IRepository {

    String DEFAULT_USERNAME = "anonymous";
    int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    interface RepositoryListener {
        void onAuthentication(boolean success);

        void onFetchConfigFinish(int lengthMessage);    //TODO: make a data object

        void onDatabaseUpdate(FriendlyMessage msg);
    }

    void pushMessage(String msg);

    void pushImage(Uri imageUri);

    void addListener(RepositoryListener listener);

    void attachAuthStateListener();

    void detachAuthStateListener();
}
