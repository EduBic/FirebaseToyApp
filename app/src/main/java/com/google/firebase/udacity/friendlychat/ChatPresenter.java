package com.google.firebase.udacity.friendlychat;

import android.net.Uri;

/**
 * Created by Eduard on 15/04/2017.
 */

public class ChatPresenter implements IPresenter, IDatabase.NetworkDatabaseListener {

    private IDatabase mRepository;

    private IView mView;

    public ChatPresenter(IDatabase repository, IView view) {
        mRepository = repository;
        mView = view;

        // attach to Fragment and NetworkDatabase
        mView.setPresenter(this);
        mRepository.addListener(this);
    }

    @Override
    public void start() {
        mRepository.attachAuthStateListener();
    }

    @Override
    public void pause() {
        mRepository.detachAuthStateListener();
    }

    // call to repository
    @Override
    public void addNewMessage(String msg) {
        mRepository.pushMessage(msg);
    }

    @Override
    public void addNewMessage(Uri imagePath) {
        mRepository.pushImage(imagePath);
    }

    // call from repository
    @Override
    public void onAuthentication(boolean success) {
        if (success) {
            mView.notifyUser("Sign in");
        }
        else {
            mView.clearAllMessage();
            mView.requestAuthentication();
        }
    }

    @Override
    public void onFetchConfigFinish(int lengthMessage) {
        mView.updateMsgLength(lengthMessage);
    }

    @Override
    public void onDatabaseUpdate(FriendlyMessage msg) {
        mView.newMessage(msg);
    }

}
