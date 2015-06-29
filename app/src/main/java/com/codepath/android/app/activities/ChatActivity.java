package com.codepath.android.app.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.codepath.android.app.Constants;
import com.codepath.android.app.R;
import com.codepath.android.app.adapters.ChatRecyclerAdapter;
import com.codepath.android.app.models.Message;
import com.codepath.android.app.models.MessageParcelable;
import com.codepath.android.app.services.ChatService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.*;
import com.parse.entity.mime.content.StringBody;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getName();
    private static String sUserId;

    public static final String USER_ID_KEY = "userId";

    private EditText etMessage;
    private Button btSend;
    private RecyclerView rvChat;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<MessageParcelable> mMessages;
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();
    private ResponseReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_chat);
        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

        // create intent filter and register the broadcast receiver for the chat service
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        // Run the runnable object defined every 100ms
        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister broadcast receiver to prevent memory leaks
        unregisterReceiver(receiver);
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    private void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);

        // Setting the LayoutManager.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        //Set LayoutManager to RecyclerView
        rvChat.setLayoutManager(layoutManager);

        // initialize the adapter
        mMessages = new ArrayList<MessageParcelable>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(mMessages, sUserId);
        // attach the adapter to the RecyclerView
        rvChat.setAdapter(chatRecyclerAdapter);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.put(USER_ID_KEY, sUserId);
                message.put("body", data);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                etMessage.setText("");
            }
        });


    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Anonymous login failed: " + e.toString());
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    private void refreshMessages() {
        // start intent service
        Intent msgIntent = new Intent(this, ChatService.class);
        msgIntent.putExtra(Constants.MAX_MSGS, MAX_CHAT_MESSAGES_TO_SHOW);
        startService(msgIntent);
    }

    // Broadcast receiver that will receive data from service
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.codepath.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            List<MessageParcelable> messages = intent.getParcelableArrayListExtra(Constants.INTENT_MSGS_EXTRA);
            chatRecyclerAdapter.updateList(messages);
        }
    }
}