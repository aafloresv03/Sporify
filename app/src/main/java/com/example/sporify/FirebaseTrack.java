package com.example.sporify;

public class FirebaseTrack {

    private String trackId;
    private String title;
    private String artist;
    private String album;
    private String coverUrl;
    private String previewUrl;

    public FirebaseTrack() {
    }

    public FirebaseTrack(String trackId, String title, String artist, String album, String coverUrl, String previewUrl) {
        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.coverUrl = coverUrl;
        this.previewUrl = previewUrl;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}