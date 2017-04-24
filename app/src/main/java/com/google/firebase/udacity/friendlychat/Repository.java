package com.google.firebase.udacity.friendlychat;

/**
 * Created by Eduard on 22/04/2017.
 */

public class Repository implements IDatabase.NetworkDatabaseListener, IDatabase.LocalDatabaseListener{

    public interface RepositoryListener {
        void onAuthentication(boolean success);
        void onFetchConfigFinish(int lengthMessage);
        void onDatabaseUpdate(FriendlyMessage msg);
    }

    // TODO change type of listener in Presenter
    private IDatabase.NetworkDatabaseListener mOldListener;
    private RepositoryListener mListener;

    LocalDatabase mLocalDb;
    NetworkDatabase mNetworkDb;

    public Repository() {
        mLocalDb = new LocalDatabase();
        mNetworkDb = new NetworkDatabase();

        mLocalDb.addListener(this);
        mNetworkDb.addListener(this);
    }

    // from network database
    @Override
    public void onAuthentication(boolean success) {

    }

    @Override
    public void onFetchConfigFinish(int lengthMessage) {

    }

    @Override
    public void onDatabaseUpdate(FriendlyMessage msg) {

    }

    // from local database
    @Override
    public void onLocalAuthentication(boolean success) {

    }

    @Override
    public void onFetchLocalConfigFinish(int lengthMessage) {

    }

    @Override
    public void onLocalDatabaseUpdate(FriendlyMessage msg) {

    }
}
