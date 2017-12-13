package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dillon on 12/13/17.
 */

public class ContactInfo implements Parcelable {
    private String email;
    private String phoneNumber;

    public ContactInfo() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public ContactInfo(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return phoneNumber != null ? phoneNumber.equals(that.phoneNumber) : that.phoneNumber == null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
    }

    protected ContactInfo(Parcel in) {
        this.email = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel source) {
            return new ContactInfo(source);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };
}
