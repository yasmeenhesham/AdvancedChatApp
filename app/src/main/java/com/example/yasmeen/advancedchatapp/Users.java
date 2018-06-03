package com.example.yasmeen.advancedchatapp;

import java.io.Serializable;

public class Users implements Serializable {
    public String name;
    public String image;
    public String status;
    public String uid;
    public String online;

    public Users()
    {

    }
    public Users(String name , String image , String status)
    {
        this.name = name;
        this.image = image;
        this.status = status;
    }
    public Users(String name , String image , String status , String uid)
    {
        this.name = name;
        this.image = image;
        this.status = status;
        this.uid = uid;
    }
    public Users(String name , String image , String status , String uid, String online)
    {
        this.name = name;
        this.image = image;
        this.status = status;
        this.uid = uid;
        this.online = online;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
