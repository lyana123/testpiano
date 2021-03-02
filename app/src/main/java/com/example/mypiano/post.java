package com.example.mypiano;

import java.io.Serializable;

public class post implements Serializable {
    private int ID;
    private String title;
    private String songContext;
    private String songId;
    private String imageId;

    public post(){    }

    public post(int ID, String title, String songContext, String songId, String imageId) {
        this.ID=ID;
        this.title = title;
        this.songContext = songContext;
        this.songId = songId;
        this.imageId = imageId;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getSongContext() {
        return songContext;
    }

    public String getSongId() {
        return songId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSongContext(String songContext) {
        this.songContext = songContext;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}