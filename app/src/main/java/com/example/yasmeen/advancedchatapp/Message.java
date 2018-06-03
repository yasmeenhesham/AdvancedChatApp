package com.example.yasmeen.advancedchatapp;

public class Message {
    private String message;
    String type;
    private boolean seen;
    long time;
    private  String from;

    public Message(String message, String type, boolean seen, long time, String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from = from;
    }

    public void setFrom(String from) {

        this.from = from;
    }

    public String getFrom() {

        return from;
    }

    public Message(){}

    public Message(String message ,boolean seen, long time,  String type ) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isSeen() {
        return seen;
    }

    public long getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
