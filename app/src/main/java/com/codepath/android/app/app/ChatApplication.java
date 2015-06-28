package com.codepath.android.app.app;

import android.app.Application;
import com.codepath.android.app.activities.ChatActivity;
import com.codepath.android.app.models.Message;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ChatApplication extends Application
{
    public static final String YOUR_APPLICATION_ID = "AERqqIXGvzH7Nmg45xa5T8zWRRjqT8UmbFQeeI";
    public static final String YOUR_CLIENT_KEY = "8bXPznF5eSLWq0sY9gTUrEF5BJlia7ltmLQFRh";
    @Override
    public void onCreate()
    {
        super.onCreate();
        // Register your parse models here
        ParseObject.registerSubclass(Message.class);

        Parse.enableLocalDatastore(this);

        //  initialization happens after all classes are registered

        Parse.initialize(this, "Vw7SJQXYop8GXyk1Ayomwdzhuyn8jPumhaAeZZXo", "KJfXjoJJtZmJ7qX82yGWHVknJHHO0T8cKjtIbt85");
        ParseUser.enableAutomaticUser();
    }

}
