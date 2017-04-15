/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

<<<<<<< HEAD
=======
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";

>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity
    // flag for return activity
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

    /*private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
<<<<<<< HEAD
    private Button mSendButton;*/

    private IRepository mRepository;

=======
    private Button mSendButton;

    private String mUsername;

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
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: remove UI from MainActivity
        setContentView(R.layout.activity_main);

<<<<<<< HEAD

        ChatFragment chatFragment =
                (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);

        if (chatFragment == null) {
            chatFragment = ChatFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frag, chatFragment);
            transaction.commit();
        }

        // init Repository
        mRepository = new Repository();
        mRepository.setViewListener(chatFragment);
        chatFragment.setRepository(mRepository);
=======
        mUsername = ANONYMOUS;

        // init firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mStorageReference = mFirebaseStorage.getReference().child("chat_photos");
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity

        /*//TODO: remove UI elements
        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Repository.DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                FriendlyMessage friendlyMessage =
                        new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);

<<<<<<< HEAD
                mRepository.pushMessage(textMsg);
=======
                mDatabaseReference.push().setValue(friendlyMessage);
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity

                // Clear input box
                mMessageEditText.setText("");
            }
<<<<<<< HEAD
        });*/
=======
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // check if user is authenticated if not show screen of login
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Sign in", Toast.LENGTH_SHORT).show();
                    OnSignInInitialize(user.getDisplayName());
                }
                else {
                    // user sign out -> use Firebase UI
                    OnSignetOutCleanUp();

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                        AuthUI.EMAIL_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        FirebaseRemoteConfigSettings config = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(config);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(FRIENDLY_MSG_LENGTH_KEY, DEFAULT_MSG_LENGTH_LIMIT);

        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        fetchConfig();
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
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGTH_KEY);
        mMessageEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, FRIENDLY_MSG_LENGTH_KEY + "=" + friendly_msg_length);
    }


    private void OnSignInInitialize(String username) {
        mUsername = username;
        this.AttachDatabaseReadListener();
    }

    private void OnSignetOutCleanUp() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        this.DetachDatabaseReadListener();
    }

    private void AttachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // match the field get in json object (DataSnapshot)
                    FriendlyMessage newFriendlyMsg = dataSnapshot.getValue(FriendlyMessage.class);
                    // update view with adapter
                    mMessageAdapter.add(newFriendlyMsg);
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

    private void DetachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
        mChildEventListener = null;
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // sign out for firebase authentication
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
<<<<<<< HEAD
        // TODO: move to fragment
        /*mRepository.detachAuthStateListener();
        mMessageAdapter.clear();*/
=======
        // detach
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        this.DetachDatabaseReadListener();
        mMessageAdapter.clear();
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: move to fragment
        //mRepository.attachAuthStateListener();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in cancel", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectImageUrl = data.getData();
<<<<<<< HEAD
            mRepository.pushImage(selectImageUrl);
=======

            // get reference to storage
            StorageReference photRef = mStorageReference.child(selectImageUrl.getLastPathSegment());

            // upload file to fb
            photRef.putFile(selectImageUrl)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FriendlyMessage msg = new FriendlyMessage(null, mUsername, downloadUrl.toString());

                    // give an unique id and push to database
                    mDatabaseReference.push().setValue(msg);
                }
            });
>>>>>>> parent of 899a4de... Extract firebase database and storage from MainActivity
        }
    }*/


    // TODO: remove implements RepositoryListener in MainActivity
    /*@Override
    public void clearAllMessage() {
        mMessageAdapter.clear();
    }

    @Override
    public void newMessage(FriendlyMessage msg) {
        mMessageAdapter.add(msg);
    }

    @Override
    public void notifyUser(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestAuthentication() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void updateMsgLength(int length) {
        mMessageEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length)});
    }*/
}
