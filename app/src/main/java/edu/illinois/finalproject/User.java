package edu.illinois.finalproject;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by dillon on 12/13/17.
 */

public class User implements Parcelable {
    private String userId;
    private String displayName;
    private ArrayList<ItemPointer> itemPointers;

    private final static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final static DatabaseReference USERS = DATABASE.getReference().child("users");

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public User(String displayName, ArrayList<ItemPointer> itemPointers) {
        this.displayName = displayName;
        this.itemPointers = itemPointers;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<ItemPointer> getItemPointers() {
        return itemPointers;
    }

    public void setItemPointers(ArrayList<ItemPointer> itemPointers) {
        this.itemPointers = itemPointers;
    }

    public UserPointer generateUserPointer() {
        return new UserPointer(this.userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != null ? !userId.equals(user.userId) : user.userId != null) return false;
        if (displayName != null ? !displayName.equals(user.displayName) : user.displayName != null)
            return false;
        return itemPointers != null ?
                itemPointers.equals(user.itemPointers) : user.itemPointers == null;
    }

    public static void findUser(final String userId, final AsyncTask<User, Void, Void> callback) {
        USERS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userObject : dataSnapshot.getChildren()) {
                    User user = userObject.getValue(User.class);
                    if (user != null && user.getUserId().equals(userId)) {
                        callback.execute(user);
                        return;
                    }
                }
                callback.execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(MainActivity.TAG, "findUser task was canceled");
                callback.cancel(false);
            }
        });
    }

    public static void createUser(User newUser,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        DatabaseReference dbRef = USERS.push();
        newUser.setUserId(dbRef.getKey());
        dbRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.execute(task.isSuccessful());
            }
        });
    }

    public static void updateUser(User updatedUser,
                                  final AsyncTask<Boolean, Void, Void> callback) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.displayName);
        dest.writeTypedList(this.itemPointers);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.displayName = in.readString();
        this.itemPointers = in.createTypedArrayList(ItemPointer.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}