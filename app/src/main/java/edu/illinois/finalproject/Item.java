package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

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

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public Item(String itemName,
                String itemId,
                String itemDescription,
                Double itemPrice,
                UserPointer sellerPointer,
                String datePosted,
                ContactInfo contactInfo,
                String imageUri) {
        this.itemName = itemName;
        this.itemId = itemId;
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