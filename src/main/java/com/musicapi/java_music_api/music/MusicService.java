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

}
