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
    private String firebaseId;
    private String userId;
    private String displayName;
    private ArrayList<ItemPointer> itemPointers;

    private final static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final static DatabaseReference USERS = DATABASE.getReference().child("users");

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    /**
     * Standrd constructor
     * @param firebaseId The FireBase generated account UUID
     * @param displayName The current Display Name
     * @param itemPointers A typically empty list of initial ItemPointers for the User
     */
    public User(String firebaseId, String displayName, ArrayList<ItemPointer> itemPointers) {
        this.firebaseId = firebaseId;
        this.displayName = displayName;
        this.itemPointers = itemPointers;
    }

    /**
     * @return The FireBase generated object UUID
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The FireBase account UUID
     */
    public String getFirebaseId() {
        return firebaseId;
    }

    /**
     * @return The current Display Name
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return An ArrayList of ItemPointers
     */
    public ArrayList<ItemPointer> getItemPointers() {
        return itemPointers;
    }

    /**
     * Useful to add a new ItemPointer. Will do nothing iof the ItemPointer exists
     * @param itemId The itemId use to create a new ItemPointer
     */
    public void addItemPointer(String itemId) {
        if (this.itemPointers == null) {
            this.itemPointers = new ArrayList<ItemPointer>(1);
        }
        for (ItemPointer itemPointer : this.itemPointers) {
            if (itemPointer.getItemId().equals(itemId)) {
                return; // Don't add duplicates
            }
        }
        this.itemPointers.add(new ItemPointer(itemId));
    }

    /**
     * Useful to remove an ItemPointer from a User. Note that an update of the User object to FireBase
     * must be completed externally
     * @param itemId The itemId used to represent the ItemPointer
     */
    public void removeItemPointer(String itemId) {
        for (ItemPointer itemPointer : this.itemPointers) {
            if (itemPointer.getItemId().equals(itemId)) {
                this.itemPointers.remove(itemPointer);
                break;
            }
        }
    }

    /**
     * Useful static method to launch a FireBase search for a certain User
     * The found User (if one exists) is given to the callback
     * One of userId or firebaseId must be given
     * @param userId The FireBase generated object UUID
     * @param firebaseId The FireBase account UUID
     * @param callback The callback to receive the User object or nothing
     */
    public static void findUser(final String userId,
                                final String firebaseId,
                                final AsyncTask<User, Void, Void> callback) {
        USERS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userObject : dataSnapshot.getChildren()) {
                    User user = userObject.getValue(User.class);
                    if (user != null) {
                        if (user.getUserId().equals(userId)
                                || user.getFirebaseId().equals(firebaseId)) {
                            callback.execute(user);
                            return;
                        }
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

    /**
     * Useful static method to launch the creation of a User
     * Note that after the method compeletes, the newUser's userId will be overwritten with the
     * appropriate value
     * @param newUser The newUser object
     * @param callback The callback indicating success
     */
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

    /**
     * Useful static method to launch the update of a User
     * @param updatedUser The updated User object
     * @param callback The callback indicating success
     */
    public static void updateUser(User updatedUser,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        USERS.child(updatedUser.getUserId()).setValue(updatedUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.execute(task.isSuccessful());
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firebaseId);
        dest.writeString(this.userId);
        dest.writeString(this.displayName);
        dest.writeTypedList(this.itemPointers);
    }

    protected User(Parcel in) {
        this.firebaseId = in.readString();
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