package com.codepath.android.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageParcelable implements Parcelable {

    private String userId;
    private String body;

    public MessageParcelable(final String userId, final String body)
    {
        this.userId = userId;
        this.body = body;
    }

    public MessageParcelable(Parcel in)
    {
        this.userId = in.readString();
        this.body = in.readString();
    }

    public String getUserId() {
        return userId;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(body);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MessageParcelable createFromParcel(Parcel in) {
            return new MessageParcelable(in);
        }

        @Override
        public MessageParcelable[] newArray(int size) {
            return new MessageParcelable[size];
        }
    };
}
