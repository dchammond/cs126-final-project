package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dillon on 12/13/17.
 */

public class ItemPointer implements Parcelable {
    private String itemId;

    public ItemPointer() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public ItemPointer(String itemId) {
        this.itemId = itemId;
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
    }

    protected ItemPointer(Parcel in) {
        this.itemId = in.readString();
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
