package com.utkarsh.crescendo;

public class Song {
    private String title;
    private int imageResourceId;
    private int audioResourceId;

    public Song(String title, int imageResourceId, int audioResourceId) {
        this.title = title;
        this.imageResourceId = imageResourceId;
        this.audioResourceId = audioResourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getAudioResourceId() {
        return audioResourceId;
    }
}
