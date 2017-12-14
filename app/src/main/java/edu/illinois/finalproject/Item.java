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

    public static void findItem(final String userId, final AsyncTask<Item, Void, Void> callback) {
        ITEMS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemObject : dataSnapshot.getChildren()) {
                    Item item = itemObject.getValue(Item.class);
                    if (item != null && item.getItemId().equals(userId)) {
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

    public static void createItem(Item newItem,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        DatabaseReference dbRef = ITEMS.push();
        newItem.setItemId(dbRef.getKey());
        dbRef.setValue(newItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.execute(task.isSuccessful());
            }
        });
    }

    public static void removeItem(final String itemId,
                                  final UserPointer userPointer,
                                  final AsyncTask<Boolean, Void, Void> callback) {
        ITEMS.child(itemId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(MainActivity.TAG, databaseError.toString());
                    userPointer.getRealUser(new removeItemPointer(itemId, callback));
                } else {
                    callback.execute(false);
                }
            }
        });
    }

    private static class removeItemPointer extends AsyncTask<User, Void, Void> {
        private String itemId;
        private AsyncTask<Boolean, Void, Void> callback;

        public removeItemPointer(String itemId, AsyncTask<Boolean, Void, Void> callback) {
            this.itemId = itemId;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(User... users) {
            if (users.length > 0) {
                User itemOwner = users[0];
                ArrayList<ItemPointer> itemPointers = itemOwner.getItemPointers();
                for (ItemPointer itemPointer : itemPointers) {
                    if (itemPointer.getItemId().equals(this.itemId)) {
                        itemPointers.remove(itemPointer);
                        break;
                    }
                }
                itemOwner.setItemPointers(itemPointers);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (itemName != null ? !itemName.equals(item.itemName) : item.itemName != null)
            return false;
        if (itemId != null ? !itemId.equals(item.itemId) : item.itemId != null) return false;
        if (itemDescription != null ?
                !itemDescription.equals(item.itemDescription) : item.itemDescription != null)
            return false;
        if (itemPrice != null ? !itemPrice.equals(item.itemPrice) : item.itemPrice != null)
            return false;
        if (sellerPointer != null ?
                !sellerPointer.equals(item.sellerPointer) : item.sellerPointer != null)
            return false;
        if (datePosted != null ? !datePosted.equals(item.datePosted) : item.datePosted != null)
            return false;
        if (contactInfo != null ? !contactInfo.equals(item.contactInfo) : item.contactInfo != null)
            return false;
        return imageUri != null ? imageUri.equals(item.imageUri) : item.imageUri == null;
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