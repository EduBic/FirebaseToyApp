package com.google.firebase.udacity.friendlychat;

import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * Created by Eduard on 09/04/2017.
 */

public class Repository implements IRepository {

    public static final String ANONYMOUS = "anonymous";

    // fb database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    // fb storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    // fb util data
    private String mUsername;

    public Repository() {
        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("chat_photos");
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
    public void onSignInInitialize(String username) {
        mUsername = username;
        // this.AttachDatabaseReadListener();
    }

    @Override
    public void onSignetOutCleanUp() {
        mUsername = ANONYMOUS;
    }
}
