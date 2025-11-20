package com.example.sporify;

public class Song {
    private int rawId;
    private int coverId;
    private String title;
    private String artist;

    public Song(int rawId, int coverId, String title, String artist) {
        this.rawId = rawId;
        this.coverId = coverId;
        this.title = title;
        this.artist = artist;
    }

    public int getRawId() { return rawId; }
    public int getCoverId() { return coverId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
}
