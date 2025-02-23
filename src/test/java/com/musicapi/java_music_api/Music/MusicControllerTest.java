package com.musicapi.java_music_api.Music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.musicapi.java_music_api.JavaMusicApiApplication;
import com.musicapi.java_music_api.music.Music;
import com.musicapi.java_music_api.music.MusicService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JavaMusicApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MusicControllerTest {

    @LocalServerPort
    private int port;

    private URI baseURI;

    @Autowired
    private TestRestTemplate restTemplate;

    private List<Music> defaultSongs = new ArrayList<>() {
        {
            add(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                    "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                    230, 120, 85, 90));
            add(new Music("Nothing New", "Charlotte Day Wilson", 2021, "R&B, Soul",
                    "A reflective, soulful track with emotional lyrics and smooth instrumentation.", 210, 85, 60, 70));
            add(new Music("Wuthering Heights", "Kate Bush", 1978, "Art Rock",
                    "A dramatic and ethereal track with Kate Bush's unique vocals and complex instrumentation.", 240,
                    130, 85, 70));
        }
    };

    @MockBean
    private MusicService musicService;

    @BeforeEach
    void setUp() throws RuntimeException {
        this.baseURI = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("music")
                .build()
                .toUri();

        when(musicService.getAllSongs()).thenReturn(defaultSongs);
    }

    @Test
    @Description("POST /music creates new Song")
    void createSong() {
        // Arrange
        Music music = createNewSong();

        when(musicService.createSong(any(Music.class))).thenAnswer(invocation -> setId(invocation.getArgument(0)));

        // Act
        ResponseEntity<Music> response = restTemplate.postForEntity(baseURI.toString(), music, Music.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        verify(musicService).createSong(any(Music.class));
    }

    @Test
    @Description("GET /music returns all songs")
    void getAllSongs() throws URISyntaxException {
        // Act: sending the http get request and receives a response
        ResponseEntity<List<Music>> response = restTemplate.exchange(baseURI, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Music>>() {
                });
        List<Music> responseSongs = response.getBody();

        // Assert
        // checks that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // checks that the response is not null
        assertNotNull(responseSongs);
        // checks that the size of the defaultSongs matches the responseSongs size
        assertEquals(defaultSongs.size(), responseSongs.size());
        // checks that the getAllSongs was implemented
        verify(musicService).getAllSongs();

    }

    @Test
    @Description("GET /music/{id} returns id associated song")
    void getSong() {
        // Arrange
        // selects random default song
        Music music = selectRandomSong();
        // adds getSong route to the base URL
        URI endpoint = getEndpoint(music);
        // imitate database behaviour, retuns song
        when(musicService.getSong(any(UUID.class))).thenReturn(music);

        // Act
        // sends GET request to getSong route and stores response
        ResponseEntity<Music> response = restTemplate.getForEntity(endpoint, Music.class);

        // Assert
        // checks that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // checks that the response is not null
        assertNotNull(response.getBody());
        // checks that the response id matches the Music instance id
        assertEquals(music.getId(), response.getBody().getId());
        // checks that getSong was implemented
        verify(musicService).getSong(music.getId());
    }

    private Music createNewSong() {
        return setId(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                230, 120, 85, 90));
    }

    private static Music setId(Music music) {
        // creates random UUID
        ReflectionTestUtils.setField(music, "id", UUID.randomUUID());
        return music;
    }

    private Music selectRandomSong() {
        // allocate random id to randomly chosen default song
        int randomIndex = new Random().nextInt(defaultSongs.size());

        return setId(defaultSongs.get(randomIndex));
    }

    private URI getEndpoint(Music music) {
        // creates endpoint with song id
        return appendPath(baseURI, music.getId().toString());
    }

    private URI appendPath(URI uri, String path) {
        // append end point to the base URI
        return UriComponentsBuilder.fromUri(uri).pathSegment(path).build().encode().toUri();
    }

}
