package com.example.hackfrenzy;

public class Chat_Model {
    boolean isSeen;
    String date,message,receiver,sender,time;

    public Chat_Model() {
    }

    public Chat_Model(String date, boolean isSeen, String message, String receiver, String sender, String time) {
        this.isSeen = isSeen;
        this.date = date;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
