package com.example.sporify;

public class Song {

    private int rawId;
    private int coverId;
    private String title;
    private String artist;

    public Song(int rawId, int coverId, String title, String artist) {
        // Constructor del modelo Song. Asigna identificadores del audio, portada
        // y metadatos básicos para uso en reproductor y vistas UI.
        this.rawId = rawId;
        this.coverId = coverId;
        this.title = title;
        this.artist = artist;
    }

    // Retorna ID del recurso de audio.
    public int getRawId() { return rawId; }

    // Retorna ID del recurso de imagen de portada.
    public int getCoverId() { return coverId; }

    // Obtiene el título de la canción.
    public String getTitle() { return title; }

    // Obtiene el nombre del artista.
    public String getArtist() { return artist; }
}
