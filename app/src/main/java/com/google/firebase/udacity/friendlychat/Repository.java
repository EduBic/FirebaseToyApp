package com.google.firebase.udacity.friendlychat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

/**
 * Created by Eduard on 09/04/2017.
 */

public class Repository implements IRepository {

    public static final String ANONYMOUS = "anonymous";

    private RepositoryListener listener;

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

    // fb util data
    private String mUsername;

    public Repository() {
        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        mFirebaseAuth = FirebaseAuth.getInstance();

        this.initAuthListener();
    }

    private void initAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // check if user is authenticated if not show screen of login
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    listener.notifyUser("Sign in");

                    onSignInInitialize(user.getDisplayName());
                    attachDatabaseReadListener();
                }
                else {
                    // user sign out
                    onSignetOutCleanUp();

                    listener.clearAllMessage();

                    detachDatabaseReadListener();

                    // -> use Firebase UI
                    listener.requestAuthentication();
                }
            }
        };
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // match the field get in json object (DataSnapshot)
                    FriendlyMessage newFriendlyMsg = dataSnapshot.getValue(FriendlyMessage.class);
                    // update view with adapter
                    listener.newMessage(newFriendlyMsg);
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
            //mDatabaseReference.addChildEventListener(mChildEventListener);
            addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            //mDatabaseReference.removeEventListener(mChildEventListener);
            removeChildEventListener(mChildEventListener);
        }
        mChildEventListener = null;
    }

    @Override
    public void setListener(RepositoryListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener() {
        this.listener = null;
    }

    @Override
    public void pushMessage(FriendlyMessage msg) {
        if (mDatabaseReference != null) {
            mDatabaseReference.push().setValue(msg);
        }
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

        // upload file to fb N.B. this part is syncronous TODO: make it async
        photoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FriendlyMessage msg = new FriendlyMessage(null, mUsername, downloadUrl.toString());

                        // give an unique id and push to database
                        //mDatabaseReference.push().setValue(msg);
                        pushMessage(msg);
                    }
                });
    }

    @Override
    public void addChildEventListener(ChildEventListener listener) {
        // listen changing in data from messages root
        mDatabaseReference.addChildEventListener(listener);
    }

    @Override
    public void removeChildEventListener(ChildEventListener listener) {
        mDatabaseReference.removeEventListener(listener);
    }

    @Override
    public void attachAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void detachAuthStateListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        this.detachDatabaseReadListener();
    }

    @Override
    public void onSignInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    @Override
    public void onSignetOutCleanUp() {
        mUsername = ANONYMOUS;
    }
}
