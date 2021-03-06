package com.google.firebase.udacity.friendlychat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Eduard on 09/04/2017.
 */
public class Repository implements IRepository {

    public static final String TAG = "Repository";

    private static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";

    //TODO: use a list of listeners
    private RepositoryListener mListener;

    // fb database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    // fb storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    // fb authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // fb remote config
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    // fb util data
    private String mUsername = DEFAULT_USERNAME;


    public Repository() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // TODO: Are we sure to comment this here?
//        this.initAuthListener();
//        this.initRemoteConfig();
    }

    @Override
    public void addListener(RepositoryListener listener) {
        mListener = listener;
        this.initAuthListener();
        this.initRemoteConfig();
    }


    private void initAuthListener() {
        if (mListener != null) {
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // check if user is authenticated if not show screen of login
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    if (user != null) {
                        mListener.onAuthentication(true);
                        onSignInInitialize(user.getDisplayName());
                    }
                    else { // user sign out
                        onSignetOutCleanUp();
                        mListener.onAuthentication(false);
                    }
                }
            };
        }
        else {
            Log.w("InitAuthListener", "No listener of repository");
        }
    }

    private void onSignInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            // TODO: extract Listener to class
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // match the field get in json object (DataSnapshot)
                    FriendlyMessage newFriendlyMsg = dataSnapshot.getValue(FriendlyMessage.class);

                    if (mListener != null) {
                        mListener.onDatabaseUpdate(newFriendlyMsg);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            // listen changing in data from messages root
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void onSignetOutCleanUp() {
        mUsername = DEFAULT_USERNAME;
        detachAuthStateListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
        mChildEventListener = null;
    }


    private void initRemoteConfig() {
        if (mListener != null) {
            FirebaseRemoteConfigSettings config = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettings(config);

            Map<String, Object> defaultConfigMap = new HashMap<>();
            defaultConfigMap.put(FRIENDLY_MSG_LENGTH_KEY, DEFAULT_MSG_LENGTH_LIMIT);

            mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

            fetchConfig();
        }
        else {
            Log.w("InitRemoteConfig", "No listener of repository");
        }
    }

    private void fetchConfig() {
        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error fetching config", e);
                        applyRetrievedLengthLimit();
                    }
                });
    }

    private void applyRetrievedLengthLimit() {
        Long friendlyMsgLength = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGTH_KEY);
        mListener.onFetchConfigFinish(friendlyMsgLength.intValue());

        Log.d(TAG, FRIENDLY_MSG_LENGTH_KEY + "=" + friendlyMsgLength);
    }


    @Override
    public void pushMessage(String textMsg) {
        FriendlyMessage friendlyMessage =
                new FriendlyMessage(textMsg, mUsername, null);

        this.pushMessage(friendlyMessage);
    }

    @Override
    public void pushImage(Uri imageUri) {
        // get reference to storage
        StorageReference photoRef = mStorageReference.child(imageUri.getLastPathSegment());

        // upload file to fb N.B. this part is syncronous
        // TODO: better use executor for the listener?
        photoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FriendlyMessage msg = new FriendlyMessage(null, mUsername, downloadUrl.toString());

                        // give an unique id and push to database
                        pushMessage(msg);
                    }
                });
    }

    private void pushMessage(FriendlyMessage msg) {
        if (mDatabaseReference != null) {
            mDatabaseReference.push().setValue(msg);
        }
    }


    @Override
    public void attachAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void detachAuthStateListener() {
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
        this.detachDatabaseReadListener();
    }
}
