package com.google.firebase.udacity.friendlychat;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eduard on 14/04/2017.
 */

public class ChatFragment extends Fragment implements IView {

    public static final String TAG = "ChatFragment";

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    //TODO remove
    //private IRepository mRepository;
    private IPresenter mPresenter;

    public ChatFragment() { }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void setPresenter(@NonNull IPresenter presenter) {
        this.mPresenter = presenter;
    }

    //TODO remove
    //public void setRepository(@NonNull IRepository repository) {this.mRepository = repository; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        Context context = getActivity().getApplicationContext();
        mMessageAdapter = new MessageAdapter(context, R.layout.item_message, friendlyMessages);
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO remove
        //mRepository.detachAuthStateListener();
        mPresenter.pause();
        mMessageAdapter.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO remove
        //mRepository.attachAuthStateListener();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.chat_frag, container, false);

        // Initialize references to views
        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        mMessageListView = (ListView) root.findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) root.findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) root.findViewById(R.id.messageEditText);
        mSendButton = (Button) root.findViewById(R.id.sendButton);

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable editable) { }
        });
        //TODO: remove static reference to Repository
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Repository.DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String textMsg = mMessageEditText.getText().toString();

                //TODO remove
                //mRepository.pushMessage(textMsg);
                mPresenter.addNewMessage(textMsg);

                // Clear input box
                mMessageEditText.setText("");
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == MainActivity.RESULT_OK) {
                Toast.makeText(getActivity(), "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == MainActivity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Signed in cancel", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
        else if (requestCode == RC_PHOTO_PICKER && resultCode == MainActivity.RESULT_OK) {
            Uri selectImageUrl = data.getData();
            // TODO: remove
            //mRepository.pushImage(selectImageUrl);
            mPresenter.addNewMessage(selectImageUrl);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // sign out for firebase authentication
                AuthUI.getInstance().signOut(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // IView methods
    @Override
    public void clearAllMessage() {
        mMessageAdapter.clear();
    }

    @Override
    public void newMessage(FriendlyMessage msg) {
        mMessageAdapter.add(msg);
    }

    @Override
    public void notifyUser(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
    }
}
