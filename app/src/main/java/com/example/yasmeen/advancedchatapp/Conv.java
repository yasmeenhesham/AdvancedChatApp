package com.example.yasmeen.advancedchatapp;

public class Conv {
    private boolean seen;
    private long timestamp;

    public boolean getSeen() {
        return seen;
    }

    public Conv() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Conv(boolean seen, long timestamp) {

        this.seen = seen;
        this.timestamp = timestamp;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
