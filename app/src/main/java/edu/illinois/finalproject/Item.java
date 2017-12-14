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
 * Created by dillon on 12/6/17.
 */

public class Item implements Parcelable {
    private String itemName;
    private String itemId;
    private String itemDescription;
    private Double itemPrice;
    private UserPointer sellerPointer;
    private String datePosted;
    private ContactInfo contactInfo;
    private String imageUri;

    private final static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final static DatabaseReference ITEMS = DATABASE.getReference().child("items");

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    /**
     * Standard Constructor
     * @param itemName Item Name
     * @param itemDescription Item Long Description
     * @param itemPrice Item Price
     * @param sellerPointer Pointer to the Seller
     * @param datePosted Timestamp
     * @param contactInfo Contact Info for this item
     * @param imageUri Location of Item image
     */
    public Item(String itemName,
                String itemDescription,
                Double itemPrice,
                UserPointer sellerPointer,
                String datePosted,
                ContactInfo contactInfo,
                String imageUri) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.sellerPointer = sellerPointer;
        this.datePosted = datePosted;
        this.contactInfo = contactInfo;
        this.imageUri = imageUri;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public UserPointer getSellerPointer() {
        return sellerPointer;
    }

    public void setSellerPointer(UserPointer sellerPointer) {
        this.sellerPointer = sellerPointer;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * Useful static method to get all Items in the Database
     * @param callback The callback to receive the list of Items
     */
    public static void getAllItems(final AsyncTask<Item, Void, Void> callback) {
        ITEMS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> allItems = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    allItems.add(item.getValue(Item.class));
                }
                callback.execute(allItems.toArray(new Item[allItems.size()]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.cancel(false);
            }
        });
    }

    /**
     * Useful static method to find a certain item in the Database
     * @param itemId The itemId used to identify the Item
     * @param callback The callback to receive the list of Items
     */
    public static void findItem(final String itemId, final AsyncTask<Item, Void, Void> callback) {
        ITEMS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemObject : dataSnapshot.getChildren()) {
                    Item item = itemObject.getValue(Item.class);
                    if (item != null && item.getItemId().equals(itemId)) {
                        callback.execute(item);
                        return;
                    }
                }
                callback.execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(MainActivity.TAG, "findItem task was canceled");
                callback.cancel(false);
            }
        });
    }

    /**
     * Useful static method to create a new Item
     * @param newItem the new Item object
     * @param owningUser the User that owns the Item
     * @param callback The callback to receive the success status
     */
    public static void createItem(final Item newItem,
                                  final User owningUser,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        DatabaseReference dbRef = ITEMS.push();
        newItem.setItemId(dbRef.getKey());
        dbRef.setValue(newItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(MainActivity.TAG, "Created an Item!");
                owningUser.addItemPointer(newItem.getItemId());
                User.updateUser(owningUser, callback);
            }
        });
    }

    /**
     * Usful static method to update an Item
     * @param updatedItem The new Item object
     * @param callback The callback to receive the success status
     */
    public static void updateItem(Item updatedItem, final AsyncTask<Boolean, Void, Void> callback) {
        ITEMS.child(updatedItem.getItemId()).setValue(updatedItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(MainActivity.TAG, "Updated an Item!");
                callback.execute(task.isSuccessful());
            }
        });
    }

    /**
     * Usful static method to remove an Item
     * @param itemId The itemId to be deleted
     * @param userPointer The UserPointer that will be used to find the owning User to remove the
     *                   ItemPointer from
     * @param callback The callback to receive the success status
     */
    public static void removeItem(final String itemId,
                                  final UserPointer userPointer,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        ITEMS.child(itemId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(MainActivity.TAG, databaseError.toString());
                    callback.execute(false);
                } else {
                    Log.i(MainActivity.TAG, "Deleted an Item!");
                    userPointer.getRealUser(new RemoveItemPointer(itemId, callback));
                }
            }
        });
    }

    /**
     * A RemoveItemPointer is an AsyncTask useful to handle removing an ItemPointer from a User
     * The callback receives the success status of the operation
     */
    private static class RemoveItemPointer extends AsyncTask<User, Void, Void> {
        private String itemId;
        private AsyncTask<Boolean, Void, Void> callback;

        public RemoveItemPointer(String itemId, AsyncTask<Boolean, Void, Void> callback) {
            this.itemId = itemId;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(User... users) {
            if (users.length > 0) {
                User itemOwner = users[0];
                itemOwner.removeItemPointer(this.itemId);
                User.updateUser(itemOwner, callback);
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemName);
        dest.writeString(this.itemId);
        dest.writeString(this.itemDescription);
        dest.writeValue(this.itemPrice);
        dest.writeParcelable(this.sellerPointer, flags);
        dest.writeString(this.datePosted);
        dest.writeParcelable(this.contactInfo, flags);
        dest.writeString(this.imageUri);
    }

    protected Item(Parcel in) {
        this.itemName = in.readString();
        this.itemId = in.readString();
        this.itemDescription = in.readString();
        this.itemPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.sellerPointer = in.readParcelable(UserPointer.class.getClassLoader());
        this.datePosted = in.readString();
        this.contactInfo = in.readParcelable(ContactInfo.class.getClassLoader());
        this.imageUri = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}