package com.musicapi.java_music_api.music;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.OptimisticLockException;

@RestController
@RequestMapping("/music")
public class MusicController {
    private MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    // get all controller
    @GetMapping
    public List<Music> getAllSongs() {
        return this.musicService.getAllSongs();
    }

    // get id controller
    @GetMapping("/{id}")
    public Music getSong(@PathVariable UUID id) {
        try {
            return this.musicService.getSong(id);
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found", nse);
        }
    }

    // create song controller
    @PostMapping
    public ResponseEntity<Music> createSong(@RequestBody Music music) {
        try {
            return new ResponseEntity<Music>(musicService.createSong(music), HttpStatusCode.valueOf(201));
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data included", iae);
        }
    }

    // update song controller
    @PutMapping("/{id}")
    public Music updateSong(@PathVariable UUID id, @RequestBody Music music) {
        try {
            return this.musicService.updateSong(id, music);
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // delete song controller
    @DeleteMapping("/{id}")
    public void deleteSong(@PathVariable UUID id) {
        try {
            this.musicService.deleteSong(id);
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // high danceability controller
    @GetMapping("/highdanceability")
    public List<Music> getHighDanceabilitySongs() {
        return this.musicService.getHighDanceabilitySongs();
    }

    // low energy controller
    @GetMapping("/lowenergy")
    public List<Music> getLowEnergySongs() {
        return this.musicService.getLowEnergySongs();
    }

    // exception to handle incorrect indentity format
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = "The format of the IDENTIFIER you entered is not valid.";
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // exception to handle incorrect post data format
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleException(HttpMessageNotReadableException noe) {
        String error = "The format of your input data in the POST request is incorrect";
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // exception to handle data conflict
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> handleOptimisticLockException(OptimisticLockException ole) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflict: Retry, data has been modified by another action.");
    }

}
