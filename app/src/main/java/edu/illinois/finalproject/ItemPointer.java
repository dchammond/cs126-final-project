package edu.illinois.finalproject;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dillon on 12/13/17.
 */

public class ItemPointer implements Parcelable {
    private String itemId;
    private Item realItem;

    public ItemPointer() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public ItemPointer(String itemId) {
        this.itemId = itemId;
    }

    public void getRealItem(AsyncTask<Item, Void, Void> callback) {
        if (this.realItem == null) {
            Item.findItem(this.itemId, new findItemTask(this, callback));
        } else {
            callback.execute(this.realItem);
        }
    }

    private static class findItemTask extends AsyncTask<Item, Void, Void> {
        private ItemPointer itemPointer;
        private AsyncTask<Item, Void, Void> callback;

        public findItemTask(ItemPointer itemPointer, AsyncTask<Item, Void, Void> callback) {
            super();
            this.itemPointer = itemPointer;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Item... items) {
            if (items.length > 0) {
                Item foundItem = items[0];
                this.itemPointer.realItem = foundItem; // Cache the Item for future calls
                this.callback.execute(foundItem);
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            this.callback.cancel(false);
        }
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPointer that = (ItemPointer) o;

        return itemId != null ? itemId.equals(that.itemId) : that.itemId == null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeParcelable(this.realItem, flags);
    }

    protected ItemPointer(Parcel in) {
        this.itemId = in.readString();
        this.realItem = in.readParcelable(Item.class.getClassLoader());
    }

    public static final Creator<ItemPointer> CREATOR = new Creator<ItemPointer>() {
        @Override
        public ItemPointer createFromParcel(Parcel source) {
            return new ItemPointer(source);
        }

        @Override
        public ItemPointer[] newArray(int size) {
            return new ItemPointer[size];
        }
    };
}
