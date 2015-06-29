package com.codepath.android.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.codepath.android.app.Constants;
import com.codepath.android.app.activities.ChatActivity;
import com.codepath.android.app.models.Message;
import com.codepath.android.app.models.MessageParcelable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ChatService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ChatService() {
        super("ChatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int maxMsgs = intent.getIntExtra("MaxMsgs", 100);
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(maxMsgs);
        query.orderByAscending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    List<MessageParcelable> messageParcelables = getMessageParcelableList(messages);
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ChatActivity.ResponseReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putParcelableArrayListExtra(Constants.INTENT_MSGS_EXTRA, (ArrayList)messageParcelables);
                    sendBroadcast(broadcastIntent);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    private List<MessageParcelable> getMessageParcelableList(List<Message> messages) {
        List<MessageParcelable> messageParcelables = new ArrayList<>();
        if(messages != null)
        {
            for(Message message : messages)
            {
                MessageParcelable msg = new MessageParcelable(message.getUserId(), message.getBody());
                messageParcelables.add(msg);
            }
        }
        return messageParcelables;
    }
}
