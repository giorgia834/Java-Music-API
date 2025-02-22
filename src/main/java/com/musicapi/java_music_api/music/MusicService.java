package com.musicapi.java_music_api.music;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class MusicService {
    public final MusicRepository musicRepository;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> getAllSongs() {
        return this.musicRepository.findAll();
    }

    public Music getSong(UUID id) throws NoSuchElementException {
        try {
            return this.musicRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException nse) {
            throw nse;
        }
    }

    public Music createSong(Music music) throws IllegalArgumentException, OptimisticLockingFailureException {
        this.musicRepository.save(music);
        return music;
    }

    public Music updateSong(UUID id, Music updatedMusic) throws NoSuchElementException {
        Music music = musicRepository.findById(id).orElseThrow();

        music.setSong(updatedMusic.getSong());
        music.setArtist(updatedMusic.getArtist());
        music.setYear(updatedMusic.getYear());
        music.setGenre(updatedMusic.getGenre());
        music.setDescription(updatedMusic.getDescription());
        music.setDurationSec(updatedMusic.getDurationSec());
        music.setBpm(updatedMusic.getBpm());
        music.setEnergy(updatedMusic.getEnergy());
        music.setDanceability(updatedMusic.getDanceability());

        return musicRepository.save(music);

    }

    public void deleteSong(UUID id) throws NoSuchElementException {
        if (musicRepository.findById(id).isPresent()) {
            musicRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<Music> getHighDanceabilitySongs() {
        return this.musicRepository.findByHighDanceability();
    }

    public List<Music> getLowEnergySongs() {
        return this.musicRepository.findByLowEnergy();
    }

}
