package com.musicapi.java_music_api.music;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface MusicRepository extends ListCrudRepository<Music, UUID> {
    // Get high energy music
    @Query(value = "SELECT * FROM music ORDER BY danceability DESC LIMIT 15", nativeQuery = true)
    List<Music> findByHighDanceability();
}
