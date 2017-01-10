package com.avinash.homework7;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by jduvvu on 11/22/16.
 */
public class Message implements Parcelable,Comparable<Message> {

    String status, senderName, receiverName, text, image, date, msgID;


    protected Message(Parcel in) {
        status = in.readString();
        senderName = in.readString();
        receiverName = in.readString();
        text = in.readString();
        image = in.readString();
        date = in.readString();
        msgID = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public Message() {
    
    }

    public Message(String status, String senderName, String receiverName, String text, String image, String date, String msgID) {
    
        this.status = status;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.text = text;
        this.image = image;
        this.date = date;
        this.msgID = msgID;
    }

    @Override
    public int compareTo(Message message) {
        Date d1 = new Date(this.date);
        Date d2 = new Date(message.date);

        if (d1.compareTo(d2)>0)
        {
            return 1;
        }
        else if(d1.compareTo(d2)<0)
        {
            return -1;
        }
        return 0;
    }
    

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(senderName);
        parcel.writeString(receiverName);
        parcel.writeString(text);
        parcel.writeString(image);
        parcel.writeString(date);
        parcel.writeString(msgID);
    }
}
