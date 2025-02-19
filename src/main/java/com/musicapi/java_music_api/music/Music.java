package com.musicapi.java_music_api.music;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "music")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String song;
    private String artist;
    private Integer year;
    private String genre;
    private String description;
    @Column(name = "duration_sec")
    private Integer duration_sec;
    private Integer bpm;
    private Integer energy;
    private Integer danceability;

    public Music(String song, String artist, Integer year, String genre, String description, Integer duration_sec,
            Integer bpm,
            Integer energy, Integer danceability) {
        this.song = song;
        this.artist = artist;
        this.year = year;
        this.genre = genre;
        this.description = description;
        this.duration_sec = duration_sec;
        this.bpm = bpm;
        this.energy = energy;
        this.danceability = danceability;
    }

    // initialise class
    public Music() {
        this("Coconut", "Manu Dibango", 1996, "Afro-Jazz, Funk",
                "A groovy Afro-jazz fusion track with rich saxophone melodies and vibrant rhythms.", 105, 250, 80, 65);
    }

    public UUID getId() {
        return this.id;
    }

    public String getSong() {
        return this.song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationSec() {
        return this.duration_sec;
    }

    public void setDurationSec(Integer duration_sec) {
        this.duration_sec = duration_sec;
    }

    public Integer getBpm() {
        return this.bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public Integer getEnergy() {
        return this.energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public Integer getDanceability() {
        return this.danceability;
    }

    public void setDanceability(Integer danceability) {
        this.danceability = danceability;
    }

}
