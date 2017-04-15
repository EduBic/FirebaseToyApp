package com.google.firebase.udacity.friendlychat;

import android.net.Uri;

/**
 * Created by Eduard on 15/04/2017.
 */

public interface IPresenter {
    void start();

    void pause();

    void addNewMessage(String msg);

    void addNewMessage(Uri imagePath);
}
