package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dillon on 12/13/17.
 */

public class UserPointer implements Parcelable {
    private String userId;
    private User realUser;

    public UserPointer() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public UserPointer(String userId) {
        this.userId = userId;
        this.realUser = null;
    }

    public String getUserId() {
        return userId;
    }

    public User getRealUser() {
        if (realUser == null) {
            // TODO: This should be a FireBase query to find the real User
            // TODO: Then cache the User Object
        }
        return realUser;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPointer that = (UserPointer) o;

        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeParcelable(this.realUser, flags);
    }

    protected UserPointer(Parcel in) {
        this.userId = in.readString();
        this.realUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<UserPointer> CREATOR = new Creator<UserPointer>() {
        @Override
        public UserPointer createFromParcel(Parcel source) {
            return new UserPointer(source);
        }

        @Override
        public UserPointer[] newArray(int size) {
            return new UserPointer[size];
        }
    };
}
