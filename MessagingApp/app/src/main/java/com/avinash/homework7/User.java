package com.avinash.homework7;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jduvvu on 11/19/16.
 */
public class User implements Parcelable {

    String firstname,lastname,email,gender,imageURL;

    public User() {
    }

    public User(String firstname, String lastname, String email, String gender, String imageURL) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.imageURL = imageURL;
    }

    protected User(Parcel in) {
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        gender = in.readString();
        imageURL = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(email);
        parcel.writeString(gender);
        parcel.writeString(imageURL);
    }
}
