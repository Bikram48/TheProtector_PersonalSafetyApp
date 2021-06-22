package com.example.theprotector.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanionInfo implements Parcelable {
    private String name;
    private String phone_number;

    public CompanionInfo() {
    }

    public CompanionInfo(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    protected CompanionInfo(Parcel in) {
        name = in.readString();
        phone_number = in.readString();
    }

    public static final Creator<CompanionInfo> CREATOR = new Creator<CompanionInfo>() {
        @Override
        public CompanionInfo createFromParcel(Parcel in) {
            return new CompanionInfo(in);
        }

        @Override
        public CompanionInfo[] newArray(int size) {
            return new CompanionInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phone_number);
    }
}
