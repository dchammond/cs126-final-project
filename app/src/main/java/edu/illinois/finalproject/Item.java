package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dillon on 12/6/17.
 */

public class Item implements Parcelable {
    private final String name;
    private final String description;
    private final Integer price;
    private final String seller;
    private final String imageUrl;


    public Item(String name, String description, Integer price, String seller, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.seller = seller;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public String getSeller() {
        return seller;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.price);
        dest.writeString(this.seller);
        dest.writeString(this.imageUrl);
    }

    protected Item(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readInt();
        this.seller = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
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
